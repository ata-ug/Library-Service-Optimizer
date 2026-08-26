package algorithms;

import library.model.ServiceRequest;

import java.util.Comparator;
import java.util.List;

/**
 * Custom Sorting Engine for Balme Library Operations Optimizer (Module M4).
 * Implements Selection Sort, Insertion Sort, Merge Sort, and Quicksort from scratch
 * without relying on java.util.Arrays.sort or java.util.Collections.sort.
 */
public class SortingEngine {

    public enum AlgorithmType {
        SELECTION_SORT("Selection Sort", "O(N^2)", false, "O(1)"),
        INSERTION_SORT("Insertion Sort", "O(N^2)", true, "O(1)"),
        MERGE_SORT("Merge Sort", "O(N log N)", true, "O(N)"),
        QUICKSORT("Quicksort", "O(N log N)", false, "O(log N)");

        private final String displayName;
        private final String timeComplexity;
        private final boolean stable;
        private final String spaceComplexity;

        AlgorithmType(String displayName, String timeComplexity, boolean stable, String spaceComplexity) {
            this.displayName = displayName;
            this.timeComplexity = timeComplexity;
            this.stable = stable;
            this.spaceComplexity = spaceComplexity;
        }

        public String getDisplayName() { return displayName; }
        public String getTimeComplexity() { return timeComplexity; }
        public boolean isStable() { return stable; }
        public String getSpaceComplexity() { return spaceComplexity; }
    }

    public enum SortCriteria {
        DEADLINE("Deadline"),
        SUBMIT_TIME("Submit Time"),
        URGENCY("Urgency Score");

        private final String displayName;

        SortCriteria(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }

    /** Metrics data class tracking empirical sorting metrics. */
    public static class SortMetrics {
        private final String algorithmName;
        private final long comparisons;
        private final long swaps;
        private final long timeNs;
        private final boolean stable;
        private final String spaceComplexity;

        public SortMetrics(String algorithmName, long comparisons, long swaps, long timeNs, boolean stable, String spaceComplexity) {
            this.algorithmName = algorithmName;
            this.comparisons = comparisons;
            this.swaps = swaps;
            this.timeNs = timeNs;
            this.stable = stable;
            this.spaceComplexity = spaceComplexity;
        }

        public String getAlgorithmName() { return algorithmName; }
        public long getComparisons() { return comparisons; }
        public long getSwaps() { return swaps; }
        public long getTimeNs() { return timeNs; }
        public double getTimeMs() { return timeNs / 1_000_000.0; }
        public boolean isStable() { return stable; }
        public String getSpaceComplexity() { return spaceComplexity; }

        @Override
        public String toString() {
            return String.format("%s: %d comparisons, %d swaps/copies, %.3f ms (Stable: %b, Space: %s)",
                    algorithmName, comparisons, swaps, getTimeMs(), stable, spaceComplexity);
        }
    }

    /* -----------------------------------------------------------------
     * ServiceRequest Comparators
     * ----------------------------------------------------------------- */

    /** Comparator for deadline. Null deadlines are placed after non-null deadlines. */
    public static final Comparator<ServiceRequest> DEADLINE_COMPARATOR = (r1, r2) -> {
        if (r1 == r2) return 0;
        if (r1 == null) return 1;
        if (r2 == null) return -1;
        if (r1.deadline == null && r2.deadline == null) return 0;
        if (r1.deadline == null) return 1;  // null deadline after non-null
        if (r2.deadline == null) return -1;
        return r1.deadline.compareTo(r2.deadline);
    };

    /** Comparator for submit time (timeSubmitted string, e.g. ISO format). */
    public static final Comparator<ServiceRequest> SUBMIT_TIME_COMPARATOR = (r1, r2) -> {
        if (r1 == r2) return 0;
        if (r1 == null) return 1;
        if (r2 == null) return -1;
        if (r1.timeSubmitted == null && r2.timeSubmitted == null) return 0;
        if (r1.timeSubmitted == null) return 1;
        if (r2.timeSubmitted == null) return -1;
        return r1.timeSubmitted.compareTo(r2.timeSubmitted);
    };

