package structures;

import java.util.Comparator;
import java.util.NoSuchElementException;

/**
 * A generic binary heap implemented from scratch, backed by a dynamically
 * resizing array. Defaults to min-heap order using natural ordering
 * (T must implement Comparable<T>), but a custom Comparator<T> can be
 * supplied instead (e.g. to build a max-heap, or to order by a specific
 * field like due date or reservation priority).
 *
 * @param <T> the type of elements held in this heap
 */
public class GenericHeap<T> {

    private T[] data;
    private int size;                  // number of elements currently in the heap
    private Comparator<T> comparator;  // determines ordering; null means "use natural ordering"
    private static final int DEFAULT_CAPACITY = 16;

    /**
     * Creates a min-heap ordered by natural ordering.
     * Elements passed to add() must implement Comparable<T>.
     */
    @SuppressWarnings("unchecked")
    public GenericHeap() {
        this.data = (T[]) new Object[DEFAULT_CAPACITY];
        this.size = 0;
        this.comparator = null;
    }

    /**
     * Creates a heap ordered according to the given comparator.
     * Pass a comparator that returns "smaller first" for min-heap behavior,
     * or Comparator.reverseOrder() / a reversed comparator for a max-heap.
     */
    @SuppressWarnings("unchecked")
    public GenericHeap(Comparator<T> comparator) {
        this.data = (T[]) new Object[DEFAULT_CAPACITY];
        this.size = 0;
        this.comparator = comparator;
    }

    /**
     * Compares two elements according to whichever ordering this heap was
     * configured with: the supplied Comparator, or natural ordering if none
     * was given.
     */
    @SuppressWarnings("unchecked")
    private int compare(T a, T b) {
        if (comparator != null) {
            return comparator.compare(a, b);
        }
        // Natural ordering: T is assumed to implement Comparable<T>.
        // This cast is unchecked because we can't enforce "T extends Comparable<T>"
        // while still allowing a Comparator-based constructor for non-Comparable types.
        return ((Comparable<T>) a).compareTo(b);
    }

    private int parentOf(int i) { return (i - 1) / 2; }
    private int leftChildOf(int i) { return 2 * i + 1; }
    private int rightChildOf(int i) { return 2 * i + 2; }

    private void swap(int i, int j) {
        T temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }

    /**
     * Inserts an element into the heap, maintaining heap order.
     */
    public void add(T item) {
        if (size == data.length) {
            resize(data.length * 2);
        }
        data[size] = item;
        siftUp(size);
        size++;
    }

    /**
     * Moves the element at index i up toward the root until heap order
     * is restored (its parent is <= it, for a min-heap).
     */
    private void siftUp(int i) {
        while (i > 0) {
            int parent = parentOf(i);
            if (compare(data[i], data[parent]) < 0) {
                swap(i, parent);
                i = parent;
            } else {
                break; // heap property satisfied
            }
        }
    }

    /**
     * Removes and returns the element at the top of the heap
     * (the minimum, for a min-heap; the maximum, for a max-heap).
     * Throws NoSuchElementException if the heap is empty.
     */
    public T poll() {
        if (isEmpty()) {
            throw new NoSuchElementException("Heap is empty");
        }
        T top = data[0];
        size--;
        data[0] = data[size]; // move last element to the root
        data[size] = null;    // avoid holding a stale reference
        if (size > 0) {
            siftDown(0);
        }
        return top;
    }

    /**
     * Moves the element at index i down toward the leaves until heap order
     * is restored.
     */
    private void siftDown(int i) {
        while (true) {
            int left = leftChildOf(i);
            int right = rightChildOf(i);
            int smallest = i;

            if (left < size && compare(data[left], data[smallest]) < 0) {
                smallest = left;
            }
            if (right < size && compare(data[right], data[smallest]) < 0) {
                smallest = right;
            }
            if (smallest == i) {
                break; // heap property satisfied
            }
            swap(i, smallest);
            i = smallest;
        }
    }

    /**
     * Returns the top element without removing it.
     * Throws NoSuchElementException if the heap is empty.
     */
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Heap is empty");
        }
        return data[0];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    @SuppressWarnings("unchecked")
    private void resize(int newCapacity) {
        T[] newData = (T[]) new Object[newCapacity];
        for (int i = 0; i < size; i++) {
            newData[i] = data[i];
        }
        data = newData;
    }

    /**
     * Simple demonstration / manual test using a library-style scenario:
     * a reservation queue where the book with the earliest due date
     * (soonest available) should be served first.
     */
    private static class Reservation {
        String memberName;
        int daysUntilDue;

        Reservation(String memberName, int daysUntilDue) {
            this.memberName = memberName;
            this.daysUntilDue = daysUntilDue;
        }

        public String toString() {
            return memberName + " (due in " + daysUntilDue + " days)";
        }
    }

    public static void main(String[] args) {
        // Min-heap ordered by daysUntilDue: whoever's book is due soonest is served first.
        GenericHeap<Reservation> reservationQueue = new GenericHeap<>(
                (a, b) -> Integer.compare(a.daysUntilDue, b.daysUntilDue)
        );

        reservationQueue.add(new Reservation("Ama", 5));
        reservationQueue.add(new Reservation("Kwame", 2));
        reservationQueue.add(new Reservation("Efua", 8));

        System.out.println("Next up: " + reservationQueue.peek());
        System.out.println("Serving: " + reservationQueue.poll());
        System.out.println("Serving: " + reservationQueue.poll());
        System.out.println("Remaining in queue: " + reservationQueue.size());
    }
}
