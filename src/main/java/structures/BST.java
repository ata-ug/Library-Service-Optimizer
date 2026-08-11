package structures;

import java.util.Comparator;
import java.util.function.Consumer;


public class BST<T> {

    // A single node in the tree
    private static class Node<T> {
        T value;
        Node<T> left;
        Node<T> right;

        Node(T value) {
            this.value = value;
            this.left = null;
            this.right = null;
        }
    }

    private Node<T> root;              
    private int size;                  
    private Comparator<T> comparator;  


    public BST() {
        root = null;
        size = 0;
        comparator = null;
    }


    public BST(Comparator<T> comparator) {
        root = null;
        size = 0;
        this.comparator = comparator;
    }


    @SuppressWarnings("unchecked")
    private int compare(T a, T b) {
        if (comparator != null) {
            return comparator.compare(a, b);
        }
        // Natural ordering: T is assumed to implement Comparable<T>.
        return ((Comparable<T>) a).compareTo(b);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    // Insert a value into the tree
    public void insert(T value) {
        root = insertHelper(root, value);
    }

    private Node<T> insertHelper(Node<T> node, T value) {
        // If we found an empty spot, put the new value here
        if (node == null) {
            size++;
            return new Node<>(value);
        }

        int comparison = compare(value, node.value);

        if (comparison < 0) {
            node.left = insertHelper(node.left, value);
        } else if (comparison > 0) {
            node.right = insertHelper(node.right, value);
        }


        return node;
    }

    // Search for a value
    public boolean search(T value) {
        return searchHelper(root, value);
    }

    private boolean searchHelper(Node<T> node, T value) {
        if (node == null) {
            return false; // reached the end, value not found
        }

        int comparison = compare(value, node.value);

        if (comparison == 0) {
            return true;
        } else if (comparison < 0) {
            return searchHelper(node.left, value);
        } else {
            return searchHelper(node.right, value);
        }
    }

    // Delete a value
    public void delete(T value) {
        root = deleteHelper(root, value);
    }

    private Node<T> deleteHelper(Node<T> node, T value) {
        if (node == null) {
            return null; // value not in tree, nothing to delete
        }

        int comparison = compare(value, node.value);

        if (comparison < 0) {
            node.left = deleteHelper(node.left, value);
        } else if (comparison > 0) {
            node.right = deleteHelper(node.right, value);
        } else {
            // this is the node we want to delete

            // Case 1: no children
            if (node.left == null && node.right == null) {
                size--;
                return null;
            }

            // Case 2: only one child
            if (node.left == null) {
                size--;
                return node.right;
            }
            if (node.right == null) {
                size--;
                return node.left;
            }

            // Case 3: two children
            // Find the smallest value in the right subtree
            // (this is called the "in-order successor")
            T smallestInRight = findMin(node.right);

            // Copy that value into this node
            node.value = smallestInRight;

            // Now delete that smallest value from the right subtree
            // (it has been duplicated, so remove the original)
            node.right = deleteHelper(node.right, smallestInRight);

            // note: size is reduced inside the recursive call above,
            // so we don't reduce it again here
        }

        return node;
    }

    private T findMin(Node<T> node) {
        while (node.left != null) {
            node = node.left;
        }
        return node.value;
    }

    
    // Height of the tree (longest path from root to a leaf)
    public int height() {
        return heightHelper(root);
    }

    private int heightHelper(Node<T> node) {
        if (node == null) {
            return -1;
        }
        int leftHeight = heightHelper(node.left);
        int rightHeight = heightHelper(node.right);

        if (leftHeight > rightHeight) {
            return leftHeight + 1;
        } else {
            return rightHeight + 1;
        }
    }



    public void inorder(Consumer<T> action) {
        inorderVisit(root, action);
    }

    private void inorderVisit(Node<T> node, Consumer<T> action) {
        if (node == null) return;
        inorderVisit(node.left, action);
        action.accept(node.value);
        inorderVisit(node.right, action);
    }

    public void preorder(Consumer<T> action) {
        preorderVisit(root, action);
    }

    private void preorderVisit(Node<T> node, Consumer<T> action) {
        if (node == null) return;
        action.accept(node.value);
        preorderVisit(node.left, action);
        preorderVisit(node.right, action);
    }

    public void postorder(Consumer<T> action) {
        postorderVisit(root, action);
    }

    private void postorderVisit(Node<T> node, Consumer<T> action) {
        if (node == null) return;
        postorderVisit(node.left, action);
        postorderVisit(node.right, action);
        action.accept(node.value);
    }

    // Traversals - print the values in each order. These are now just
    // thin wrappers around the programmatic versions above, so there's
    // one single source of truth for the actual walk logic.

    public void printInorder() {
        inorder(value -> System.out.println(" - " + value));
        System.out.println();
    }

    public void printPreorder() {
        preorder(value -> System.out.println(" - " + value));
        System.out.println();
    }

    public void printPostorder() {
        postorder(value -> System.out.println(" - " + value));
        System.out.println();
    }

}
