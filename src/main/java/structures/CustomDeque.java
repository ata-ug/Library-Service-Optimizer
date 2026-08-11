package structures;

import java.util.NoSuchElementException;

public class CustomDeque<T> {
    private static class Node<T> {
        T data; Node<T> prev; Node<T> next;
        Node(T data) { this.data = data; }
    }
    private Node<T> head; private Node<T> tail; private int size;
    public CustomDeque() { head = tail = null; size = 0; }

    public void addFirst(T data) {
        Node<T> newNode = new Node<>(data);
        if (isEmpty()) { head = tail = newNode; }
        else { newNode.next = head; head.prev = newNode; head = newNode; }
        size++;
    }
    public void addLast(T data) {
        Node<T> newNode = new Node<>(data);
        if (isEmpty()) { head = tail = newNode; }
        else { newNode.prev = tail; tail.next = newNode; tail = newNode; }
        size++;
    }
    public T removeFirst() {
        if (isEmpty()) throw new NoSuchElementException("Deque is empty");
        T data = head.data; head = head.next;
        if (head == null) tail = null; else head.prev = null;
        size--; return data;
    }
    public T removeLast() {
        if (isEmpty()) throw new NoSuchElementException("Deque is empty");
        T data = tail.data; tail = tail.prev;
        if (tail == null) head = null; else tail.next = null;
        size--; return data;
    }
    public T peekFirst() {
        if (isEmpty()) throw new NoSuchElementException("Deque is empty");
        return head.data;
    }
    public T peekLast() {
        if (isEmpty()) throw new NoSuchElementException("Deque is empty");
        return tail.data;
    }
    public boolean isEmpty() { return size == 0; }
    public int size() { return size; }
    public void clear() { head = tail = null; size = 0; }
}
