package structures;

import java.util.function.BiConsumer;
import java.util.function.Consumer;


public class Graph<T> {

    private static final int DEFAULT_CAPACITY = 16;

    // One entry in a vertex's adjacency list: "there is an edge to the
    // vertex at this internal index, with this weight".
    private static class Edge<T> {
        int toIndex;
        double weight;

        Edge(int toIndex, double weight) {
            this.toIndex = toIndex;
            this.weight = weight;
        }
    }

    // A small growable array of Edge objects for one vertex. Written by
    // hand (same resizing idea as GenericStack/GenericHeap) instead of
    // using java.util.ArrayList.
    @SuppressWarnings("unchecked")
    private static class EdgeList<T> {
        Edge<T>[] edges;
        int count;

        EdgeList() {
            edges = new Edge[4];
            count = 0;
        }

        void add(Edge<T> edge) {
            if (count == edges.length) {
                Edge<T>[] bigger = new Edge[edges.length * 2];
                for (int i = 0; i < count; i++) {
                    bigger[i] = edges[i];
                }
                edges = bigger;
            }
            edges[count] = edge;
            count++;
        }

        void removeByToIndex(int toIndex) {
            for (int i = 0; i < count; i++) {
                if (edges[i].toIndex == toIndex) {
                    for (int j = i; j < count - 1; j++) {
                        edges[j] = edges[j + 1];
                    }
                    count--;
                    return;
                }
            }
        }

        // Updates the weight of an existing entry for toIndex, if one
        // exists. Used when addEdge() is called again for a pair that
        // already has an edge, so we don't end up with two stale
        // entries for the same neighbor.
        void updateWeight(int toIndex, double newWeight) {
            for (int i = 0; i < count; i++) {
                if (edges[i].toIndex == toIndex) {
                    edges[i].weight = newWeight;
                    return;
                }
            }
        }
    }

    private T[] vertices;                  // vertices[i] = the vertex value stored at internal index i
    private EdgeList<T>[] adjacencyList;    // adjacencyList[i] = i's neighbors
    private boolean[][] hasEdgeMatrix;      // hasEdgeMatrix[i][j] = true if there's an edge i -> j
    private double[][] weightMatrix;        // weightMatrix[i][j] = that edge's weight (meaningless if hasEdgeMatrix[i][j] is false)
    private GenericHashtable<T, Integer> indexOf; // maps a vertex's value to its internal index - built on OUR OWN hashtable, not java.util.HashMap

    private int vertexCount;
    private int edgeCount;
    private int capacity;
    private final boolean directed;

    // Undirected by default: addEdge(a, b) also creates b -> a automatically.
    public Graph() {
        this(false);
    }

    public Graph(boolean directed) {
        this.directed = directed;
        this.capacity = DEFAULT_CAPACITY;
        this.vertices = allocateVertexArray(capacity);
        this.adjacencyList = allocateEdgeListArray(capacity);
        this.hasEdgeMatrix = new boolean[capacity][capacity];
        this.weightMatrix = new double[capacity][capacity];
        this.indexOf = new GenericHashtable<>();
        this.vertexCount = 0;
        this.edgeCount = 0;
    }

    @SuppressWarnings("unchecked")
    private T[] allocateVertexArray(int size) {
        return (T[]) new Object[size];
    }

    @SuppressWarnings("unchecked")
    private EdgeList<T>[] allocateEdgeListArray(int size) {
        return new EdgeList[size];
    }

    public int vertexCount() {
        return vertexCount;
    }

    public int edgeCount() {
        return edgeCount;
    }

    public boolean isDirected() {
        return directed;
    }

    public boolean containsVertex(T vertex) {
        return indexOf.containsKey(vertex);
    }

    // Adding a vertex
    public void addVertex(T vertex) {
        if (indexOf.containsKey(vertex)) {
            return; // already in the graph, nothing to do
        }
        if (vertexCount == capacity) {
            grow();
        }

        int index = vertexCount;
        vertices[index] = vertex;
        adjacencyList[index] = new EdgeList<>();
        indexOf.put(vertex, index);
        vertexCount++;
    }

    // Doubles every backing array's capacity (including both dimensions
    // of the two matrices), copying existing data across.
    private void grow() {
        int newCapacity = capacity * 2;

        T[] newVertices = allocateVertexArray(newCapacity);
        EdgeList<T>[] newAdjacencyList = allocateEdgeListArray(newCapacity);
        boolean[][] newHasEdgeMatrix = new boolean[newCapacity][newCapacity];
        double[][] newWeightMatrix = new double[newCapacity][newCapacity];

        for (int i = 0; i < vertexCount; i++) {
            newVertices[i] = vertices[i];
            newAdjacencyList[i] = adjacencyList[i];
            for (int j = 0; j < vertexCount; j++) {
                newHasEdgeMatrix[i][j] = hasEdgeMatrix[i][j];
                newWeightMatrix[i][j] = weightMatrix[i][j];
            }
        }

        vertices = newVertices;
        adjacencyList = newAdjacencyList;
        hasEdgeMatrix = newHasEdgeMatrix;
        weightMatrix = newWeightMatrix;
        capacity = newCapacity;
    }