    /** Comparator for urgency score. Higher score = higher urgency, but basic comparator compares natural ascending int. */
    public static final Comparator<ServiceRequest> URGENCY_COMPARATOR = (r1, r2) -> {
        if (r1 == r2) return 0;
        if (r1 == null) return 1;
        if (r2 == null) return -1;
        return Integer.compare(r1.urgency, r2.urgency);
    };

    public static Comparator<ServiceRequest> getComparator(SortCriteria criteria, boolean ascending) {
        Comparator<ServiceRequest> baseComp;
        switch (criteria) {
            case DEADLINE:
                baseComp = DEADLINE_COMPARATOR;
                break;
            case SUBMIT_TIME:
                baseComp = SUBMIT_TIME_COMPARATOR;
                break;
            case URGENCY:
                baseComp = URGENCY_COMPARATOR;
                break;
            default:
                throw new IllegalArgumentException("Unknown sorting criteria: " + criteria);
        }
        return ascending ? baseComp : baseComp.reversed();
    }

    /* -----------------------------------------------------------------
     * High-level ServiceRequest Sort API
     * ----------------------------------------------------------------- */

    public static SortMetrics sort(ServiceRequest[] reqs, SortCriteria criteria, AlgorithmType algo, boolean ascending) {
        Comparator<ServiceRequest> comp = getComparator(criteria, ascending);
        return sort(reqs, comp, algo);
    }

    public static SortMetrics sort(List<ServiceRequest> reqsList, SortCriteria criteria, AlgorithmType algo, boolean ascending) {
        ServiceRequest[] arr = reqsList.toArray(new ServiceRequest[0]);
        SortMetrics metrics = sort(arr, criteria, algo, ascending);
        for (int i = 0; i < arr.length; i++) {
            reqsList.set(i, arr[i]);
        }
        return metrics;
    }

    public static <T> SortMetrics sort(T[] arr, Comparator<? super T> comp, AlgorithmType algo) {
        long startTime = System.nanoTime();
        MetricsTracker tracker = new MetricsTracker();

        switch (algo) {
            case SELECTION_SORT:
                selectionSort(arr, comp, tracker);
                break;
            case INSERTION_SORT:
                insertionSort(arr, comp, tracker);
                break;
            case MERGE_SORT:
                mergeSort(arr, comp, tracker);
                break;
            case QUICKSORT:
                quickSort(arr, comp, tracker);
                break;
        }

        long timeNs = System.nanoTime() - startTime;
        return new SortMetrics(algo.getDisplayName(), tracker.comparisons, tracker.swaps, timeNs, algo.isStable(), algo.getSpaceComplexity());
    }

    /* -----------------------------------------------------------------
     * Metrics Tracker Helper
     * ----------------------------------------------------------------- */
    private static class MetricsTracker {
        long comparisons = 0;
        long swaps = 0;
    }

    /* -----------------------------------------------------------------
     * 1. Selection Sort (In-place, O(N^2), Unstable)
     * ----------------------------------------------------------------- */
    public static <T> void selectionSort(T[] arr, Comparator<? super T> comp) {
        selectionSort(arr, comp, new MetricsTracker());
    }

