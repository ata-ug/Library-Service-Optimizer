package structures;

public class DynamicArray<T> {

    private static final int DEFAULT_CAPACITY = 8;

    private Object[] data;
    private int size;

    public DynamicArray() {
        data = new Object[DEFAULT_CAPACITY];
        size = 0;
    }

    public DynamicArray(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Initial capacity must be positive");
        }
        data = new Object[initialCapacity];
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int capacity() {
        return data.length;
    }

    public void insert(T value) {
        ensureCapacityForOneMore();
        data[size] = value;
        size++;
    }

    public void insert(int index, T value) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        ensureCapacityForOneMore();

        for (int i = size; i > index; i--) {
            data[i] = data[i - 1];
        }
        data[index] = value;
        size++;
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        checkIndex(index);
        return (T) data[index];
    }

    public void set(int index, T value) {
        checkIndex(index);
        data[index] = value;
    }

    @SuppressWarnings("unchecked")
    public T remove(int index) {
        checkIndex(index);
        T removed = (T) data[index];

        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
        }
        data[size - 1] = null;
        size--;

        shrinkIfSparse();
        return removed;
    }

    public boolean remove(T value) {
        for (int i = 0; i < size; i++) {
            boolean matches = (data[i] == null) ? (value == null) : data[i].equals(value);
            if (matches) {
                remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean contains(T value) {
        for (int i = 0; i < size; i++) {
            boolean matches = (data[i] == null) ? (value == null) : data[i].equals(value);
            if (matches) {
                return true;
            }
        }
        return false;
    }

    public void clear() {
        data = new Object[DEFAULT_CAPACITY];
        size = 0;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    private void ensureCapacityForOneMore() {
        if (size == data.length) {
            resize(data.length * 2);
        }
    }

    private void shrinkIfSparse() {
        if (data.length > DEFAULT_CAPACITY && size <= data.length / 4) {
            int newCapacity = Math.max(DEFAULT_CAPACITY, data.length / 2);
            resize(newCapacity);
        }
    }

    private void resize(int newCapacity) {
        Object[] newData = new Object[newCapacity];
        for (int i = 0; i < size; i++) {
            newData[i] = data[i];
        }
        data = newData;
    }
}
