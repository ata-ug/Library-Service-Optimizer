package structures;

import java.util.Comparator;
import java.util.function.Consumer;


public class BTree<T> {

    private static final int DEFAULT_MIN_DEGREE = 3;

    // A single B-tree node. Unlike BST/RedBlackTree's Node, this one
    // holds an ARRAY of keys and an array of children, not just one
    // value and two child pointers.
    @SuppressWarnings("unchecked")
    private static class Node<T> {
        Object[] keys;       // up to (2t - 1) keys, kept sorted
        Node<T>[] children;  // up to 2t children (only used if !leaf)
        int keyCount;        // how many keys are actually in use right now
        boolean leaf;

        Node(int minDegree, boolean leaf) {
            keys = new Object[2 * minDegree - 1];
            children = new Node[2 * minDegree];
            keyCount = 0;
            this.leaf = leaf;
        }

        T key(int i) {
            return (T) keys[i];
        }
    }

    private Node<T> root;
    private final int minDegree;
    private int size;
    private Comparator<T> comparator; // null means "use natural ordering (Comparable)"

    public BTree() {
        this(DEFAULT_MIN_DEGREE);
    }

    public BTree(int minDegree) {
        if (minDegree < 2) {
            throw new IllegalArgumentException("Minimum degree must be at least 2");
        }
        this.minDegree = minDegree;
        this.root = new Node<>(minDegree, true);
        this.size = 0;
        this.comparator = null;
    }

    // Comparator-based tree, same pattern as BST.java and RedBlackTree.java,
    // for types that don't implement Comparable themselves (e.g. Book).
    public BTree(Comparator<T> comparator) {
        this(DEFAULT_MIN_DEGREE, comparator);
    }

    public BTree(int minDegree, Comparator<T> comparator) {
        if (minDegree < 2) {
            throw new IllegalArgumentException("Minimum degree must be at least 2");
        }
        this.minDegree = minDegree;
        this.root = new Node<>(minDegree, true);
        this.size = 0;
        this.comparator = comparator;
    }

