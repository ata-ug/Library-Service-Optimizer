package structures;

import java.util.Comparator;
import java.util.function.Consumer;


public class RedBlackTree<T> {

    private static final boolean RED = true;
    private static final boolean BLACK = false;

    private static class Node<T> {
        T value;
        Node<T> left;
        Node<T> right;
        Node<T> parent;
        boolean color;

        Node(T value) {
            this.value = value;
        }
    }

    private final Node<T> NIL;   // shared sentinel representing every empty leaf
    private Node<T> root;
    private int size;
    private Comparator<T> comparator; // null means "use natural ordering (Comparable)"

    // Natural-ordering tree. T must implement Comparable<T> at runtime.
    public RedBlackTree() {
        NIL = new Node<>(null);
        NIL.color = BLACK;
        root = NIL;
        size = 0;
        comparator = null;
    }

    // Comparator-based tree, same pattern as BST.java and GenericHeap.java,
    // for types like Book that don't implement Comparable themselves.
    public RedBlackTree(Comparator<T> comparator) {
        NIL = new Node<>(null);
        NIL.color = BLACK;
        root = NIL;
        size = 0;
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


    private void leftRotate(Node<T> x) {
        Node<T> y = x.right;
        x.right = y.left;
        if (y.left != NIL) {
            y.left.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == NIL) {
            root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }
        y.left = x;
        x.parent = y;
    }

    private void rightRotate(Node<T> x) {
        Node<T> y = x.left;
        x.left = y.right;
        if (y.right != NIL) {
            y.right.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == NIL) {
            root = y;
        } else if (x == x.parent.right) {
            x.parent.right = y;
        } else {
            x.parent.left = y;
        }
        y.right = x;
        x.parent = y;
    }

   
    public void insert(T value) {
        // Step 1: plain BST insert, but pointing empty children at NIL
        // instead of null, and remembering the parent as we walk down.
        Node<T> newNode = new Node<>(value);
        newNode.left = NIL;
        newNode.right = NIL;

        Node<T> parent = NIL;
        Node<T> current = root;

        while (current != NIL) {
            parent = current;
            int comparison = compare(value, current.value);
            if (comparison < 0) {
                current = current.left;
            } else if (comparison > 0) {
                current = current.right;
            } else {
                return; // no duplicates allowed, same rule as BST.java
            }
        }

        newNode.parent = parent;
        if (parent == NIL) {
            root = newNode; // tree was empty
        } else if (compare(value, parent.value) < 0) {
            parent.left = newNode;
        } else {
            parent.right = newNode;
        }

        newNode.color = RED; // new nodes always start RED
        size++;

        // Step 2: fix up any red-black rule violations the insert caused.
        insertFixup(newNode);
    }

    // After a plain BST insert, the only rule that could be broken is
    // rule 4 (a RED node with a RED parent). This walks back up the tree
    // fixing that, using the new node's "uncle" (its parent's sibling)
    // to decide whether to recolour or rotate.
    private void insertFixup(Node<T> z) {
        while (z.parent.color == RED) {
            if (z.parent == z.parent.parent.left) {
                Node<T> uncle = z.parent.parent.right;

                if (uncle.color == RED) {
                    // Case 1: uncle is RED - just recolour and move up.
                    z.parent.color = BLACK;
                    uncle.color = BLACK;
                    z.parent.parent.color = RED;
                    z = z.parent.parent;
                } else {
                    if (z == z.parent.right) {
                        // Case 2: z is a "right child" - rotate to turn
                        // it into case 3.
                        z = z.parent;
                        leftRotate(z);
                    }
                    // Case 3: z is a "left child" - recolour and rotate.
                    z.parent.color = BLACK;
                    z.parent.parent.color = RED;
                    rightRotate(z.parent.parent);
                }
            } else {
                // Exact mirror image of the block above (left <-> right).
                Node<T> uncle = z.parent.parent.left;

                if (uncle.color == RED) {
                    z.parent.color = BLACK;
                    uncle.color = BLACK;
                    z.parent.parent.color = RED;
                    z = z.parent.parent;
                } else {
                    if (z == z.parent.left) {
                        z = z.parent;
                        rightRotate(z);
                    }
                    z.parent.color = BLACK;
                    z.parent.parent.color = RED;
                    leftRotate(z.parent.parent);
                }
            }
        }
        root.color = BLACK; // rule 2: the root is always BLACK
    }

   
    public boolean search(T value) {
        return findNode(value) != NIL;
    }

    private Node<T> findNode(T value) {
        Node<T> current = root;
        while (current != NIL) {
            int comparison = compare(value, current.value);
            if (comparison == 0) {
                return current;
            } else if (comparison < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return NIL;
    }


    public void delete(T value) {
        Node<T> z = findNode(value);
        if (z == NIL) {
            return; // value not found, nothing to delete
        }

        Node<T> y = z;
        boolean yOriginalColor = y.color;
        Node<T> x;

        if (z.left == NIL) {
            x = z.right;
            transplant(z, z.right);
        } else if (z.right == NIL) {
            x = z.left;
            transplant(z, z.left);
        } else {
            // Two children: same idea as BST.java - replace with the
            // in-order successor (smallest value in the right subtree).
            y = minimum(z.right);
            yOriginalColor = y.color;
            x = y.right;

            if (y.parent == z) {
                x.parent = y; // keep x's parent link valid even if x is NIL
            } else {
                transplant(y, y.right);
                y.right = z.right;
                y.right.parent = y;
            }

            transplant(z, y);
            y.left = z.left;
            y.left.parent = y;
            y.color = z.color;
        }

        size--;

        // Only removing a BLACK node can break the red-black rules,
        // since removing a RED node can't change any path's black-height.
        if (yOriginalColor == BLACK) {
            deleteFixup(x);
        }
    }

    // Replaces the subtree rooted at u with the subtree rooted at v.
    // (u's parent now points at v instead of u.)
    private void transplant(Node<T> u, Node<T> v) {
        if (u.parent == NIL) {
            root = v;
        } else if (u == u.parent.left) {
            u.parent.left = v;
        } else {
            u.parent.right = v;
        }
        v.parent = u.parent; // safe even when v is NIL - it's just a sentinel
    }

    private Node<T> minimum(Node<T> node) {
        while (node.left != NIL) {
            node = node.left;
        }
        return node;
    }

    // Restores the red-black rules after a BLACK node was removed. x is
    // the node that moved into the deleted node's spot (possibly NIL) -
    // conceptually, x is carrying an "extra" black that needs to be
    // pushed up or resolved via its sibling.
    private void deleteFixup(Node<T> x) {
        while (x != root && x.color == BLACK) {
            if (x == x.parent.left) {
                Node<T> sibling = x.parent.right;

                if (sibling.color == RED) {
                    sibling.color = BLACK;
                    x.parent.color = RED;
                    leftRotate(x.parent);
                    sibling = x.parent.right;
                }

                if (sibling.left.color == BLACK && sibling.right.color == BLACK) {
                    sibling.color = RED;
                    x = x.parent;
                } else {
                    if (sibling.right.color == BLACK) {
                        sibling.left.color = BLACK;
                        sibling.color = RED;
                        rightRotate(sibling);
                        sibling = x.parent.right;
                    }
                    sibling.color = x.parent.color;
                    x.parent.color = BLACK;
                    sibling.right.color = BLACK;
                    leftRotate(x.parent);
                    x = root;
                }
            } else {
                // Mirror image of the block above (left <-> right).
                Node<T> sibling = x.parent.left;

                if (sibling.color == RED) {
                    sibling.color = BLACK;
                    x.parent.color = RED;
                    rightRotate(x.parent);
                    sibling = x.parent.left;
                }

                if (sibling.right.color == BLACK && sibling.left.color == BLACK) {
                    sibling.color = RED;
                    x = x.parent;
                } else {
                    if (sibling.left.color == BLACK) {
                        sibling.right.color = BLACK;
                        sibling.color = RED;
                        leftRotate(sibling);
                        sibling = x.parent.left;
                    }
                    sibling.color = x.parent.color;
                    x.parent.color = BLACK;
                    sibling.left.color = BLACK;
                    rightRotate(x.parent);
                    x = root;
                }
            }
        }
        x.color = BLACK;
    }


    public int height() {
        return heightHelper(root);
    }

    private int heightHelper(Node<T> node) {
        if (node == NIL) {
            return -1;
        }
        int leftHeight = heightHelper(node.left);
        int rightHeight = heightHelper(node.right);
        return 1 + Math.max(leftHeight, rightHeight);
    }


    public void inorder(Consumer<T> action) {
        inorderVisit(root, action);
    }

    private void inorderVisit(Node<T> node, Consumer<T> action) {
        if (node == NIL) return;
        inorderVisit(node.left, action);
        action.accept(node.value);
        inorderVisit(node.right, action);
    }

    // Inorder traversal - prints values in sorted order, now just a
    // thin wrapper around inorder() above.
    public void printInorder() {
        inorder(value -> System.out.println(" - " + value));
        System.out.println();
    }


    public boolean isValid() {
        if (root.color != BLACK) {
            return false; // rule 2: root must be BLACK
        }
        return checkNoRedRedViolation(root) && checkBlackHeightConsistent(root) != -1;
    }

    // Rule 4: a RED node can never have a RED child.
    private boolean checkNoRedRedViolation(Node<T> node) {
        if (node == NIL) {
            return true;
        }
        if (node.color == RED) {
            if (node.left.color == RED || node.right.color == RED) {
                return false;
            }
        }
        return checkNoRedRedViolation(node.left) && checkNoRedRedViolation(node.right);
    }

    // Rule 5: every root-to-leaf path has the same number of BLACK
    // nodes. Returns the black-height of this subtree, or -1 if the
    // left and right subtrees disagree (a violation).
    private int checkBlackHeightConsistent(Node<T> node) {
        if (node == NIL) {
            return 1; // NIL leaves count as one black node by convention
        }
        int leftHeight = checkBlackHeightConsistent(node.left);
        if (leftHeight == -1) return -1;
        int rightHeight = checkBlackHeightConsistent(node.right);
        if (rightHeight == -1) return -1;
        if (leftHeight != rightHeight) {
            return -1; // violation: paths disagree on black-height
        }
        return leftHeight + (node.color == BLACK ? 1 : 0);
    }
}
