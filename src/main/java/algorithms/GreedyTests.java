package algorithms;

import java.util.Arrays;


/* ---------- Demonstration and tests for Greedy ---------- */

class GreedyTests {
    public static void runAll() {
        testExample();
        testBoundary();
        testInvalid();
        counterExample();
    }

    private static void testExample() {
        System.out.println("Greedy testExample:");
        GreedyAlgorithms[] requests = new GreedyAlgorithms[] {
            new GreedyAlgorithms("R001", RequestType.BORROW, 9, ResourceType.STAFF),
            new GreedyAlgorithms("R002", RequestType.RETURN, 5, ResourceType.CART),
            new GreedyAlgorithms("R003", RequestType.RESERVE, 8, ResourceType.STAFF),
            new GreedyAlgorithms("R004", RequestType.RENEW, 3, ResourceType.KIOSK)
        };
        ResourcePool pool = new ResourcePool(1, 1, 1); // 1 staff, 1 cart, 1 kiosk
        System.out.println("Start resources: " + pool);
        GreedyAlgorithms[] scheduled = GreedyScheduler.schedule(requests, pool);
        System.out.println("Scheduled order: " + Arrays.toString(scheduled));
        // expected: R001 -> R003 -> R002 -> R004 depends: only 1 staff, so after R001 scheduled, staff=0, R003 can't be scheduled if we chose R001 first.
        // With 1 staff, greedy picks R001 (urgency 9, staff), then highest urgency that can allocate: R003 requires staff but none left -> skip, picks R002 (cart), then R004 (kiosk).
        // So expected: R001, R002, R004
        System.out.println();
    }

    private static void testBoundary() {
        System.out.println("Greedy testBoundary (no resources):");
        GreedyAlgorithms[] requests = new GreedyAlgorithms[] {
            new GreedyAlgorithms("R005", RequestType.BORROW, 7, ResourceType.STAFF)
        };
        ResourcePool pool = new ResourcePool(0, 0, 0);
        GreedyAlgorithms[] scheduled = GreedyScheduler.schedule(requests, pool);
        System.out.println("Scheduled (expected empty): " + Arrays.toString(scheduled));
        System.out.println();
    }

    private static void testInvalid() {
        System.out.println("Greedy testInvalid (null and zero urgency):");
        GreedyAlgorithms[] requests = new GreedyAlgorithms[] {
            new GreedyAlgorithms("R006", RequestType.RETURN, 0, ResourceType.CART),
            new GreedyAlgorithms("R007", RequestType.RENEW, -1, ResourceType.KIOSK)
        };
        ResourcePool pool = new ResourcePool(1, 1, 1);
        GreedyAlgorithms[] scheduled = GreedyScheduler.schedule(requests, pool);
        System.out.println("Scheduled (orders by urgency including nonpositive): " + Arrays.toString(scheduled));
        System.out.println();
    }

    private static void counterExample() {
        System.out.println("Greedy counterexample where greedy fails to maximize total benefit:");
        // Suppose staff resource only and we treat urgency as single-item priority.
        // But suppose benefits (not considered by greedy) would prefer two medium urgency tasks giving sum benefit > 1 high-urgency task.
        GreedyAlgorithms rHigh = new GreedyAlgorithms("RH", RequestType.BORROW, 100, ResourceType.STAFF); // high urgency
        GreedyAlgorithms rM1 = new GreedyAlgorithms("RM1", RequestType.RESERVE, 60, ResourceType.STAFF);
        GreedyAlgorithms rM2 = new GreedyAlgorithms("RM2", RequestType.RENEW, 59, ResourceType.STAFF);
        GreedyAlgorithms[] requests = new GreedyAlgorithms[] { rHigh, rM1, rM2 };
        ResourcePool pool = new ResourcePool(1, 0, 0); // only 1 staff -> greedy picks RH (urgency 100) and schedules only it.
        GreedyAlgorithms[] scheduled = GreedyScheduler.schedule(requests, pool);
        System.out.println("Requests: " + Arrays.toString(requests));
        System.out.println("Resources: staff=1");
        System.out.println("Greedy selected: " + Arrays.toString(scheduled));
        System.out.println("But optimal if goal were to maximize total benefit (treating urgency as NOT equal to benefit) could be selecting RM1 and RM2 if staff allowed 2 units; with staff=1 greedy is constrained and shows how urgent-first can be suboptimal when resources/time/benefit trade-off exists.");
        System.out.println();
    }

    public static void main(String[] args) {
        System.out.println("===== Greedy Algorithm Tests =====\n");
        runAll();
        System.out.println("===== All Tests Completed =====");
    }
}

