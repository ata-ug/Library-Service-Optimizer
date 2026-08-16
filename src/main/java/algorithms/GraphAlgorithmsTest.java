package algorithms;

import library.db.DatabaseConnection;
import library.db.DatabaseSeeder;
import library.db.LibraryDataLoader;
import library.model.Location;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import structures.CustomLinkedList;
import structures.Graph;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

/**
 * Formal Unit Test Suite for Graph & Analytics Squad (Milestones M7 & M9)
 *
 * Tests all core algorithms (BFS, DFS, Dijkstra, Prim, Kruskal) across standard networks,
 * complex topologies, edge cases (single node, disconnected, unreachable), precondition
 * violations (negative weights, missing vertices), and real database-seeded library graph.
 */
public class GraphAlgorithmsTest {

    private Graph<String> sampleGraph;

    @BeforeClass
    public static void initDatabase() {
        try {
            DatabaseSeeder.initSchema();
            DatabaseSeeder.seedDatabase();
        } catch (Exception ignored) {
            // If DB cannot be seeded in test runner, DB integration tests will gracefully handle it
        }
    }

    @Before
    public void setUp() {
        // Construct a standard 6-node library corridor graph
        // Nodes: Entrance, Desk, ShelfA, ShelfB, StudyRoom, Archive
        sampleGraph = new Graph<>(false); // Undirected

        sampleGraph.addVertex("Entrance");
        sampleGraph.addVertex("Desk");
        sampleGraph.addVertex("ShelfA");
        sampleGraph.addVertex("ShelfB");
        sampleGraph.addVertex("StudyRoom");
        sampleGraph.addVertex("Archive");

        sampleGraph.addEdge("Entrance", "Desk", 10.0);
        sampleGraph.addEdge("Desk", "ShelfA", 15.0);
        sampleGraph.addEdge("Desk", "ShelfB", 25.0);
        sampleGraph.addEdge("ShelfA", "ShelfB", 8.0);
        sampleGraph.addEdge("ShelfA", "StudyRoom", 12.0);
        sampleGraph.addEdge("ShelfB", "StudyRoom", 6.0);
        sampleGraph.addEdge("StudyRoom", "Archive", 20.0);
    }

    // =========================================================================
    // 1. Breadth-First Search (BFS) & Reachability Tests
    // =========================================================================

    @Test
    public void bfs_standardGraph_visitsAllConnectedVertices() {
        CustomLinkedList<String> order = GraphAlgorithms.bfs(sampleGraph, "Entrance");
        assertEquals(6, order.size());
        assertEquals("Entrance", order.get(0));
        assertEquals("Desk", order.get(1)); // First neighbor level
    }

    @Test
    public void bfs_singleVertexGraph_returnsSingleElement() {
        Graph<String> singleNodeGraph = new Graph<>();
        singleNodeGraph.addVertex("Solo");

        CustomLinkedList<String> order = GraphAlgorithms.bfs(singleNodeGraph, "Solo");
        assertEquals(1, order.size());
        assertEquals("Solo", order.get(0));
    }

    @Test
    public void isReachable_connectedNodes_returnsTrue() {
        assertTrue(GraphAlgorithms.isReachable(sampleGraph, "Entrance", "Archive"));
        assertTrue(GraphAlgorithms.isReachable(sampleGraph, "ShelfA", "Entrance"));
    }

    @Test
    public void isReachable_disconnectedComponents_returnsFalse() {
        Graph<String> disconnected = new Graph<>();
        disconnected.addVertex("IslandA");
        disconnected.addVertex("IslandB");
        disconnected.addVertex("IslandC");
        disconnected.addEdge("IslandA", "IslandB", 5.0);

        assertTrue(GraphAlgorithms.isReachable(disconnected, "IslandA", "IslandB"));
        assertFalse(GraphAlgorithms.isReachable(disconnected, "IslandA", "IslandC"));
    }

    @Test
    public void shortestHopPath_findsFewestHopsRoute() {
        // Even if Desk -> ShelfB has weight 25, it is 1 hop from Desk
        CustomLinkedList<String> hopPath = GraphAlgorithms.shortestHopPath(sampleGraph, "Desk", "ShelfB");
        assertEquals(2, hopPath.size());
        assertEquals("Desk", hopPath.get(0));
        assertEquals("ShelfB", hopPath.get(1));
    }

    @Test
    public void shortestHopPath_unreachableDestination_returnsEmptyList() {
        Graph<String> disconnected = new Graph<>();
        disconnected.addVertex("A");
        disconnected.addVertex("B");

        CustomLinkedList<String> path = GraphAlgorithms.shortestHopPath(disconnected, "A", "B");
        assertTrue(path.isEmpty());
    }

