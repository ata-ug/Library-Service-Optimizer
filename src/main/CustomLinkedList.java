import java.util.Iterator;
import java.util.NoSuchElementException;

public class CustomLinkedList<T> implements Iterable<T> {

    private static class Node<T> {
        T data;
        Node<T> next;
        Node(T data) { this.data = data; this.next = null; }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public CustomLinkedList() { head = tail = null; size = 0; }

    public void addFirst(T data) {
        Node<T> newNode = new Node<>(data);
        if (isEmpty()) { head = tail = newNode; }
        else { newNode.next = head; head = newNode; }
        size++;
    }

    public void addLast(T data) {
        Node<T> newNode = new Node<>(data);
        if (isEmpty()) { head = tail = newNode; }
        else { tail.next = newNode; tail = newNode; }
        size++;
    }

    public T removeFirst() {
        if (isEmpty()) throw new NoSuchElementException("List is empty");
        T data = head.data; head = head.next;
        if (head == null) tail = null;
        size--; return data;
    }

    public T removeLast() {
        if (isEmpty()) throw new NoSuchElementException("List is empty");
        if (head == tail) return removeFirst();
        Node<T> current = head;
        while (current.next != tail) { current = current.next; }
        T data = tail.data; current.next = null; tail = current;
        size--; return data;
    }

    public boolean remove(T data) {
        if (isEmpty()) return false;
        if (head.data.equals(data)) { removeFirst(); return true; }
        Node<T> current = head;
        while (current.next != null && !current.next.data.equals(data)) { current = current.next; }
        if (current.next != null) {
            if (current.next == tail) tail = current;
            current.next = current.next.next; size--; return true;
        }
        return false;
    }

    public T get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        Node<T> current = head;
        for (int i = 0; i < index; i++) current = current.next;
        return current.data;
    }

    public boolean contains(T data) {
        Node<T> current = head;
        while (current != null) { if (current.data.equals(data)) return true; current = current.next; }
        return false;
    }

    public boolean isEmpty() { return size == 0; }
    public int size() { return size; }
    public void clear() { head = tail = null; size = 0; }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = head;
            public boolean hasNext() { return current != null; }
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                T data = current.data; current = current.next; return data;
            }
        };
    }
}
