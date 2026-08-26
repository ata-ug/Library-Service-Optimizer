package algorithms;


    // GreedyAndKnapsack.java
// Combined file containing both Greedy Resource Assignment and Knapsack DP examples and tests.

import java.util.Arrays;

/* ---------- Core data types for library requests ---------- */

enum RequestType {
    BORROW, RETURN, RESERVE, RENEW
}

enum ResourceType {
    STAFF, CART, KIOSK
}

public class GreedyAlgorithms {
    public final String id;
    public final RequestType type;
    public final int urgency; // higher is more urgent
    public final ResourceType resource;

    public GreedyAlgorithms(String id, RequestType type, int urgency, ResourceType resource) {
        this.id = id;
        this.type = type;
        this.urgency = urgency;
        this.resource = resource;
    }

    @Override
    public String toString() {
        return id + "(" + type + ", u=" + urgency + ", res=" + resource + ")";
    }

    public static void runDemoWithDatabase(java.util.List<library.model.ServiceRequest> dbRequests, int staffCount, int cartCount, int kioskCount) {
        java.util.List<GreedyAlgorithms> list = new java.util.ArrayList<>();
        if (dbRequests != null && !dbRequests.isEmpty()) {
            for (library.model.ServiceRequest r : dbRequests) {
                RequestType type;
                try {
                    type = RequestType.valueOf(r.category.toUpperCase());
                } catch (Exception e) {
                    type = RequestType.BORROW;
                }
                ResourceType res;
                if (type == RequestType.RETURN) res = ResourceType.CART;
                else if (type == RequestType.RENEW) res = ResourceType.KIOSK;
                else res = ResourceType.STAFF;

                list.add(new GreedyAlgorithms("REQ-" + r.requestId, type, r.urgency, res));
            }
        } else {
            list.add(new GreedyAlgorithms("REQ-101", RequestType.BORROW, 9, ResourceType.STAFF));
            list.add(new GreedyAlgorithms("REQ-102", RequestType.RETURN, 4, ResourceType.CART));
            list.add(new GreedyAlgorithms("REQ-103", RequestType.RESERVE, 8, ResourceType.STAFF));
            list.add(new GreedyAlgorithms("REQ-104", RequestType.RENEW, 6, ResourceType.KIOSK));
            list.add(new GreedyAlgorithms("REQ-105", RequestType.BORROW, 10, ResourceType.STAFF));
        }

        GreedyAlgorithms[] requests = list.toArray(new GreedyAlgorithms[0]);

        System.out.println("\nLoaded " + requests.length + " Pending Service Requests from Database:");
        for (int i = 0; i < Math.min(8, requests.length); i++) {
            System.out.println("  " + requests[i]);
        }
        if (requests.length > 8) System.out.println("  ... (" + (requests.length - 8) + " more requests in queue)");

        ResourcePool pool = new ResourcePool(staffCount, cartCount, kioskCount);
        GreedyAlgorithms[] allocated = GreedyScheduler.schedule(requests, pool);

        System.out.println("\n✔ Greedy Allocation Result (" + allocated.length + " / " + requests.length + " scheduled):");
        for (int i = 0; i < Math.min(15, allocated.length); i++) {
            System.out.println("  ✅ Scheduled: " + allocated[i]);
        }
        if (allocated.length > 15) System.out.println("  ... (" + (allocated.length - 15) + " more scheduled)");
    }
}

/* ---------- Resource pool for availability tracking ---------- */

class ResourcePool {
    private int staff;
    private int carts;
    private int kiosks;

    public ResourcePool(int staff, int carts, int kiosks) {
        this.staff = staff;
        this.carts = carts;
        this.kiosks = kiosks;
    }

    public boolean canAllocate(ResourceType r) {
        switch (r) {
            case STAFF: return staff > 0;
            case CART:  return carts > 0;
            case KIOSK: return kiosks > 0;
            default: return false;
        }
    }

    public boolean allocate(ResourceType r) {
        if (!canAllocate(r)) return false;
        switch (r) {
            case STAFF: staff--; return true;
            case CART:  carts--; return true;
            case KIOSK: kiosks--; return true;
            default: return false;
        }
    }

    @Override
    public String toString() {
        return "ResourcePool{staff=" + staff + ", carts=" + carts + ", kiosks=" + kiosks + "}";
    }
}

/* ---------- Greedy scheduler ---------- */

class GreedyScheduler {
    /**
     * Schedules requests greedily by selecting at each step the highest urgency request that can
     * currently be handled by available resources.
     * 
     * @param requests input array (will not be mutated)
     * @param resources starting resource availability (mutation occurs on the provided ResourcePool)
     * @return array of scheduled requests in order
     */
    public static GreedyAlgorithms[] schedule(GreedyAlgorithms[] requests, ResourcePool resources) {
        // Copy references into a mutable array of remaining requests
        GreedyAlgorithms[] remaining = Arrays.copyOf(requests, requests.length);
        int remCount = remaining.length;

        GreedyAlgorithms[] scheduled = new GreedyAlgorithms[requests.length];
        int scheduledCount = 0;

        while (true) {
            // Find best request
            int bestIdx = -1;
            for (int i = 0; i < remCount; i++) {
                GreedyAlgorithms r = remaining[i];
                if (r == null) continue;
                if (!resources.canAllocate(r.resource)) continue;
                if (bestIdx == -1 || r.urgency > remaining[bestIdx].urgency) {
                    bestIdx = i;
                }
            }

            if (bestIdx == -1) break; // no schedulable request remains

            GreedyAlgorithms chosen = remaining[bestIdx];
            // allocate and append to scheduled
            boolean ok = resources.allocate(chosen.resource);
            if (!ok) {
                // Defensive: allocation failed unexpectedly; remove it and continue
                remaining[bestIdx] = null;
                continue;
            }
            scheduled[scheduledCount++] = chosen;
            // remove chosen from remaining by shifting last element into its place
            remaining[bestIdx] = remaining[remCount - 1];
            remaining[remCount - 1] = null;
            remCount--;
        }

        // return compacted array
        return Arrays.copyOf(scheduled, scheduledCount);
    }
}


    

