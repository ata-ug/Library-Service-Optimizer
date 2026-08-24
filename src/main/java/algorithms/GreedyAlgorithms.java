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

class GreedyAlgorithms {
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


    