    // =========================================================================
    // 2. Depth-First Search (DFS) Tests
    // =========================================================================

    @Test
    public void dfs_standardGraph_visitsAllVertices() {
        CustomLinkedList<String> order = GraphAlgorithms.dfs(sampleGraph, "Entrance");
        assertEquals(6, order.size());
        assertEquals("Entrance", order.get(0));
    }

    @Test
    public void dfs_singleVertexGraph_returnsSingleElement() {
        Graph<String> single = new Graph<>();
        single.addVertex("Solo");
        CustomLinkedList<String> order = GraphAlgorithms.dfs(single, "Solo");
        assertEquals(1, order.size());
        assertEquals("Solo", order.get(0));
    }

    // =========================================================================
    // 3. Dijkstra Shortest Path Tests
    // =========================================================================

    @Test
    public void dijkstra_standardGraph_findsOptimalWeightedRoute() {
        // Desk -> ShelfA -> ShelfB has cost 15 + 8 = 23 (better than direct Desk -> ShelfB which is 25)
        GraphAlgorithms.PathResult<String> result = GraphAlgorithms.dijkstra(sampleGraph, "Desk", "ShelfB");

        assertTrue(result.isReachable());
        assertEquals(23.0, result.getTotalDistance(), 1e-9);

        CustomLinkedList<String> path = result.getPath();
        assertEquals(3, path.size());
        assertEquals("Desk", path.get(0));
        assertEquals("ShelfA", path.get(1));
        assertEquals("ShelfB", path.get(2));
    }

    @Test
    public void dijkstra_sourceEqualsDestination_returnsZeroDistance() {
        GraphAlgorithms.PathResult<String> result = GraphAlgorithms.dijkstra(sampleGraph, "Entrance", "Entrance");
        assertTrue(result.isReachable());
        assertEquals(0.0, result.getTotalDistance(), 1e-9);
        assertEquals(1, result.getPath().size());
        assertEquals("Entrance", result.getPath().get(0));
    }

