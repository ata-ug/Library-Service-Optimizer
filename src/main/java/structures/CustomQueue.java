package structures;

import java.util.NoSuchElementException;

public class CustomQueue<T> {
    private static class Node<T> {
        T data; Node<T> next;
        Node(T data) { this.data = data; }
    }
    private Node<T> front; private Node<T> rear; private int size;
    public CustomQueue() { front = rear = null; size = 0; }

    public void enqueue(T data) {
        Node<T> newNode = new Node<>(data);
        if (isEmpty()) { front = rear = newNode; }
        else { rear.next = newNode; rear = newNode; }
        size++;
    }
    public T dequeue() {
        if (isEmpty()) throw new NoSuchElementException("Queue is empty");
        T data = front.data; front = front.next;
        if (front == null) rear = null;
        size--; return data;
    }
    public T peek() {
        if (isEmpty()) throw new NoSuchElementException("Queue is empty");
        return front.data;
    }
    public boolean isEmpty() { return size == 0; }
    public int size() { return size; }
    public void clear() { front = rear = null; size = 0; }
}