    @SuppressWarnings("unchecked")
    private int compare(T a, T b) {
        if (comparator != null) {
            return comparator.compare(a, b);
        }
        return ((Comparable<T>) a).compareTo(b);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    // Search
    public boolean search(T value) {
        return searchNode(root, value) != null;
    }

    private Node<T> searchNode(Node<T> node, T value) {
        int i = 0;
        while (i < node.keyCount && compare(value, node.key(i)) > 0) {
            i++;
        }
        if (i < node.keyCount && compare(value, node.key(i)) == 0) {
            return node; // found it in this node
        }
        if (node.leaf) {
            return null; // reached the bottom, not found
        }
        return searchNode(node.children[i], value);
    }

 

    public void insert(T value) {
        if (search(value)) {
            return; // no duplicates allowed, same rule as BST.java
        }

        if (root.keyCount == 2 * minDegree - 1) {
            // root is full - the tree grows one level taller here.
            Node<T> newRoot = new Node<>(minDegree, false);
            newRoot.children[0] = root;
            splitChild(newRoot, 0);
            root = newRoot;
        }
        insertNonFull(root, value);
        size++;
    }

    // Splits the full child at parent.children[i] into two nodes with
    // (minDegree - 1) keys each, and moves the middle key up into parent.
    private void splitChild(Node<T> parent, int i) {
        Node<T> fullChild = parent.children[i];
        Node<T> newSibling = new Node<>(minDegree, fullChild.leaf);
        newSibling.keyCount = minDegree - 1;

        // The larger half of fullChild's keys move to newSibling.
        for (int j = 0; j < minDegree - 1; j++) {
            newSibling.keys[j] = fullChild.keys[j + minDegree];
        }
        if (!fullChild.leaf) {
            for (int j = 0; j < minDegree; j++) {
                newSibling.children[j] = fullChild.children[j + minDegree];
            }
        }

        T middleKey = fullChild.key(minDegree - 1);
        fullChild.keyCount = minDegree - 1; // fullChild keeps the smaller half

        // Make room in parent for the new child pointer.
        for (int j = parent.keyCount; j >= i + 1; j--) {
            parent.children[j + 1] = parent.children[j];
        }
        parent.children[i + 1] = newSibling;

        // Make room in parent for the middle key moving up.
        for (int j = parent.keyCount - 1; j >= i; j--) {
            parent.keys[j + 1] = parent.keys[j];
        }
        parent.keys[i] = middleKey;
        parent.keyCount++;
    }

    // Inserts into a subtree rooted at a node we already know is NOT full.
    private void insertNonFull(Node<T> node, T value) {
        int i = node.keyCount - 1;

        if (node.leaf) {
            // Shift larger keys right to make room, then place the value.
            while (i >= 0 && compare(value, node.key(i)) < 0) {
                node.keys[i + 1] = node.keys[i];
                i--;
            }
            node.keys[i + 1] = value;
            node.keyCount++;
        } else {
            while (i >= 0 && compare(value, node.key(i)) < 0) {
                i--;
            }
            i++; // i is now the index of the child to descend into

            if (node.children[i].keyCount == 2 * minDegree - 1) {
                splitChild(node, i);
                if (compare(value, node.key(i)) > 0) {
                    i++; // the split may mean the value now belongs one child over
                }
            }
            insertNonFull(node.children[i], value);
        }
    }

    // Delete

    public void delete(T value) {
        if (!search(value)) {
            return; // value not found, nothing to delete
        }

        deleteFromNode(root, value);

        // If the root lost its only key (because two of its children
        // just got merged into one), the tree shrinks by one level.
        if (root.keyCount == 0 && !root.leaf) {
            root = root.children[0];
        }
        size--;
    }

    private void deleteFromNode(Node<T> node, T value) {
        int i = 0;
        while (i < node.keyCount && compare(value, node.key(i)) > 0) {
            i++;
        }

        if (i < node.keyCount && compare(value, node.key(i)) == 0) {
            // The value lives in this node.
            if (node.leaf) {
                removeFromLeaf(node, i);
            } else {
                removeFromInternal(node, i);
            }
        } else {
            if (node.leaf) {
                return; // shouldn't happen - delete() already confirmed the value exists
            }

            boolean valueIsInLastSubtree = (i == node.keyCount);

            if (node.children[i].keyCount < minDegree) {
                fill(node, i);
            }

            // fill() may have merged children together, shifting indices,
            // so re-check which child to descend into.
            if (valueIsInLastSubtree && i > node.keyCount) {
                deleteFromNode(node.children[i - 1], value);
            } else {
                deleteFromNode(node.children[i], value);
            }
        }
    }

    private void removeFromLeaf(Node<T> node, int i) {
        for (int j = i + 1; j < node.keyCount; j++) {
            node.keys[j - 1] = node.keys[j];
        }
        node.keyCount--;
    }

    private void removeFromInternal(Node<T> node, int i) {
        T key = node.key(i);

        if (node.children[i].keyCount >= minDegree) {
            // Replace with the predecessor (largest key in the left subtree),
            // then delete that predecessor from where it actually lives.
            T predecessor = getPredecessor(node, i);
            node.keys[i] = predecessor;
            deleteFromNode(node.children[i], predecessor);
        } else if (node.children[i + 1].keyCount >= minDegree) {
            // Same idea, using the successor (smallest key in the right subtree).
            T successor = getSuccessor(node, i);
            node.keys[i] = successor;
            deleteFromNode(node.children[i + 1], successor);
        } else {
            // Both neighboring children are at the minimum - merge them
            // (with this key sandwiched between), then delete from the merged node.
            merge(node, i);
            deleteFromNode(node.children[i], key);
        }
    }

    private T getPredecessor(Node<T> node, int i) {
        Node<T> current = node.children[i];
        while (!current.leaf) {
            current = current.children[current.keyCount];
        }
        return current.key(current.keyCount - 1);
    }

    private T getSuccessor(Node<T> node, int i) {
        Node<T> current = node.children[i + 1];
        while (!current.leaf) {
            current = current.children[0];
        }
        return current.key(0);
    }

    // Makes sure node.children[i] has at least minDegree keys before we
    // recurse into it, by borrowing a key from a sibling if one has
    // spare keys, or merging with a sibling if neither does.
    private void fill(Node<T> node, int i) {
        if (i != 0 && node.children[i - 1].keyCount >= minDegree) {
            borrowFromPrev(node, i);
        } else if (i != node.keyCount && node.children[i + 1].keyCount >= minDegree) {
            borrowFromNext(node, i);
        } else if (i != node.keyCount) {
            merge(node, i);
        } else {
            merge(node, i - 1);
        }
    }

    // Moves one key down from the parent into children[i] (at the front),
    // and moves the previous sibling's last key up to take its place.
    private void borrowFromPrev(Node<T> node, int i) {
        Node<T> child = node.children[i];
        Node<T> sibling = node.children[i - 1];

        for (int j = child.keyCount - 1; j >= 0; j--) {
            child.keys[j + 1] = child.keys[j];
        }
        if (!child.leaf) {
            for (int j = child.keyCount; j >= 0; j--) {
                child.children[j + 1] = child.children[j];
            }
        }

        child.keys[0] = node.keys[i - 1];
        if (!child.leaf) {
            child.children[0] = sibling.children[sibling.keyCount];
        }

        node.keys[i - 1] = sibling.key(sibling.keyCount - 1);

        child.keyCount++;
        sibling.keyCount--;
    }

    // Mirror image of borrowFromPrev: pulls a key from the NEXT sibling instead.
    private void borrowFromNext(Node<T> node, int i) {
        Node<T> child = node.children[i];
        Node<T> sibling = node.children[i + 1];

        child.keys[child.keyCount] = node.keys[i];
        if (!child.leaf) {
            child.children[child.keyCount + 1] = sibling.children[0];
        }

        node.keys[i] = sibling.key(0);

        for (int j = 1; j < sibling.keyCount; j++) {
            sibling.keys[j - 1] = sibling.keys[j];
        }
        if (!sibling.leaf) {
            for (int j = 1; j <= sibling.keyCount; j++) {
                sibling.children[j - 1] = sibling.children[j];
            }
        }

        child.keyCount++;
        sibling.keyCount--;
    }

    // Merges node.children[i], the key at node.keys[i], and
    // node.children[i+1] into a single node (stored back at children[i]).
    private void merge(Node<T> node, int i) {
        Node<T> child = node.children[i];
        Node<T> sibling = node.children[i + 1];

        child.keys[minDegree - 1] = node.keys[i];

        for (int j = 0; j < sibling.keyCount; j++) {
            child.keys[j + minDegree] = sibling.keys[j];
        }
        if (!child.leaf) {
            for (int j = 0; j <= sibling.keyCount; j++) {
                child.children[j + minDegree] = sibling.children[j];
            }
        }

        for (int j = i + 1; j < node.keyCount; j++) {
            node.keys[j - 1] = node.keys[j];
        }
        for (int j = i + 2; j <= node.keyCount; j++) {
            node.children[j - 1] = node.children[j];
        }

        child.keyCount += sibling.keyCount + 1;
        node.keyCount--;
    }

    
    // Height - number of edges from root down to a leaf. Because every
    // leaf sits at the same depth in a B-tree, we only ever need to
    // follow one path (always via children[0]) to find it
    public int height() {
        return heightHelper(root);
    }

    private int heightHelper(Node<T> node) {
        if (node.leaf) {
            return 0;
        }
        return 1 + heightHelper(node.children[0]);
    }

    // Programmatic inorder traversal - hands each value to a caller-
    // supplied action, IN SORTED ORDER. Same visitor pattern as
    // BST.java / RedBlackTree.java, so another squad can consume the
    // data without us returning an array or a java.util collection.
    public void inorder(Consumer<T> action) {
        inorderVisit(root, action);
    }

    private void inorderVisit(Node<T> node, Consumer<T> action) {
        int i;
        for (i = 0; i < node.keyCount; i++) {
            if (!node.leaf) {
                inorderVisit(node.children[i], action);
            }
            action.accept(node.key(i));
        }
        if (!node.leaf) {
            inorderVisit(node.children[i], action);
        }
    }

    // Validator - checks the real B-tree invariants against the tree's
    // actual internal state (same idea as RedBlackTree.isValid()):
    //   - every node's keys are sorted internally
    //   - no non-root node has fewer than (minDegree - 1) keys
    //   - no node has more than (2*minDegree - 1) keys
    //   - every leaf sits at the same depth
    public boolean isValid() {
        int leafDepth = findALeafDepth(root, 0);
        return checkNode(root, true, leafDepth, 0);
    }

    private int findALeafDepth(Node<T> node, int depth) {
        if (node.leaf) {
            return depth;
        }
        return findALeafDepth(node.children[0], depth + 1);
    }

    private boolean checkNode(Node<T> node, boolean isRoot, int expectedLeafDepth, int depth) {
        int minKeys = isRoot ? (node.leaf ? 0 : 1) : minDegree - 1;
        if (node.keyCount < minKeys || node.keyCount > 2 * minDegree - 1) {
            return false;
        }

        for (int i = 1; i < node.keyCount; i++) {
            if (compare(node.key(i - 1), node.key(i)) >= 0) {
                return false; // keys within a node must be strictly increasing
            }
        }

        if (node.leaf) {
            return depth == expectedLeafDepth;
        }

        for (int i = 0; i <= node.keyCount; i++) {
            if (!checkNode(node.children[i], false, expectedLeafDepth, depth + 1)) {
                return false;
            }
        }
        return true;
    }
}
