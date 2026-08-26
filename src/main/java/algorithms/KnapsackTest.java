package algorithms;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Formal JUnit 4 Test Suite for 0/1 Knapsack Dynamic Programming Algorithm.
 * Tests optimal selection, edge cases (zero capacity, oversized items, empty list, tie values).
 */
public class KnapsackTest {

    @Test
    public void knapsack_standardCase_selectsOptimalSubset() {
        Item[] items = new Item[] {
            new Item("R001", 2, 30),
            new Item("R002", 3, 40),
            new Item("R003", 4, 50),
            new Item("R004", 5, 70)
        };
        int capacity = 7;
        DynamicProgramming.Result res = DynamicProgramming.solve(items, capacity);

        assertEquals(100, res.totalValue);
        assertEquals(7, res.totalWeight);
        assertEquals(2, res.selectedItems.length);
        assertEquals("R001", res.selectedItems[0].id);
        assertEquals("R004", res.selectedItems[1].id);
    }

    @Test
    public void knapsack_zeroCapacity_returnsZeroValueAndEmptySelection() {
        Item[] items = new Item[] {
            new Item("R001", 2, 30),
            new Item("R002", 3, 40)
        };
        DynamicProgramming.Result res = DynamicProgramming.solve(items, 0);

        assertEquals(0, res.totalValue);
        assertEquals(0, res.totalWeight);
        assertEquals(0, res.selectedItems.length);
    }

    @Test
    public void knapsack_emptyItemList_returnsZeroValue() {
        Item[] items = new Item[0];
        DynamicProgramming.Result res = DynamicProgramming.solve(items, 10);

        assertEquals(0, res.totalValue);
        assertEquals(0, res.totalWeight);
        assertEquals(0, res.selectedItems.length);
    }

    @Test
    public void knapsack_itemExceedsCapacity_skipsItem() {
        Item[] items = new Item[] {
            new Item("BigItem", 100, 1000),
            new Item("SmallItem", 3, 25)
        };
        DynamicProgramming.Result res = DynamicProgramming.solve(items, 5);

        assertEquals(25, res.totalValue);
        assertEquals(3, res.totalWeight);
        assertEquals(1, res.selectedItems.length);
        assertEquals("SmallItem", res.selectedItems[0].id);
    }

    @Test
    public void knapsack_exactCapacityMatch_includesItem() {
        Item[] items = new Item[] {
            new Item("Exact", 10, 500)
        };
        DynamicProgramming.Result res = DynamicProgramming.solve(items, 10);

        assertEquals(500, res.totalValue);
        assertEquals(10, res.totalWeight);
        assertEquals(1, res.selectedItems.length);
        assertEquals("Exact", res.selectedItems[0].id);
    }

    @Test
    public void knapsack_allSameWeightDifferentValues_picksHighestValue() {
        Item[] items = new Item[] {
            new Item("Low", 5, 10),
            new Item("High", 5, 100),
            new Item("Med", 5, 50)
        };
        DynamicProgramming.Result res = DynamicProgramming.solve(items, 5);

        assertEquals(100, res.totalValue);
        assertEquals(5, res.totalWeight);
        assertEquals(1, res.selectedItems.length);
        assertEquals("High", res.selectedItems[0].id);
    }
}