    private static <T> void selectionSort(T[] arr, Comparator<? super T> comp, MetricsTracker tracker) {
        if (arr == null || arr.length <= 1) return;
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                tracker.comparisons++;
                if (comp.compare(arr[j], arr[minIdx]) < 0) {
                    minIdx = j;
                }
            }
            if (minIdx != i) {
                swap(arr, i, minIdx);
                tracker.swaps++;
            }
        }
    }

    /* -----------------------------------------------------------------
     * 2. Insertion Sort (In-place, O(N^2), Stable)
     * ----------------------------------------------------------------- */
    public static <T> void insertionSort(T[] arr, Comparator<? super T> comp) {
        insertionSort(arr, comp, new MetricsTracker());
    }

    private static <T> void insertionSort(T[] arr, Comparator<? super T> comp, MetricsTracker tracker) {
        if (arr == null || arr.length <= 1) return;
        int n = arr.length;
        for (int i = 1; i < n; i++) {
            T key = arr[i];
            int j = i - 1;
            while (j >= 0) {
                tracker.comparisons++;
                if (comp.compare(arr[j], key) > 0) {
                    arr[j + 1] = arr[j];
                    tracker.swaps++;
                    j--;
                } else {
                    break;
                }
            }
            arr[j + 1] = key;
            if (j + 1 != i) {
                tracker.swaps++;
            }
        }
    }

    /* -----------------------------------------------------------------
     * 3. Merge Sort (Out-of-place O(N) space, O(N log N) time, Stable)
     * ----------------------------------------------------------------- */
    public static <T> void mergeSort(T[] arr, Comparator<? super T> comp) {
        mergeSort(arr, comp, new MetricsTracker());
    }

    private static <T> void mergeSort(T[] arr, Comparator<? super T> comp, MetricsTracker tracker) {
        if (arr == null || arr.length <= 1) return;
        @SuppressWarnings("unchecked")
        T[] aux = (T[]) new Object[arr.length];
        mergeSortRecursive(arr, aux, 0, arr.length - 1, comp, tracker);
    }

    private static <T> void mergeSortRecursive(T[] arr, T[] aux, int low, int high, Comparator<? super T> comp, MetricsTracker tracker) {
        if (low >= high) return;
        int mid = low + (high - low) / 2;
        mergeSortRecursive(arr, aux, low, mid, comp, tracker);
        mergeSortRecursive(arr, aux, mid + 1, high, comp, tracker);
        merge(arr, aux, low, mid, high, comp, tracker);
    }

    private static <T> void merge(T[] arr, T[] aux, int low, int mid, int high, Comparator<? super T> comp, MetricsTracker tracker) {
        for (int k = low; k <= high; k++) {
            aux[k] = arr[k];
            tracker.swaps++;
        }

        int i = low;
        int j = mid + 1;

        for (int k = low; k <= high; k++) {
            if (i > mid) {
                arr[k] = aux[j++];
                tracker.swaps++;
            } else if (j > high) {
                arr[k] = aux[i++];
                tracker.swaps++;
            } else {
                tracker.comparisons++;
                // Using <= 0 maintains stability when keys are equal!
                if (comp.compare(aux[i], aux[j]) <= 0) {
                    arr[k] = aux[i++];
                } else {
                    arr[k] = aux[j++];
                }
                tracker.swaps++;
            }
        }
    }

    /* -----------------------------------------------------------------
     * 4. Quicksort (In-place, O(N log N) avg, Unstable)
     * ----------------------------------------------------------------- */
    public static <T> void quickSort(T[] arr, Comparator<? super T> comp) {
        quickSort(arr, comp, new MetricsTracker());
    }

    private static <T> void quickSort(T[] arr, Comparator<? super T> comp, MetricsTracker tracker) {
        if (arr == null || arr.length <= 1) return;
        quickSortRecursive(arr, 0, arr.length - 1, comp, tracker);
    }

    private static <T> void quickSortRecursive(T[] arr, int low, int high, Comparator<? super T> comp, MetricsTracker tracker) {
        if (low < high) {
            int pIndex = partition(arr, low, high, comp, tracker);
            quickSortRecursive(arr, low, pIndex - 1, comp, tracker);
            quickSortRecursive(arr, pIndex + 1, high, comp, tracker);
        }
    }

    private static <T> int partition(T[] arr, int low, int high, Comparator<? super T> comp, MetricsTracker tracker) {
        // Median-of-three pivot selection to prevent worst-case O(N^2) on pre-sorted arrays
        int mid = low + (high - low) / 2;
        tracker.comparisons++;
        if (comp.compare(arr[mid], arr[low]) < 0) {
            swap(arr, low, mid);
            tracker.swaps++;
        }
        tracker.comparisons++;
        if (comp.compare(arr[high], arr[low]) < 0) {
            swap(arr, low, high);
            tracker.swaps++;
        }
        tracker.comparisons++;
        if (comp.compare(arr[high], arr[mid]) < 0) {
            swap(arr, mid, high);
            tracker.swaps++;
        }
        // Place median at high position
        swap(arr, mid, high);
        tracker.swaps++;

        T pivot = arr[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            tracker.comparisons++;
            if (comp.compare(arr[j], pivot) <= 0) {
                i++;
                if (i != j) {
                    swap(arr, i, j);
                    tracker.swaps++;
                }
            }
        }
        swap(arr, i + 1, high);
        tracker.swaps++;
        return i + 1;
    }

    private static <T> void swap(T[] arr, int i, int j) {
        T temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
