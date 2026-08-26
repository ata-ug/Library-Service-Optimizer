package algorithms;

import java.util.Arrays;


    /* ---------- Knapsack DP for request selection ---------- */

class Item {
    public final String id;
    public final int weight; // processing time (minutes)
    public final int value;  // benefit

    public Item(String id, int weight, int value) {
        this.id = id;
        this.weight = weight;
        this.value = value;
    }

    @Override
    public String toString() {
        return id + "(wt=" + weight + ", val=" + value + ")";
    }
}

public class DynamicProgramming {
    /**
     * Solves 0/1 knapsack via tabulation and reconstructs the chosen items.
     * @param items array of items
     * @param capacity integer capacity (e.g., staff minutes)
     * @return Result object containing total value, total weight and chosen item list
     */
    public static Result solve(Item[] items, int capacity) {
        int n = items.length;
        int[][] dp = new int[n + 1][capacity + 1];

        // Fill DP table
        for (int i = 1; i <= n; i++) {
            int wt = items[i - 1].weight;
            int val = items[i - 1].value;
            for (int w = 0; w <= capacity; w++) {
                if (wt > w) {
                    dp[i][w] = dp[i - 1][w];
                } else {
                    int exclude = dp[i - 1][w];
                    int include = dp[i - 1][w - wt] + val;
                    dp[i][w] = Math.max(exclude, include);
                }
            }
        }

        // Reconstruction
        int w = capacity;
        boolean[] chosen = new boolean[n];
        for (int i = n; i >= 1; i--) {
            if (dp[i][w] != dp[i - 1][w]) {
                chosen[i - 1] = true;
                w -= items[i - 1].weight;
            } else {
                chosen[i - 1] = false;
            }
        }

        // Build result
        int totalValue = dp[n][capacity];
        int totalWeight = 0;
        int count = 0;
        for (int i = 0; i < n; i++) {
            if (chosen[i]) {
                totalWeight += items[i].weight;
                count++;
            }
        }
        Item[] selected = new Item[count];
        int idx = 0;
        for (int i = 0; i < n; i++) {
            if (chosen[i]) selected[idx++] = items[i];
        }

        return new Result(totalValue, totalWeight, selected, dp);
    }

    public static class Result {
        public final int totalValue;
        public final int totalWeight;
        public final Item[] selectedItems;
        public final int[][] dpTable; // for trace/debug

        public Result(int totalValue, int totalWeight, Item[] selectedItems, int[][] dpTable) {
            this.totalValue = totalValue;
            this.totalWeight = totalWeight;
            this.selectedItems = selectedItems;
            this.dpTable = dpTable;
        }
    }

    public static void runDemoWithDatabase(java.util.List<library.model.ServiceRequest> dbRequests, int capacity) {
        java.util.List<Item> itemList = new java.util.ArrayList<>();
        if (dbRequests != null && !dbRequests.isEmpty()) {
            for (library.model.ServiceRequest r : dbRequests) {
                int weight = Math.max(5, (r.requestId * 7) % 25 + 5); // estimated processing time (5..30 mins)
                int value = r.urgency * 10; // priority benefit score
                itemList.add(new Item("REQ-" + r.requestId + " [" + r.category + "]", weight, value));
            }
        } else {
            itemList.add(new Item("Stack Re-shelving", 15, 30));
            itemList.add(new Item("Rare Book Consultation", 25, 60));
            itemList.add(new Item("RFID Tag Audit", 20, 40));
            itemList.add(new Item("Inter-Library Loan Processing", 10, 25));
            itemList.add(new Item("Catalog System Maintenance", 30, 50));
        }

        Item[] items = itemList.toArray(new Item[0]);

        System.out.println("\nLoaded " + items.length + " Pending Service Tasks from SQLite Database:");
        for (int i = 0; i < Math.min(6, items.length); i++) {
            System.out.println("  " + items[i]);
        }
        if (items.length > 6) System.out.println("  ... (" + (items.length - 6) + " more tasks loaded)");

        Result result = solve(items, capacity);
        System.out.println("\n✔ Optimal 0/1 Knapsack Selection (DP):");
        System.out.println("  • Maximum Priority Benefit Score: " + result.totalValue);
        System.out.println("  • Total Staff Time Consumed:     " + result.totalWeight + " / " + capacity + " mins");
        System.out.println("  • Chosen Tasks (" + result.selectedItems.length + " selected):");
        for (int i = 0; i < Math.min(12, result.selectedItems.length); i++) {
            System.out.println("    ✅ " + result.selectedItems[i]);
        }
        if (result.selectedItems.length > 12) {
            System.out.println("    ... (" + (result.selectedItems.length - 12) + " more selected)");
        }
    }
}


    

