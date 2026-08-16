package algorithms;

import structures.*;

import java.util.NoSuchElementException;

/**
 * Graph & Analytics Squad Engine (Milestone M7)
 *
 * Implements fundamental graph algorithms for the Library Service Operations Optimizer
 * without using any Java standard collections (HashMap, PriorityQueue, ArrayList, etc.).
 *
 * Algorithms Included:
 *  1. Breadth-First Search (BFS) & Shortest Hop Reachability
 *  2. Depth-First Search (DFS) & Connected Component Traversal
 *  3. Dijkstra's Algorithm (Fastest Weighted Route with Road Condition Weights)
 *  4. Prim's Algorithm (Minimum Spanning Tree - Node-based Greedy)
 *  5. Kruskal's Algorithm (Minimum Spanning Tree - Edge-based Disjoint Set)
 */
public class GraphAlgorithms {

    // =========================================================================
    // Data Classes for Results
    // =========================================================================

    /**
     * Represents a directed or undirected weighted edge in graph analysis.
     */
    public static class WeightedEdge<T> implements Comparable<WeightedEdge<T>> {
        public final T from;
        public final T to;
        public final double weight;

        public WeightedEdge(T from, T to, double weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }

        @Override
        public int compareTo(WeightedEdge<T> other) {
            return Double.compare(this.weight, other.weight);
        }

        @Override
        public String toString() {
            return from + " --(" + weight + ")--> " + to;
        }
    }

    /**
     * Represents the result of a shortest-path query.
     */
    public static class PathResult<T> {
        private final T source;
        private final T destination;
        private final CustomLinkedList<T> path;
        private final double totalDistance;
        private final boolean reachable;

        public PathResult(T source, T destination, CustomLinkedList<T> path, double totalDistance, boolean reachable) {
            this.source = source;
            this.destination = destination;
            this.path = path;
            this.totalDistance = totalDistance;
            this.reachable = reachable;
        }

        public T getSource() { return source; }
        public T getDestination() { return destination; }
        public CustomLinkedList<T> getPath() { return path; }
        public double getTotalDistance() { return totalDistance; }
        public boolean isReachable() { return reachable; }

        @Override
        public String toString() {
            if (!reachable) {
                return "Unreachable: No path from " + source + " to " + destination;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Shortest Path (Total Weight: ").append(totalDistance).append("): ");
            for (int i = 0; i < path.size(); i++) {
                sb.append(path.get(i));
                if (i < path.size() - 1) sb.append(" -> ");
            }
            return sb.toString();
        }
    }

    /**
     * Represents the result of a Minimum Spanning Tree (MST) computation.
     */
    public static class MSTResult<T> {
        private final CustomLinkedList<WeightedEdge<T>> edges;
        private final double totalWeight;
        private final boolean connected;

        public MSTResult(CustomLinkedList<WeightedEdge<T>> edges, double totalWeight, boolean connected) {
            this.edges = edges;
            this.totalWeight = totalWeight;
            this.connected = connected;
        }

        public CustomLinkedList<WeightedEdge<T>> getEdges() { return edges; }
        public double getTotalWeight() { return totalWeight; }
        public boolean isConnected() { return connected; }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("MST [Connected=").append(connected)
              .append(", Total Weight=").append(totalWeight).append("]:\n");
            for (WeightedEdge<T> edge : edges) {
                sb.append("  ").append(edge).append("\n");
            }
            return sb.toString();
        }
    }

    // Helper for Dijkstra priority queue ordering
    private static class NodeDistance<T> implements Comparable<NodeDistance<T>> {
        final T node;
        final double distance;

        NodeDistance(T node, double distance) {
            this.node = node;
            this.distance = distance;
        }

        @Override
        public int compareTo(NodeDistance<T> other) {
            return Double.compare(this.distance, other.distance);
        }
    }

    // =========================================================================
    // 1. Breadth-First Search (BFS) & Reachability
    // =========================================================================

