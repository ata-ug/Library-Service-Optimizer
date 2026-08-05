package structures;

import java.util.EmptyStackException;

/**
 * A generic stack implemented from scratch using a dynamically resizing array.
 *
 * LIFO (Last-In, First-Out) behavior: the most recently pushed element
 * is the first one popped.
 *
 * @param <T> the type of elements held in this stack
 */
public class GenericStack<T> {

    private T[] data;
    private int top;               // index of the next free slot (also = current size)
    private static final int DEFAULT_CAPACITY = 16;

    @SuppressWarnings("unchecked")
    public GenericStack() {
        this.data = (T[]) new Object[DEFAULT_CAPACITY];
        this.top = 0;
    }

    @SuppressWarnings("unchecked")
    public GenericStack(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Initial capacity must be positive");
        }
        this.data = (T[]) new Object[initialCapacity];
        this.top = 0;
    }

    /**
     * Pushes an item onto the top of the stack.
     */
    public void push(T item) {
        if (top == data.length) {
            resize(data.length * 2);
        }
        data[top] = item;
        top++;
    }

    /**
     * Removes and returns the item at the top of the stack.
     * Throws EmptyStackException if the stack is empty.
     */
    public T pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        top--;
        T item = data[top];
        data[top] = null; // avoid holding a stale reference (helps garbage collection)

        // Shrink the array if it's getting sparse, to avoid wasting memory
        // after a burst of pushes followed by many pops.
        if (top > 0 && top == data.length / 4) {
            resize(data.length / 2);
        }
        return item;
    }

    /**
     * Returns the item at the top of the stack without removing it.
     * Throws EmptyStackException if the stack is empty.
     */
    public T peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return data[top - 1];
    }

    public boolean isEmpty() {
        return top == 0;
    }

    public int size() {
        return top;
    }

    /**
     * Resizes the backing array to the given capacity and copies existing
     * elements over. Used internally by push (growing) and pop (shrinking).
     */
    @SuppressWarnings("unchecked")
    private void resize(int newCapacity) {
        T[] newData = (T[]) new Object[newCapacity];
        for (int i = 0; i < top; i++) {
            newData[i] = data[i];
        }
        data = newData;
    }

    /**
     * Simple demonstration / manual test using a library-style scenario:
     * tracking a member's recent borrow actions so the most recent one
     * can be undone first.
     */
    public static void main(String[] args) {
        GenericStack<String> recentActions = new GenericStack<>();

        recentActions.push("Borrowed: Changes: A Love Story");
        recentActions.push("Returned: Beasts of No Nation");
        recentActions.push("Borrowed: The Famished Road");

        System.out.println("Peek (most recent action): " + recentActions.peek());
        System.out.println("Size: " + recentActions.size());

        System.out.println("Undo: " + recentActions.pop());
        System.out.println("Undo: " + recentActions.pop());
        System.out.println("Size after two undos: " + recentActions.size());
    }
}
