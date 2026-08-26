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

    public static void runDemo(int capacity) {
        Item[] items = new Item[]{
                new Item("Stack Re-shelving", 15, 30),
                new Item("Rare Book Consultation", 25, 60),
                new Item("RFID Tag Audit", 20, 40),
                new Item("Inter-Library Loan Processing", 10, 25),
                new Item("Catalog System Maintenance", 30, 50)
        };

        System.out.println("\nAvailable Service Tasks:");
        for (Item item : items) System.out.println("  " + item);

        Result result = solve(items, capacity);
        System.out.println("\n✔ Optimal DP Selection:");
        System.out.println("  • Maximum Achieved Benefit: " + result.totalValue);
        System.out.println("  • Total Time Consumed:       " + result.totalWeight + " / " + capacity + " mins");
        System.out.println("  • Chosen Tasks:");
        for (Item item : result.selectedItems) {
            System.out.println("    - " + item);
        }
    }
}


    

