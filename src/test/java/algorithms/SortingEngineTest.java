package algorithms;

import library.model.ServiceRequest;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.*;

public class SortingEngineTest {

    private ServiceRequest req1;
    private ServiceRequest req2;
    private ServiceRequest req3;
    private ServiceRequest req4;
    private ServiceRequest req5;

    @Before
    public void setUp() {
        // req1: Urgency 5, deadline 2026-09-01T12:00, time 2026-08-25T08:00
        req1 = new ServiceRequest(1, 101, 201, 1, 5, "BORROW", 5, "2026-08-25T08:00:00", "2026-09-01T12:00:00", "PENDING");
        // req2: Urgency 9, deadline 2026-08-28T10:00, time 2026-08-25T09:30
        req2 = new ServiceRequest(2, 102, 202, 2, 6, "RETURN", 9, "2026-08-25T09:30:00", "2026-08-28T10:00:00", "PENDING");
        // req3: Urgency 2, deadline null, time 2026-08-25T07:15
        req3 = new ServiceRequest(3, 103, 203, 3, 7, "RESERVE", 2, "2026-08-25T07:15:00", null, "PENDING");
        // req4: Urgency 9, deadline 2026-08-30T15:00, time 2026-08-25T11:00
        req4 = new ServiceRequest(4, 104, 204, 4, 8, "RENEW", 9, "2026-08-25T11:00:00", "2026-08-30T15:00:00", "PENDING");
        // req5: Urgency 7, deadline null, time 2026-08-25T10:00
        req5 = new ServiceRequest(5, 105, 205, 5, 9, "BORROW", 7, "2026-08-25T10:00:00", null, "PENDING");
    }

    @Test
    public void testIntegerArraySortingAllAlgorithms() {
        Integer[] expected = {1, 2, 3, 5, 8, 9, 14, 20};
        Comparator<Integer> comp = Integer::compareTo;

        for (SortingEngine.AlgorithmType algo : SortingEngine.AlgorithmType.values()) {
            Integer[] arr = {9, 3, 5, 1, 20, 2, 14, 8};
            SortingEngine.sort(arr, comp, algo);
            assertArrayEquals("Failed for " + algo, expected, arr);
        }
    }

    @Test
    public void testEmptyAndSingleElementArrays() {
        Comparator<Integer> comp = Integer::compareTo;
        for (SortingEngine.AlgorithmType algo : SortingEngine.AlgorithmType.values()) {
            Integer[] emptyArr = new Integer[0];
            SortingEngine.sort(emptyArr, comp, algo);
            assertEquals(0, emptyArr.length);

            Integer[] singleArr = {42};
            SortingEngine.sort(singleArr, comp, algo);
            assertEquals(Integer.valueOf(42), singleArr[0]);
        }
    }

    @Test
    public void testSortByUrgencyAscendingAndDescending() {
        ServiceRequest[] list = {req1, req2, req3, req4, req5};

        for (SortingEngine.AlgorithmType algo : SortingEngine.AlgorithmType.values()) {
            ServiceRequest[] arrAsc = list.clone();
            SortingEngine.sort(arrAsc, SortingEngine.SortCriteria.URGENCY, algo, true);
            // Urgencies should be 2, 5, 7, 9, 9
            assertEquals(2, arrAsc[0].urgency);
            assertEquals(5, arrAsc[1].urgency);
            assertEquals(7, arrAsc[2].urgency);
            assertEquals(9, arrAsc[3].urgency);
            assertEquals(9, arrAsc[4].urgency);

            ServiceRequest[] arrDesc = list.clone();
            SortingEngine.sort(arrDesc, SortingEngine.SortCriteria.URGENCY, algo, false);
            // Urgencies should be 9, 9, 7, 5, 2
            assertEquals(9, arrDesc[0].urgency);
            assertEquals(9, arrDesc[1].urgency);
            assertEquals(7, arrDesc[2].urgency);
            assertEquals(5, arrDesc[3].urgency);
            assertEquals(2, arrDesc[4].urgency);
        }
    }

    @Test
    public void testSortByDeadlineNullsLast() {
        ServiceRequest[] list = {req1, req2, req3, req4, req5};

        for (SortingEngine.AlgorithmType algo : SortingEngine.AlgorithmType.values()) {
            ServiceRequest[] arr = list.clone();
            SortingEngine.sort(arr, SortingEngine.SortCriteria.DEADLINE, algo, true);

            // Valid deadlines sorted: req2 (2026-08-28), req4 (2026-08-30), req1 (2026-09-01)
            assertEquals("2026-08-28T10:00:00", arr[0].deadline);
            assertEquals("2026-08-30T15:00:00", arr[1].deadline);
            assertEquals("2026-09-01T12:00:00", arr[2].deadline);
            // Last 2 should have null deadline (req3, req5)
            assertNull(arr[3].deadline);
            assertNull(arr[4].deadline);
        }
    }

    @Test
    public void testSortBySubmitTime() {
        ServiceRequest[] list = {req1, req2, req3, req4, req5};

        for (SortingEngine.AlgorithmType algo : SortingEngine.AlgorithmType.values()) {
            ServiceRequest[] arr = list.clone();
            SortingEngine.sort(arr, SortingEngine.SortCriteria.SUBMIT_TIME, algo, true);

            // Times: req3 (07:15), req1 (08:00), req2 (09:30), req5 (10:00), req4 (11:00)
            assertEquals(3, arr[0].requestId);
            assertEquals(1, arr[1].requestId);
            assertEquals(2, arr[2].requestId);
            assertEquals(5, arr[3].requestId);
            assertEquals(4, arr[4].requestId);
        }
    }

    @Test
    public void testInsertionSortAndMergeSortStability() {
        // Create duplicate key items where original order is req2 then req4 (both urgency 9)
        List<ServiceRequest> reqList = new ArrayList<>();
        reqList.add(req2); // ID 2, urgency 9
        reqList.add(req1); // ID 1, urgency 5
        reqList.add(req4); // ID 4, urgency 9

        // Test Insertion Sort Stability
        ServiceRequest[] arrInsertion = reqList.toArray(new ServiceRequest[0]);
        SortingEngine.sort(arrInsertion, SortingEngine.SortCriteria.URGENCY, SortingEngine.AlgorithmType.INSERTION_SORT, true);
        // Sorted urgencies: req1 (5), req2 (9), req4 (9)
        assertEquals(1, arrInsertion[0].requestId);
        assertEquals(2, arrInsertion[1].requestId); // req2 should stay before req4
        assertEquals(4, arrInsertion[2].requestId);

        // Test Merge Sort Stability
        ServiceRequest[] arrMerge = reqList.toArray(new ServiceRequest[0]);
        SortingEngine.sort(arrMerge, SortingEngine.SortCriteria.URGENCY, SortingEngine.AlgorithmType.MERGE_SORT, true);
        assertEquals(1, arrMerge[0].requestId);
        assertEquals(2, arrMerge[1].requestId); // req2 should stay before req4
        assertEquals(4, arrMerge[2].requestId);
    }
}