    @Test
    public void dijkstra_unreachableDestination_returnsReachableFalseAndInfinity() {
        Graph<String> disconnected = new Graph<>();
        disconnected.addVertex("Hub");
        disconnected.addVertex("IsolatedRoom");

        GraphAlgorithms.PathResult<String> result = GraphAlgorithms.dijkstra(disconnected, "Hub", "IsolatedRoom");
        assertFalse(result.isReachable());
        assertEquals(Double.POSITIVE_INFINITY, result.getTotalDistance(), 1e-9);
        assertTrue(result.getPath().isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void dijkstra_negativeWeightPrecondition_throwsIllegalArgumentException() {
        Graph<String> negGraph = new Graph<>();
        negGraph.addVertex("A");
        negGraph.addVertex("B");
        negGraph.addEdge("A", "B", -5.0);

        GraphAlgorithms.dijkstra(negGraph, "A", "B");
    }

    @Test(expected = NoSuchElementException.class)
    public void dijkstra_missingVertexPrecondition_throwsNoSuchElementException() {
        GraphAlgorithms.dijkstra(sampleGraph, "Entrance", "NonExistentLocation");
    }

    @Test
    public void dijkstra_longestRouteThroughNetwork_findsCorrectTotalWeight() {
        // Entrance -> Desk (10) -> ShelfA (15) -> ShelfB (8) -> StudyRoom (6) -> Archive (20) = 59.0
        // Or Entrance -> Desk (10) -> ShelfA (15) -> StudyRoom (12) -> Archive (20) = 57.0 (Shortest!)
        GraphAlgorithms.PathResult<String> result = GraphAlgorithms.dijkstra(sampleGraph, "Entrance", "Archive");
        assertTrue(result.isReachable());
        assertEquals(57.0, result.getTotalDistance(), 1e-9);
    }

    // =========================================================================
    // 4. Prim's Algorithm (Minimum Spanning Tree) Tests
    // =========================================================================

    @Test
    public void prim_standardGraph_computesCorrectTotalWeight() {
        // Edges chosen in MST:
        // Entrance-Desk (10), Desk-ShelfA (15), ShelfA-ShelfB (8), ShelfB-StudyRoom (6), StudyRoom-Archive (20)
        // Total MST Weight = 10 + 15 + 8 + 6 + 20 = 59.0
        GraphAlgorithms.MSTResult<String> mst = GraphAlgorithms.primMST(sampleGraph);

        assertTrue(mst.isConnected());
        assertEquals(59.0, mst.getTotalWeight(), 1e-9);
        assertEquals(5, mst.getEdges().size()); // |V| - 1 = 6 - 1 = 5
    }

    @Test
    public void prim_singleVertex_returnsZeroWeight() {
        Graph<String> single = new Graph<>();
        single.addVertex("Single");

        GraphAlgorithms.MSTResult<String> mst = GraphAlgorithms.primMST(single);
        assertTrue(mst.isConnected());
        assertEquals(0.0, mst.getTotalWeight(), 1e-9);
        assertEquals(0, mst.getEdges().size());
    }

    @Test
    public void prim_disconnectedGraph_flagsConnectedFalse() {
        Graph<String> disconnected = new Graph<>();
        disconnected.addVertex("A");
        disconnected.addVertex("B");
        disconnected.addVertex("C");
        disconnected.addEdge("A", "B", 4.0); // C is isolated

        GraphAlgorithms.MSTResult<String> mst = GraphAlgorithms.primMST(disconnected);
        assertFalse(mst.isConnected());
    }

    // =========================================================================
    // 5. Kruskal's Algorithm (Minimum Spanning Tree) Tests
    // =========================================================================

    @Test
    public void kruskal_standardGraph_computesCorrectTotalWeight() {
        GraphAlgorithms.MSTResult<String> mst = GraphAlgorithms.kruskalMST(sampleGraph);

        assertTrue(mst.isConnected());
        assertEquals(59.0, mst.getTotalWeight(), 1e-9);
        assertEquals(5, mst.getEdges().size());
    }

    @Test
    public void kruskal_singleVertex_returnsZeroWeight() {
        Graph<String> single = new Graph<>();
        single.addVertex("Solo");

        GraphAlgorithms.MSTResult<String> mst = GraphAlgorithms.kruskalMST(single);
        assertTrue(mst.isConnected());
        assertEquals(0.0, mst.getTotalWeight(), 1e-9);
    }

    @Test
    public void kruskal_disconnectedGraph_flagsConnectedFalse() {
        Graph<String> disconnected = new Graph<>();
        disconnected.addVertex("A");
        disconnected.addVertex("B");
        disconnected.addVertex("C");
        disconnected.addEdge("A", "B", 7.0);

        GraphAlgorithms.MSTResult<String> mst = GraphAlgorithms.kruskalMST(disconnected);
        assertFalse(mst.isConnected());
    }

    @Test
    public void kruskalAndPrim_agreeOnTotalMSTWeight() {
        GraphAlgorithms.MSTResult<String> prim = GraphAlgorithms.primMST(sampleGraph);
        GraphAlgorithms.MSTResult<String> kruskal = GraphAlgorithms.kruskalMST(sampleGraph);

        assertEquals(prim.getTotalWeight(), kruskal.getTotalWeight(), 1e-9);
        assertEquals(prim.isConnected(), kruskal.isConnected());
        assertEquals(prim.getEdges().size(), kruskal.getEdges().size());
    }

    // =========================================================================
    // 6. Database Graph Integration Tests (LibraryGraphService)
    // =========================================================================

    @Test
    public void libraryGraphService_loadsDatabaseGraphCorrectly() throws SQLException {
        LibraryDataLoader loader = new LibraryDataLoader();
        LibraryGraphService graphService = new LibraryGraphService();
        graphService.loadFromDatabase(loader);

        Graph<Location> graph = graphService.getGraph();
        assertEquals(50, graph.vertexCount());
        assertEquals(113, graph.edgeCount());
        assertTrue(graph.isValid());
    }

    @Test
    public void libraryGraphService_findsFastestRoute_betweenRealLocations() throws SQLException {
        LibraryDataLoader loader = new LibraryDataLoader();
        LibraryGraphService graphService = new LibraryGraphService();
        graphService.loadFromDatabase(loader);

        // Location 2 (Issue Desk) to Location 1 (Main Shelf A1)
        GraphAlgorithms.PathResult<Location> route = graphService.findFastestRoute(2, 1);
        assertTrue(route.isReachable());
        assertTrue(route.getTotalDistance() > 0);
        assertTrue(route.getPath().size() >= 2);
    }

    @Test
    public void libraryGraphService_reachableLocations_fromMainEntrance() throws SQLException {
        LibraryDataLoader loader = new LibraryDataLoader();
        LibraryGraphService graphService = new LibraryGraphService();
        graphService.loadFromDatabase(loader);

        CustomLinkedList<Location> reachable = graphService.getReachableLocations(1);
        assertTrue(reachable.size() > 0);
    }

    @Test
    public void libraryGraphService_corridorMST_spansLibraryNetwork() throws SQLException {
        LibraryDataLoader loader = new LibraryDataLoader();
        LibraryGraphService graphService = new LibraryGraphService();
        graphService.loadFromDatabase(loader);

        GraphAlgorithms.MSTResult<Location> mst = graphService.computeCorridorNetworkMST();
        assertTrue(mst.getTotalWeight() > 0);
        assertTrue(mst.getEdges().size() > 0);
    }
}
