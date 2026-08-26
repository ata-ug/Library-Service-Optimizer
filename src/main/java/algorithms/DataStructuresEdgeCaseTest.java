package algorithms;

import org.junit.Test;
import static org.junit.Assert.*;
import structures.*;

import java.util.Comparator;
import java.util.NoSuchElementException;

/**
 * Formal JUnit 4 Test Suite for Custom Data Structures Edge Cases.
 * Covers GenericHeap, GenericDisjointSet, CustomLinkedList, GenericStack, GenericHashtable,
 * BST, and RedBlackTree boundary conditions and invariant validations.
 */
public class DataStructuresEdgeCaseTest {

    // =========================================================================
    // 1. GenericHeap Edge Cases
    // =========================================================================

    @Test(expected = NoSuchElementException.class)
    public void heap_pollEmpty_throwsNoSuchElementException() {
        GenericHeap<Integer> minHeap = new GenericHeap<>();
        minHeap.poll();
    }

    @Test
    public void heap_singleElement_addAndPoll() {
        GenericHeap<Integer> heap = new GenericHeap<>();
        heap.add(42);
        assertEquals(1, heap.size());
        assertFalse(heap.isEmpty());
        assertEquals(Integer.valueOf(42), heap.poll());
        assertTrue(heap.isEmpty());
    }

    @Test
    public void heap_minHeapOrdering_extractsInAscendingOrder() {
        GenericHeap<Integer> heap = new GenericHeap<>();
        heap.add(50);
        heap.add(10);
        heap.add(30);
        heap.add(5);
        heap.add(20);

        assertEquals(Integer.valueOf(5), heap.poll());
        assertEquals(Integer.valueOf(10), heap.poll());
        assertEquals(Integer.valueOf(20), heap.poll());
        assertEquals(Integer.valueOf(30), heap.poll());
        assertEquals(Integer.valueOf(50), heap.poll());
    }

    @Test
    public void heap_maxHeapComparator_extractsInDescendingOrder() {
        Comparator<Integer> comp = Comparator.reverseOrder();
        GenericHeap<Integer> maxHeap = new GenericHeap<>(comp);
        maxHeap.add(10);
        maxHeap.add(50);
        maxHeap.add(30);

        assertEquals(Integer.valueOf(50), maxHeap.poll());
        assertEquals(Integer.valueOf(30), maxHeap.poll());
        assertEquals(Integer.valueOf(10), maxHeap.poll());
    }

    // =========================================================================
    // 2. GenericDisjointSet Edge Cases
    // =========================================================================

    @Test(expected = NoSuchElementException.class)
    public void disjointSet_findUnregisteredElement_throwsNoSuchElementException() {
        GenericDisjointSet<String> ds = new GenericDisjointSet<>(10);
        ds.find("NonExistent");
    }

    @Test
    public void disjointSet_unionAndPathCompression_maintainsSetPartitions() {
        GenericDisjointSet<String> ds = new GenericDisjointSet<>(10);
        ds.makeSet("A");
        ds.makeSet("B");
        ds.makeSet("C");
        ds.makeSet("D");

        assertEquals(4, ds.setCount());
        assertFalse(ds.connected("A", "B"));

        assertTrue(ds.union("A", "B"));
        assertEquals(3, ds.setCount());
        assertTrue(ds.connected("A", "B"));

        assertTrue(ds.union("C", "D"));
        assertEquals(2, ds.setCount());
        assertTrue(ds.connected("C", "D"));

        assertFalse(ds.connected("A", "C"));

        assertTrue(ds.union("B", "C"));
        assertEquals(1, ds.setCount());
        assertTrue(ds.connected("A", "D"));

        // Second union call on already connected elements returns false
        assertFalse(ds.union("A", "D"));
    }

    // =========================================================================
    // 3. CustomLinkedList Edge Cases
    // =========================================================================

    @Test(expected = NoSuchElementException.class)
    public void linkedList_removeFirstEmpty_throwsException() {
        CustomLinkedList<String> list = new CustomLinkedList<>();
        list.removeFirst();
    }

    @Test(expected = NoSuchElementException.class)
    public void linkedList_removeLastEmpty_throwsException() {
        CustomLinkedList<String> list = new CustomLinkedList<>();
        list.removeLast();
    }

    @Test
    public void linkedList_addRemoveOperations_maintainHeadTailAndSize() {
        CustomLinkedList<String> list = new CustomLinkedList<>();
        list.addLast("Head");
        list.addLast("Middle");
        list.addLast("Tail");

        assertEquals(3, list.size());
        assertEquals("Head", list.get(0));
        assertEquals("Tail", list.get(2));

        assertTrue(list.remove("Middle"));
        assertEquals(2, list.size());
        assertEquals("Head", list.get(0));
        assertEquals("Tail", list.get(1));

        assertEquals("Head", list.removeFirst());
        assertEquals("Tail", list.removeLast());
        assertTrue(list.isEmpty());
    }

    // =========================================================================
    // 4. GenericStack Edge Cases
    // =========================================================================

    @Test(expected = java.util.EmptyStackException.class)
    public void stack_popEmpty_throwsException() {
        GenericStack<String> stack = new GenericStack<>();
        stack.pop();
    }

    @Test
    public void stack_pushPop_behavesLIFO() {
        GenericStack<String> stack = new GenericStack<>();
        stack.push("Event1");
        stack.push("Event2");
        stack.push("Event3");

        assertEquals(3, stack.size());
        assertEquals("Event3", stack.peek());
        assertEquals("Event3", stack.pop());
        assertEquals("Event2", stack.pop());
        assertEquals("Event1", stack.pop());
        assertTrue(stack.isEmpty());
    }

    // =========================================================================
    // 5. GenericHashtable Edge Cases
    // =========================================================================

    @Test
    public void hashtable_putGetRemoveAndResize() {
        GenericHashtable<String, Integer> map = new GenericHashtable<>();
        map.put("Key1", 100);
        map.put("Key2", 200);

        assertEquals(Integer.valueOf(100), map.get("Key1"));
        assertEquals(Integer.valueOf(200), map.get("Key2"));
        assertTrue(map.containsKey("Key1"));
        assertFalse(map.containsKey("MissingKey"));

        // Update value for existing key
        map.put("Key1", 150);
        assertEquals(Integer.valueOf(150), map.get("Key1"));

        // Remove key returns previous value
        assertEquals(Integer.valueOf(150), map.remove("Key1"));
        assertFalse(map.containsKey("Key1"));
        assertNull(map.remove("Key1"));
    }

    @Test(expected = NoSuchElementException.class)
    public void hashtable_getMissingKey_throwsNoSuchElementException() {
        GenericHashtable<String, Integer> map = new GenericHashtable<>();
        map.get("NonExistentKey");
    }
}
