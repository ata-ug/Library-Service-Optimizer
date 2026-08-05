package structures;

import java.util.NoSuchElementException;

/**
 * A generic hashtable implemented from scratch using separate chaining
 * for collision resolution.
 *
 * Backing structure: an array of singly linked lists ("buckets").
 * Each bucket holds Entry<K,V> nodes for keys that hash to that index.
 *
 * @param <K> the type of keys maintained by this hashtable
 * @param <V> the type of mapped values
 */
public class GenericHashtable<K, V> {

    // A single key-value pair stored in a bucket's linked list.
    private static class Entry<K, V> {
        K key;
        V value;
        Entry<K, V> next; // next node in the same bucket (chaining)

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }
    }

    private Entry<K, V>[] buckets;
    private int size;              // number of key-value pairs stored
    private int capacity;          // number of buckets
    private static final int DEFAULT_CAPACITY = 16;
    private static final double LOAD_FACTOR_THRESHOLD = 0.75;

    @SuppressWarnings("unchecked")
    public GenericHashtable() {
        this.capacity = DEFAULT_CAPACITY;
        this.buckets = new Entry[capacity];
        this.size = 0;
    }

    @SuppressWarnings("unchecked")
    public GenericHashtable(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Initial capacity must be positive");
        }
        this.capacity = initialCapacity;
        this.buckets = new Entry[capacity];
        this.size = 0;
    }

    /**
     * Computes the bucket index for a given key.
     * Uses the key's hashCode(), masks off the sign bit, then mods by capacity.
     */
    private int indexFor(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Null keys are not supported");
        }
        int hash = key.hashCode();
        // Strip the sign bit so we never get a negative index.
        hash = hash & 0x7fffffff;
        return hash % capacity;
    }

    /**
     * Inserts a key-value pair, or updates the value if the key already exists.
     */
    public void put(K key, V value) {
        if ((double) (size + 1) / capacity > LOAD_FACTOR_THRESHOLD) {
            resize();
        }

        int index = indexFor(key);
        Entry<K, V> current = buckets[index];

        // Walk the chain to see if the key already exists.
        while (current != null) {
            if (current.key.equals(key)) {
                current.value = value; // update existing
                return;
            }
            current = current.next;
        }

        // Key not found: insert new entry at the head of the bucket's chain.
        Entry<K, V> newEntry = new Entry<>(key, value);
        newEntry.next = buckets[index];
        buckets[index] = newEntry;
        size++;
    }

    /**
     * Returns the value associated with the given key.
     * Throws NoSuchElementException if the key is not present.
     */
    public V get(K key) {
        int index = indexFor(key);
        Entry<K, V> current = buckets[index];

        while (current != null) {
            if (current.key.equals(key)) {
                return current.value;
            }
            current = current.next;
        }

        throw new NoSuchElementException("Key not found: " + key);
    }

    /**
     * Returns true if the key exists in the hashtable.
     */
    public boolean containsKey(K key) {
        int index = indexFor(key);
        Entry<K, V> current = buckets[index];

        while (current != null) {
            if (current.key.equals(key)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    /**
     * Removes the key-value pair for the given key, if present.
     * Returns the removed value, or null if the key was not found.
     */
    public V remove(K key) {
        int index = indexFor(key);
        Entry<K, V> current = buckets[index];
        Entry<K, V> previous = null;

        while (current != null) {
            if (current.key.equals(key)) {
                if (previous == null) {
                    buckets[index] = current.next; // removing head of chain
                } else {
                    previous.next = current.next; // splice out
                }
                size--;
                return current.value;
            }
            previous = current;
            current = current.next;
        }
        return null; // key not found
    }

    /**
     * Doubles the number of buckets and re-inserts all existing entries.
     * Called automatically once the load factor threshold is exceeded.
     */
    @SuppressWarnings("unchecked")
    private void resize() {
        Entry<K, V>[] oldBuckets = buckets;
        capacity = capacity * 2;
        buckets = new Entry[capacity];
        size = 0; // will be recounted as we re-insert

        for (Entry<K, V> head : oldBuckets) {
            Entry<K, V> current = head;
            while (current != null) {
                put(current.key, current.value); // rehash into new buckets
                current = current.next;
            }
        }
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Simple demonstration / manual test using library-style data:
     * mapping book ISBNs (String keys) to book titles (String values).
     */
    public static void main(String[] args) {
        GenericHashtable<String, String> catalog = new GenericHashtable<>();

        catalog.put("978-9988-0001", "Beasts of No Nation");
        catalog.put("978-9988-0002", "The Beautyful Ones Are Not Yet Born");
        catalog.put("978-9988-0003", "Changes: A Love Story");

        System.out.println("Lookup 978-9988-0002: " + catalog.get("978-9988-0002"));
        System.out.println("Contains 978-9988-0001? " + catalog.containsKey("978-9988-0001"));
        System.out.println("Size before removal: " + catalog.size());

        catalog.remove("978-9988-0001");
        System.out.println("Size after removal: " + catalog.size());
        System.out.println("Contains 978-9988-0001 now? " + catalog.containsKey("978-9988-0001"));
    }
}