    /**
     * Executes Breadth-First Search starting from a given vertex.
     * Explores neighbor levels in FIFO order using our hand-built CustomQueue.
     *
     * @param graph the graph to search
     * @param startVertex the origin node
     * @return CustomLinkedList of vertices in BFS traversal order
     */
    public static <T> CustomLinkedList<T> bfs(Graph<T> graph, T startVertex) {
        validateVertex(graph, startVertex);
        CustomLinkedList<T> traversalOrder = new CustomLinkedList<>();
        GenericHashtable<T, Boolean> visited = new GenericHashtable<>();
        CustomQueue<T> queue = new CustomQueue<>();

        visited.put(startVertex, true);
        queue.enqueue(startVertex);

        while (!queue.isEmpty()) {
            T current = queue.dequeue();
            traversalOrder.addLast(current);

            graph.forEachNeighbor(current, (neighbor, weight) -> {
                if (!visited.containsKey(neighbor)) {
                    visited.put(neighbor, true);
                    queue.enqueue(neighbor);
                }
            });
        }
        return traversalOrder;
    }

    /**
     * Checks if a target location is reachable from a starting point.
     */
    public static <T> boolean isReachable(Graph<T> graph, T source, T destination) {
        if (!graph.containsVertex(source) || !graph.containsVertex(destination)) {
            return false;
        }
        if (source.equals(destination)) {
            return true;
        }

        GenericHashtable<T, Boolean> visited = new GenericHashtable<>();
        CustomQueue<T> queue = new CustomQueue<>();

        visited.put(source, true);
        queue.enqueue(source);

        while (!queue.isEmpty()) {
            T current = queue.dequeue();
            if (current.equals(destination)) {
                return true;
            }

            graph.forEachNeighbor(current, (neighbor, weight) -> {
                if (!visited.containsKey(neighbor)) {
                    visited.put(neighbor, true);
                    queue.enqueue(neighbor);
                }
            });
        }
        return false;
    }

    /**
     * Finds the shortest unweighted path (fewest corridor hops/turns) between two nodes.
     */
    public static <T> CustomLinkedList<T> shortestHopPath(Graph<T> graph, T source, T destination) {
        validateVertex(graph, source);
        validateVertex(graph, destination);

        if (source.equals(destination)) {
            CustomLinkedList<T> single = new CustomLinkedList<>();
            single.addLast(source);
            return single;
        }

        GenericHashtable<T, T> parentMap = new GenericHashtable<>();
        GenericHashtable<T, Boolean> visited = new GenericHashtable<>();
        CustomQueue<T> queue = new CustomQueue<>();

        visited.put(source, true);
        queue.enqueue(source);
        boolean found = false;

        while (!queue.isEmpty() && !found) {
            T current = queue.dequeue();
            if (current.equals(destination)) {
                found = true;
                break;
            }

            graph.forEachNeighbor(current, (neighbor, weight) -> {
                if (!visited.containsKey(neighbor)) {
                    visited.put(neighbor, true);
                    parentMap.put(neighbor, current);
                    queue.enqueue(neighbor);
                }
            });
        }

        if (!found && !parentMap.containsKey(destination)) {
            return new CustomLinkedList<>(); // Empty list indicates unreachable
        }

        // Reconstruct path backwards
        CustomLinkedList<T> path = new CustomLinkedList<>();
        T step = destination;
        while (step != null) {
            path.addFirst(step);
            if (step.equals(source)) break;
            step = parentMap.containsKey(step) ? parentMap.get(step) : null;
        }
        return path;
    }

    // =========================================================================
    // 2. Depth-First Search (DFS)
    // =========================================================================

