package structures;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * A generic disjoint-set (Union-Find) implemented from scratch.
 *
 * Internally, each distinct element of type T is assigned an integer index.
 * The classic union-find algorithm (parent array + rank array) operates on
 * those indices. Path compression and union by rank keep operations close
 * to O(1) amortized (technically O(alpha(n)), the inverse Ackermann function).
 *
 * @param <T> the type of elements grouped into disjoint sets
 */
public class GenericDisjointSet<T> {

    private int[] parent;   // parent[i] = index of i's parent; parent[i] == i means i is a root
    private int[] rank;     // rank[i] = an upper bound on the height of the tree rooted at i
    private Map<T, Integer> indexOf; // maps each element to its internal index
    private int count;      // number of distinct sets currently

    public GenericDisjointSet(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        parent = new int[capacity];
        rank = new int[capacity];
        indexOf = new HashMap<>();
        count = 0;
    }

    /**
     * Registers a new element as its own singleton set.
     * Must be called before union() or find() are used on this element.
     */
    public void makeSet(T element) {
        if (indexOf.containsKey(element)) {
            return; // already registered, no-op
        }
        int index = indexOf.size();
        if (index >= parent.length) {
            throw new IllegalStateException("Disjoint set capacity exceeded");
        }
        indexOf.put(element, index);
        parent[index] = index; // a fresh element is its own root
        rank[index] = 0;
        count++;
    }

    /**
     * Finds the representative (root) index for the given element's set,
     * applying path compression along the way.
     */
    private int findIndex(int i) {
        if (parent[i] != i) {
            parent[i] = findIndex(parent[i]); // path compression: point directly to the root
        }
        return parent[i];
    }

    /**
     * Returns the representative element for the set containing the given element.
     * Throws NoSuchElementException if the element was never registered via makeSet().
     */
    public T find(T element) {
        Integer index = indexOf.get(element);
        if (index == null) {
            throw new NoSuchElementException("Element not found: " + element);
        }
        int rootIndex = findIndex(index);

        // Recover the T element corresponding to rootIndex.
        for (Map.Entry<T, Integer> entry : indexOf.entrySet()) {
            if (entry.getValue() == rootIndex) {
                return entry.getKey();
            }
        }
        throw new IllegalStateException("Root index had no corresponding element");
    }

    /**
     * Merges the sets containing elementA and elementB, using union by rank.
     * Returns true if a merge happened, false if they were already in the same set.
     */
    public boolean union(T elementA, T elementB) {
        Integer indexA = indexOf.get(elementA);
        Integer indexB = indexOf.get(elementB);
        if (indexA == null || indexB == null) {
            throw new NoSuchElementException("Both elements must be registered via makeSet() first");
        }

        int rootA = findIndex(indexA);
        int rootB = findIndex(indexB);

        if (rootA == rootB) {
            return false; // already in the same set
        }

        // Union by rank: attach the shorter tree under the taller one,
        // to keep the overall tree height small.
        if (rank[rootA] < rank[rootB]) {
            parent[rootA] = rootB;
        } else if (rank[rootA] > rank[rootB]) {
            parent[rootB] = rootA;
        } else {
            parent[rootB] = rootA;
            rank[rootA]++;
        }

        count--;
        return true;
    }

    /**
     * Returns true if elementA and elementB are currently in the same set.
     */
    public boolean connected(T elementA, T elementB) {
        return find(elementA).equals(find(elementB));
    }

    /**
     * Returns the current number of distinct disjoint sets.
     */
    public int setCount() {
        return count;
    }

    /**
     * Simple demonstration / manual test using a library-style scenario:
     * grouping book titles into collections/series so a query like
     * "are these two books in the same series?" is a fast connected() check.
     */
    public static void main(String[] args) {
        GenericDisjointSet<String> collections = new GenericDisjointSet<>(10);

        String[] books = {
                "Changes: A Love Story",
                "No Sweetness Here",
                "The Famished Road",
                "Songs of Enchantment",
                "Beasts of No Nation"
        };
        for (String book : books) {
            collections.makeSet(book);
        }

        // Group "The Famished Road" and "Songs of Enchantment" as the same series.
        collections.union("The Famished Road", "Songs of Enchantment");

        System.out.println("Number of distinct groups: " + collections.setCount());
        System.out.println("Famished Road & Songs of Enchantment same group? "
                + collections.connected("The Famished Road", "Songs of Enchantment"));
        System.out.println("Famished Road & Beasts of No Nation same group? "
                + collections.connected("The Famished Road", "Beasts of No Nation"));
    }
}
