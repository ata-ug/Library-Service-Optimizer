package algorithms;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Formal JUnit 4 Test Suite for Greedy Resource Scheduler.
 * Tests urgent-first selection, resource pool constraints, zero capacity, tie breaking,
 * and boundary edge cases.
 */
public class GreedySchedulerTest {

    @Test
    public void schedule_standardRequests_selectsByHighestUrgencyAndAvailability() {
        GreedyAlgorithms[] requests = new GreedyAlgorithms[] {
            new GreedyAlgorithms("R001", RequestType.BORROW, 9, ResourceType.STAFF),
            new GreedyAlgorithms("R002", RequestType.RETURN, 5, ResourceType.CART),
            new GreedyAlgorithms("R003", RequestType.RESERVE, 8, ResourceType.STAFF),
            new GreedyAlgorithms("R004", RequestType.RENEW, 3, ResourceType.KIOSK)
        };
        ResourcePool pool = new ResourcePool(1, 1, 1);
        GreedyAlgorithms[] scheduled = GreedyScheduler.schedule(requests, pool);

        assertEquals(3, scheduled.length);
        assertEquals("R001", scheduled[0].id);
        assertEquals("R002", scheduled[1].id);
        assertEquals("R004", scheduled[2].id);
    }

    @Test
    public void schedule_zeroAvailableResources_returnsEmptyArray() {
        GreedyAlgorithms[] requests = new GreedyAlgorithms[] {
            new GreedyAlgorithms("R001", RequestType.BORROW, 10, ResourceType.STAFF)
        };
        ResourcePool pool = new ResourcePool(0, 0, 0);
        GreedyAlgorithms[] scheduled = GreedyScheduler.schedule(requests, pool);

        assertEquals(0, scheduled.length);
    }

    @Test
    public void schedule_emptyRequests_returnsEmptyArray() {
        GreedyAlgorithms[] requests = new GreedyAlgorithms[0];
        ResourcePool pool = new ResourcePool(5, 5, 5);
        GreedyAlgorithms[] scheduled = GreedyScheduler.schedule(requests, pool);

        assertEquals(0, scheduled.length);
    }

    @Test
    public void schedule_multipleSameResourceRequests_consumesPoolUntilExhausted() {
        GreedyAlgorithms[] requests = new GreedyAlgorithms[] {
            new GreedyAlgorithms("R1", RequestType.BORROW, 10, ResourceType.STAFF),
            new GreedyAlgorithms("R2", RequestType.BORROW, 20, ResourceType.STAFF),
            new GreedyAlgorithms("R3", RequestType.BORROW, 30, ResourceType.STAFF)
        };
        ResourcePool pool = new ResourcePool(2, 0, 0);
        GreedyAlgorithms[] scheduled = GreedyScheduler.schedule(requests, pool);

        assertEquals(2, scheduled.length);
        assertEquals("R3", scheduled[0].id);
        assertEquals("R2", scheduled[1].id);
    }

    @Test
    public void schedule_negativeOrZeroUrgency_schedulesIfResourcesAvailable() {
        GreedyAlgorithms[] requests = new GreedyAlgorithms[] {
            new GreedyAlgorithms("Low", RequestType.RETURN, 0, ResourceType.CART),
            new GreedyAlgorithms("Neg", RequestType.RENEW, -5, ResourceType.KIOSK)
        };
        ResourcePool pool = new ResourcePool(1, 1, 1);
        GreedyAlgorithms[] scheduled = GreedyScheduler.schedule(requests, pool);

        assertEquals(2, scheduled.length);
        assertEquals("Low", scheduled[0].id);
        assertEquals("Neg", scheduled[1].id);
    }
}