    /**
     * Executes Depth-First Search starting from a given vertex using our GenericStack.
     *
     * @param graph the graph to search
     * @param startVertex origin node
     * @return CustomLinkedList of vertices in DFS traversal order
     */
    public static <T> CustomLinkedList<T> dfs(Graph<T> graph, T startVertex) {
        validateVertex(graph, startVertex);
        CustomLinkedList<T> traversalOrder = new CustomLinkedList<>();
        GenericHashtable<T, Boolean> visited = new GenericHashtable<>();
        GenericStack<T> stack = new GenericStack<>();

        stack.push(startVertex);

        while (!stack.isEmpty()) {
            T current = stack.pop();

            if (!visited.containsKey(current)) {
                visited.put(current, true);
                traversalOrder.addLast(current);

                graph.forEachNeighbor(current, (neighbor, weight) -> {
                    if (!visited.containsKey(neighbor)) {
                        stack.push(neighbor);
                    }
                });
            }
        }
        return traversalOrder;
    }

    // =========================================================================
    // 3. Dijkstra's Algorithm (Weighted Shortest Path)
    // =========================================================================

    /**
     * Calculates the fastest / lowest-cost route between source and destination
     * using Dijkstra's algorithm backed by our hand-built GenericHeap (Min-Heap)
     * and GenericHashtable for distance tables.
     *
     * Preconditions Checked:
     *  - Source and destination vertices must exist in the graph.
     *  - Edge weights must be non-negative (>= 0). Throws IllegalArgumentException on negative weights.
     *
     * @param graph the physical road/corridor graph
     * @param source dispatch location
     * @param destination target shelf/room
     * @return PathResult containing the list of path nodes, total weight, and reachability flag
     */
    public static <T> PathResult<T> dijkstra(Graph<T> graph, T source, T destination) {
        validateVertex(graph, source);
        validateVertex(graph, destination);

        if (source.equals(destination)) {
            CustomLinkedList<T> single = new CustomLinkedList<>();
            single.addLast(source);
            return new PathResult<>(source, destination, single, 0.0, true);
        }

        GenericHeap<NodeDistance<T>> pq = new GenericHeap<>();
        GenericHashtable<T, Double> distTo = new GenericHashtable<>();
        GenericHashtable<T, T> edgeTo = new GenericHashtable<>();
        GenericHashtable<T, Boolean> settled = new GenericHashtable<>();

        distTo.put(source, 0.0);
        pq.add(new NodeDistance<>(source, 0.0));

        while (!pq.isEmpty()) {
            NodeDistance<T> current = pq.poll();
            T u = current.node;

            if (settled.containsKey(u)) {
                continue; // Stale entry in priority queue
            }
            settled.put(u, true);

            // Early exit if we have settled the target destination
            if (u.equals(destination)) {
                break;
            }

            graph.forEachNeighbor(u, (neighbor, weight) -> {
                if (weight < 0) {
                    throw new IllegalArgumentException("Dijkstra does not support negative edge weights: " + weight);
                }

                double currentDist = distTo.get(u);
                double newDist = currentDist + weight;

                boolean isShorter = !distTo.containsKey(neighbor) || newDist < distTo.get(neighbor);
                if (isShorter) {
                    distTo.put(neighbor, newDist);
                    edgeTo.put(neighbor, u);
                    pq.add(new NodeDistance<>(neighbor, newDist));
                }
            });
        }

        if (!distTo.containsKey(destination)) {
            return new PathResult<>(source, destination, new CustomLinkedList<>(), Double.POSITIVE_INFINITY, false);
        }

        // Reconstruct path from destination backwards to source
        CustomLinkedList<T> path = new CustomLinkedList<>();
        T curr = destination;
        while (curr != null) {
            path.addFirst(curr);
            if (curr.equals(source)) break;
            curr = edgeTo.containsKey(curr) ? edgeTo.get(curr) : null;
        }

        return new PathResult<>(source, destination, path, distTo.get(destination), true);
    }

    // =========================================================================
    // 4. Prim's Algorithm (Minimum Spanning Tree)
    // =========================================================================

