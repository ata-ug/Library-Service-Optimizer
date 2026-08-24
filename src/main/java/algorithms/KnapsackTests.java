package algorithms;

import java.util.Arrays;

    /* ---------- Demonstration and tests for Knapsack ---------- */

class KnapsackTests {
    public static void runAll() {
        testExample();
        testBoundary();
        testInvalid();
    }

    private static void testExample() {
        System.out.println("Knapsack testExample:");
        Item[] items = new Item[] {
            new Item("R001", 2, 30),
            new Item("R002", 3, 40),
            new Item("R003", 4, 50),
            new Item("R004", 5, 70)
        };
        int capacity = 7;
        DynamicProgramming.Result res = DynamicProgramming.solve(items, capacity);
        System.out.println("Items: " + Arrays.toString(items));
        System.out.println("Capacity: " + capacity);
        System.out.println("Selected items: " + Arrays.toString(res.selectedItems));
        System.out.println("Total benefit: " + res.totalValue + ", total time: " + res.totalWeight);
        // For the given example: optimal is choose R002 (3,40) + R004 (5,70) cannot (3+5>7),
        // best should be R001(2,30)+R004(5,70)=100 (2+5=7) -> total 100
        System.out.println("DP table (last row): " + Arrays.toString(res.dpTable[items.length]));
        System.out.println();
    }

    private static void testBoundary() {
        System.out.println("Knapsack testBoundary (zero capacity):");
        Item[] items = new Item[] { new Item("A", 1, 10) };
        DynamicProgramming.Result res = DynamicProgramming.solve(items, 0);
        System.out.println("Selected (expected none): " + Arrays.toString(res.selectedItems));
        System.out.println();
    }

    private static void testInvalid() {
        System.out.println("Knapsack testInvalid (item exceeding capacity):");
        Item[] items = new Item[] { new Item("Big", 100, 1000), new Item("Small", 1, 1) };
        DynamicProgramming.Result res = DynamicProgramming.solve(items, 5);
        System.out.println("Selected: " + Arrays.toString(res.selectedItems) + ", totalValue=" + res.totalValue);
        System.out.println();
    }

        public static void main(String[] args) {
        System.out.println("===== Knapsack Tests =====\n");
        runAll();
        System.out.println("===== All Tests Completed =====");
    }
}

