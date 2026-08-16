package algorithms;

import library.db.LibraryDataLoader;
import library.model.Location;
import library.model.Road;
import library.model.ServiceRequest;
import structures.CustomLinkedList;
import structures.GenericHashtable;
import structures.Graph;

import java.sql.SQLException;
import java.util.List;

/**
 * Bridge between the Database Persistence Layer and the Graph & Analytics Engine.
 *
 * Ingests staged Location and Road data from SQLite via LibraryDataLoader into our
 * hand-built Graph<Location> and provides operational routing methods to answer
 * core service questions:
 *  1. Fastest weighted route between two library nodes (Dijkstra).
 *  2. Reachable locations from a central dispatch desk (BFS).
 *  3. Optimal minimal corridor network (MST - Kruskal / Prim).
 *  4. Service request routing from origin desk/cart to shelf destination.
 */
public class LibraryGraphService {

    private final Graph<Location> graph;
    private final GenericHashtable<Integer, Location> locationById;

    public LibraryGraphService() {
        this.graph = new Graph<>(false); // Undirected library corridor network
        this.locationById = new GenericHashtable<>();
    }

    /**
     * Ingests locations and roads from SQLite via LibraryDataLoader into the custom Graph.
     *
     * @param loader initialized LibraryDataLoader
     * @throws SQLException if database read fails
     */
    public void loadFromDatabase(LibraryDataLoader loader) throws SQLException {
        List<Location> locations = loader.loadLocations();
        List<Road> roads = loader.loadRoads();

        // 1. Register all location nodes
        for (Location loc : locations) {
            locationById.put(loc.locationId, loc);
            graph.addVertex(loc);
        }

        // 2. Register all road edges with effective weight = distance * road_condition_weight
        for (Road road : roads) {
            if (locationById.containsKey(road.fromLocationId) && locationById.containsKey(road.toLocationId)) {
                Location from = locationById.get(road.fromLocationId);
                Location to = locationById.get(road.toLocationId);
                double effectiveWeight = road.distance * road.roadConditionWeight;
                graph.addEdge(from, to, effectiveWeight);
            }
        }
    }

    public Graph<Location> getGraph() {
        return graph;
    }

    public Location getLocation(int locationId) {
        if (!locationById.containsKey(locationId)) {
            return null;
        }
        return locationById.get(locationId);
    }

    /**
     * Answers: "What is the fastest route between two locations under real road conditions?"
     *
     * @param fromLocationId origin location ID
     * @param toLocationId target location ID
     * @return PathResult containing the path of Locations, total weighted distance, and reachability
     */
    public GraphAlgorithms.PathResult<Location> findFastestRoute(int fromLocationId, int toLocationId) {
        Location src = getLocation(fromLocationId);
        Location dst = getLocation(toLocationId);
        if (src == null || dst == null) {
            return new GraphAlgorithms.PathResult<>(src, dst, new CustomLinkedList<>(), Double.POSITIVE_INFINITY, false);
        }
        return GraphAlgorithms.dijkstra(graph, src, dst);
    }

    /**
     * Answers: "Which locations are reachable from a given dispatch point?"
     *
     * @param startLocationId dispatch location ID
     * @return CustomLinkedList of all reachable Location nodes
     */
    public CustomLinkedList<Location> getReachableLocations(int startLocationId) {
        Location start = getLocation(startLocationId);
        if (start == null) {
            return new CustomLinkedList<>();
        }
        return GraphAlgorithms.bfs(graph, start);
    }

    /**
     * Answers: "What is the optimal corridor interconnect network (MST) connecting all library sections?"
     *
     * @return MSTResult with the minimum set of corridor edges and total network weight
     */
    public GraphAlgorithms.MSTResult<Location> computeCorridorNetworkMST() {
        return GraphAlgorithms.kruskalMST(graph);
    }

    /**
     * Routes a specific service request from its source desk/station to the destination shelf.
     *
     * @param request the service request to route
     * @return PathResult of the route
     */
    public GraphAlgorithms.PathResult<Location> routeServiceRequest(ServiceRequest request) {
        if (request.sourceLocationId == null || request.destinationLocationId == null) {
            return new GraphAlgorithms.PathResult<>(null, null, new CustomLinkedList<>(), Double.POSITIVE_INFINITY, false);
        }
        return findFastestRoute(request.sourceLocationId, request.destinationLocationId);
    }
}