    /**
     * Computes the Minimum Spanning Tree (MST) using Prim's algorithm.
     * Grows the tree vertex-by-vertex from an initial hub using a Min-Heap.
     *
     * @param graph the connected library corridor network
     * @return MSTResult with chosen edges, total network weight, and connectivity status
     */
    public static <T> MSTResult<T> primMST(Graph<T> graph) {
        if (graph.vertexCount() == 0) {
            return new MSTResult<>(new CustomLinkedList<>(), 0.0, true);
        }

        CustomLinkedList<WeightedEdge<T>> mstEdges = new CustomLinkedList<>();
        GenericHashtable<T, Boolean> inMST = new GenericHashtable<>();
        GenericHeap<WeightedEdge<T>> pq = new GenericHeap<>();
        double totalWeight = 0.0;

        // Select starting vertex
        final Object[] startHolder = new Object[1];
        graph.forEachVertex(v -> {
            if (startHolder[0] == null) startHolder[0] = v;
        });

        @SuppressWarnings("unchecked")
        T startVertex = (T) startHolder[0];
        inMST.put(startVertex, true);

        // Add start vertex's edges to priority queue
        graph.forEachNeighbor(startVertex, (neighbor, weight) -> {
            pq.add(new WeightedEdge<>(startVertex, neighbor, weight));
        });

        while (!pq.isEmpty() && mstEdges.size() < graph.vertexCount() - 1) {
            WeightedEdge<T> edge = pq.poll();

            boolean hasFrom = inMST.containsKey(edge.from);
            boolean hasTo = inMST.containsKey(edge.to);

            // If both vertices are already in the MST, adding this edge would create a cycle
            if (hasFrom && hasTo) {
                continue;
            }

            T newVertex = hasFrom ? edge.to : edge.from;
            inMST.put(newVertex, true);
            mstEdges.addLast(edge);
            totalWeight += edge.weight;

            graph.forEachNeighbor(newVertex, (neighbor, weight) -> {
                if (!inMST.containsKey(neighbor)) {
                    pq.add(new WeightedEdge<>(newVertex, neighbor, weight));
                }
            });
        }

        boolean fullyConnected = (graph.vertexCount() <= 1) || (mstEdges.size() == graph.vertexCount() - 1);
        return new MSTResult<>(mstEdges, totalWeight, fullyConnected);
    }

    // =========================================================================
    // 5. Kruskal's Algorithm (Minimum Spanning Tree)
    // =========================================================================

    /**
     * Computes the Minimum Spanning Tree (MST) using Kruskal's algorithm.
     * Sorts all edges globally and adds them greedily, preventing cycles
     * using our hand-built GenericDisjointSet (Union-Find).
     *
     * @param graph the connected library corridor network
     * @return MSTResult with chosen edges, total network weight, and connectivity status
     */
    public static <T> MSTResult<T> kruskalMST(Graph<T> graph) {
        if (graph.vertexCount() == 0) {
            return new MSTResult<>(new CustomLinkedList<>(), 0.0, true);
        }

        CustomLinkedList<WeightedEdge<T>> mstEdges = new CustomLinkedList<>();
        GenericHeap<WeightedEdge<T>> allEdgesHeap = new GenericHeap<>();
        GenericDisjointSet<T> disjointSet = new GenericDisjointSet<>(graph.vertexCount() + 16);

        // 1. Initialize Disjoint Set with all vertices
        graph.forEachVertex(v -> {
            disjointSet.makeSet(v);
        });

        // 2. Collect all unique edges into min-heap
        GenericHashtable<String, Boolean> seenEdges = new GenericHashtable<>();
        graph.forEachVertex(u -> {
            graph.forEachNeighbor(u, (v, weight) -> {
                String key1 = u.toString() + "->" + v.toString();
                String key2 = v.toString() + "->" + u.toString();
                if (!graph.isDirected()) {
                    if (!seenEdges.containsKey(key1) && !seenEdges.containsKey(key2)) {
                        seenEdges.put(key1, true);
                        allEdgesHeap.add(new WeightedEdge<>(u, v, weight));
                    }
                } else {
                    if (!seenEdges.containsKey(key1)) {
                        seenEdges.put(key1, true);
                        allEdgesHeap.add(new WeightedEdge<>(u, v, weight));
                    }
                }
            });
        });

        double totalWeight = 0.0;

        // 3. Process edges greedily in ascending order of weight
        while (!allEdgesHeap.isEmpty() && mstEdges.size() < graph.vertexCount() - 1) {
            WeightedEdge<T> edge = allEdgesHeap.poll();

            // Union-Find cycle prevention
            if (!disjointSet.connected(edge.from, edge.to)) {
                disjointSet.union(edge.from, edge.to);
                mstEdges.addLast(edge);
                totalWeight += edge.weight;
            }
        }

        boolean fullyConnected = (graph.vertexCount() <= 1) || (mstEdges.size() == graph.vertexCount() - 1);
        return new MSTResult<>(mstEdges, totalWeight, fullyConnected);
    }

