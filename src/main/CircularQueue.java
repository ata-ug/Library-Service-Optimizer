import java.util.NoSuchElementException;

public class CircularQueue<T> {
    private Object[] array;
    private int front; private int rear; private int size; private int capacity;

    public CircularQueue(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be > 0");
        this.capacity = capacity;
        this.array = new Object[capacity];
        this.front = 0; this.rear = 0; this.size = 0;
    }

    public void enqueue(T data) {
        if (isFull()) throw new IllegalStateException("Queue is full (capacity: " + capacity + ")");
        array[rear] = data;
        rear = (rear + 1) % capacity;
        size++;
    }

    @SuppressWarnings("unchecked")
    public T dequeue() {
        if (isEmpty()) throw new NoSuchElementException("Queue is empty");
        T data = (T) array[front];
        array[front] = null;
        front = (front + 1) % capacity;
        size--; return data;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) throw new NoSuchElementException("Queue is empty");
        return (T) array[front];
    }

    public boolean isEmpty() { return size == 0; }
    public boolean isFull() { return size == capacity; }
    public int size() { return size; }
    public int capacity() { return capacity; }
    public void clear() {
        for (int i = 0; i < capacity; i++) array[i] = null;
        front = rear = size = 0;
    }
}