    // Adding / removing an edge
    public void addEdge(T from, T to, double weight) {
        requireVertex(from);
        requireVertex(to);

        int i = indexOf.get(from);
        int j = indexOf.get(to);

        boolean isNewEdge = !hasEdgeMatrix[i][j];

        setDirectedEdge(i, j, weight);
        if (!directed) {
            setDirectedEdge(j, i, weight);
        }

        if (isNewEdge) {
            edgeCount++;
        }
    }

    // Updates ONE direction (i -> j) of an edge: the matrix always just
    // gets overwritten, but the adjacency list only gets a NEW entry if
    // this exact direction didn't already have one - otherwise we update
    // the existing entry's weight in place, so calling addEdge() twice
    // on the same pair doesn't leave stale duplicate entries behind.
    private void setDirectedEdge(int i, int j, double weight) {
        boolean isNewCell = !hasEdgeMatrix[i][j];
        hasEdgeMatrix[i][j] = true;
        weightMatrix[i][j] = weight;
        if (isNewCell) {
            adjacencyList[i].add(new Edge<>(j, weight));
        } else {
            adjacencyList[i].updateWeight(j, weight);
        }
    }

    public void removeEdge(T from, T to) {
        requireVertex(from);
        requireVertex(to);

        int i = indexOf.get(from);
        int j = indexOf.get(to);

        if (!hasEdgeMatrix[i][j]) {
            return; // no such edge, nothing to do
        }

        hasEdgeMatrix[i][j] = false;
        weightMatrix[i][j] = 0.0;
        adjacencyList[i].removeByToIndex(j);
        edgeCount--;

        if (!directed) {
            hasEdgeMatrix[j][i] = false;
            weightMatrix[j][i] = 0.0;
            adjacencyList[j].removeByToIndex(i);
        }
    }

    private void requireVertex(T vertex) {
        if (!indexOf.containsKey(vertex)) {
            throw new IllegalArgumentException(
                    "Vertex must be added with addVertex() before it can be used in an edge: " + vertex);
        }
    }


    public boolean hasEdge(T from, T to) {
        requireVertex(from);
        requireVertex(to);
        int i = indexOf.get(from);
        int j = indexOf.get(to);
        return hasEdgeMatrix[i][j];
    }

    public double getWeight(T from, T to) {
        requireVertex(from);
        requireVertex(to);
        int i = indexOf.get(from);
        int j = indexOf.get(to);
        if (!hasEdgeMatrix[i][j]) {
            throw new IllegalArgumentException("No edge exists between the given vertices");
        }
        return weightMatrix[i][j];
    }


    // Visiting vertices / neighbors - this is where the adjacency list
    // representation shines: walking only a vertex's actual neighbors,
    // instead of scanning an entire matrix row. Visitor-style, same
    // pattern as BST/RedBlackTree/BTree's inorder(), so another squad
    // (e.g. Algorithms Engine, running Dijkstra/BFS/DFS) can consume
    // this data without us returning an array or java.util collection.
    public void forEachVertex(Consumer<T> action) {
        for (int i = 0; i < vertexCount; i++) {
            action.accept(vertices[i]);
        }
    }

    public void forEachNeighbor(T vertex, BiConsumer<T, Double> action) {
        requireVertex(vertex);
        int i = indexOf.get(vertex);
        EdgeList<T> list = adjacencyList[i];
        for (int k = 0; k < list.count; k++) {
            Edge<T> edge = list.edges[k];
            action.accept(vertices[edge.toIndex], edge.weight);
        }
    }


    // Validator - checks that the adjacency list and adjacency matrix
    // genuinely agree with each other (same idea as the isValid()
    // methods on RedBlackTree/BTree: proving the structure's own
    // invariants hold, not just "looks about right").
    public boolean isValid() {
        for (int i = 0; i < vertexCount; i++) {
            boolean[] seenInAdjacencyList = new boolean[vertexCount];

            EdgeList<T> list = adjacencyList[i];
            for (int k = 0; k < list.count; k++) {
                int j = list.edges[k].toIndex;
                if (!hasEdgeMatrix[i][j]) {
                    return false; // adjacency list has an edge the matrix doesn't
                }
                if (Math.abs(weightMatrix[i][j] - list.edges[k].weight) > 1e-9) {
                    return false; // weight mismatch between the two representations
                }
                seenInAdjacencyList[j] = true;
            }

            for (int j = 0; j < vertexCount; j++) {
                if (hasEdgeMatrix[i][j] && !seenInAdjacencyList[j]) {
                    return false; // matrix has an edge the adjacency list doesn't
                }
            }
        }
        return true;
    }
}