    // =========================================================================
    // Private Helpers
    // =========================================================================

    private static <T> void validateVertex(Graph<T> graph, T vertex) {
        if (vertex == null) {
            throw new IllegalArgumentException("Vertex cannot be null");
        }
        if (!graph.containsVertex(vertex)) {
            throw new NoSuchElementException("Vertex not found in graph: " + vertex);
        }
    }

    // =========================================================================
    // Demonstration & Verification
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("Graph & Analytics Squad - GraphAlgorithms Engine");
        System.out.println("=================================================\n");

        // Construct a sample library corridor network
        Graph<String> libraryNetwork = new Graph<>();
        String entrance = "Main Entrance";
        String issueDesk = "Circulation Desk";
        String shelfA = "Shelf A (Computer Science)";
        String shelfB = "Shelf B (Engineering)";
        String readingRoom = "Study Room 1";
        String archive = "Archive Room";

        libraryNetwork.addVertex(entrance);
        libraryNetwork.addVertex(issueDesk);
        libraryNetwork.addVertex(shelfA);
        libraryNetwork.addVertex(shelfB);
        libraryNetwork.addVertex(readingRoom);
        libraryNetwork.addVertex(archive);

        // Corridor connections with distances/weights
        libraryNetwork.addEdge(entrance, issueDesk, 10.0);
        libraryNetwork.addEdge(issueDesk, shelfA, 15.0);
        libraryNetwork.addEdge(issueDesk, shelfB, 25.0);
        libraryNetwork.addEdge(shelfA, shelfB, 12.0);
        libraryNetwork.addEdge(shelfA, readingRoom, 8.0);
        libraryNetwork.addEdge(shelfB, readingRoom, 14.0);
        libraryNetwork.addEdge(readingRoom, archive, 30.0);

        System.out.println("1. BFS Traversal from Main Entrance:");
        System.out.println("   " + bfs(libraryNetwork, entrance));

        System.out.println("\n2. DFS Traversal from Main Entrance:");
        System.out.println("   " + dfs(libraryNetwork, entrance));

        System.out.println("\n3. Dijkstra Shortest Route (Main Entrance -> Shelf B):");
        PathResult<String> route = dijkstra(libraryNetwork, entrance, shelfB);
        System.out.println("   " + route);

        System.out.println("\n4. Shortest Hop Path (Fewest Corridors):");
        System.out.println("   " + shortestHopPath(libraryNetwork, entrance, shelfB));

        System.out.println("\n5. Prim's Minimum Spanning Tree (MST):");
        MSTResult<String> primResult = primMST(libraryNetwork);
        System.out.print(primResult);

        System.out.println("6. Kruskal's Minimum Spanning Tree (MST):");
        MSTResult<String> kruskalResult = kruskalMST(libraryNetwork);
        System.out.print(kruskalResult);

        System.out.println("MST Consistency Check: " +
                (Math.abs(primResult.getTotalWeight() - kruskalResult.getTotalWeight()) < 1e-9 ? "PASSED (Weights match!)" : "FAILED"));
    }
}
