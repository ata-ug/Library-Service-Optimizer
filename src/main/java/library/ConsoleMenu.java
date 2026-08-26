package library;

import algorithms.*;
import library.db.DatabaseSeeder;
import library.db.LibraryDataLoader;
import library.model.*;
import structures.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Interactive Console Menu Demonstration Runner.
 * Fully integrated with live SQLite persistence (library.db) across all options.
 */
public class ConsoleMenu {

    private final Scanner scanner;
    private final LibraryDataLoader loader;
    private final LibraryGraphService graphService;

    public ConsoleMenu() {
        this.scanner = new Scanner(System.in);
        this.loader = new LibraryDataLoader();
        this.graphService = new LibraryGraphService();
    }

    public static void main(String[] args) {
        ConsoleMenu menu = new ConsoleMenu();
        menu.start();
    }

    public void start() {
        System.out.println("==================================================================");
        System.out.println("   📚 BALME LIBRARY SERVICE OPERATIONS OPTIMIZER - DEMO MENU");
        System.out.println("==================================================================");

        // Attempt initial database load into Graph Engine
        try {
            graphService.loadFromDatabase(loader);
            System.out.println("✔ SQLite Database 'library.db' connected & graph initialized successfully.\n");
        } catch (Exception e) {
            System.out.println("⚠ Database load warning: " + e.getMessage());
            System.out.println("  (You can re-seed the database using Option 1 in the menu below.)\n");
        }

        boolean exit = false;
        while (!exit) {
            printMainMenu();
            String input = readInput("Select an option [1-8]: ");
            System.out.println();
            switch (input.trim()) {
                case "1":
                    demoDatabaseOperations();
                    break;
                case "2":
                    demoGraphRouting();
                    break;
                case "3":
                    demoSearchEngine();
                    break;
                case "4":
                    demoGreedyScheduling();
                    break;
                case "5":
                    demoKnapsackOptimization();
                    break;
                case "6":
                    demoCustomDataStructures();
                    break;
                case "7":
                    runSystemSmokeTest();
                    break;
                case "8":
                    exit = true;
                    System.out.println("Exiting Balme Library Operations Optimizer. Goodbye!");
                    break;
                default:
                    System.out.println("❌ Invalid option. Please select a number between 1 and 8.");
            }
            if (!exit) {
                System.out.println("\nPress Enter to return to main menu...");
                scanner.nextLine();
            }
        }
    }

    private void printMainMenu() {
        System.out.println("==================================================================");
        System.out.println("                         MAIN MENU");
        System.out.println("==================================================================");
        System.out.println("  1. 🗄  Database & Persistence Operations (Seed / Stats / Browse)");
        System.out.println("  2. 🗺  Spatial Graph Routing & Corridor Optimization (Live DB Graph)");
        System.out.println("  3. 🔍 Search Engine & Defensive Validation (Live DB Records)");
        System.out.println("  4. ⚡ Greedy Service Request Scheduling (Live DB Requests & Resources)");
        System.out.println("  5. 💼 0/1 Knapsack Request Selection (Live DB Service Tasks)");
        System.out.println("  6. 🧩 Custom Generic Data Structures Showcase (Populated from SQLite)");
        System.out.println("  7. 🧪 Full System DAO & Database Smoke Test");
        System.out.println("  8. ❌ Exit Program");
        System.out.println("==================================================================");
    }

    /* -----------------------------------------------------------------
     * Option 1: Database Operations
     * ----------------------------------------------------------------- */
    private void demoDatabaseOperations() {
        System.out.println("--- 🗄 Database & Persistence Operations ---");
        System.out.println("1. View Current Table Record Counts");
        System.out.println("2. Re-seed SQLite Database from CSV Datasets");
        System.out.println("3. Browse Live Database Records");
        String sub = readInput("Choice [1-3, default=1]: ");

        if ("2".equals(sub.trim())) {
            try {
                System.out.println("Seeding database from data/*.csv...");
                DatabaseSeeder.main(new String[0]);
                graphService.loadFromDatabase(loader);
                System.out.println("✔ Database re-seeded and graph re-loaded successfully!");
            } catch (Exception e) {
                System.out.println("❌ Database seeding failed: " + e.getMessage());
            }
        } else if ("3".equals(sub.trim())) {
            browseDatabaseRecords();
        } else {
            try {
                List<Location> locs = loader.loadLocations();
                List<Road> roads = loader.loadRoads();
                List<Member> members = loader.loadMembers();
                List<Book> books = loader.loadBooks();
                List<ServiceRequest> reqs = loader.loadServiceRequests();
                List<IssueLog> logs = loader.loadIssueLogs();
                List<AlgorithmRun> runs = loader.loadAlgorithmRuns();
                List<AuditEvent> events = loader.loadAuditEvents();

                System.out.println("\n📊 Current Database Statistics (library.db):");
                System.out.println("  • Locations:          " + locs.size());
                System.out.println("  • Roads (Corridors):  " + roads.size());
                System.out.println("  • Members:            " + members.size());
                System.out.println("  • Books:              " + books.size());
                System.out.println("  • Service Requests:   " + reqs.size());
                System.out.println("  • Issue Logs:         " + logs.size());
                System.out.println("  • Algorithm Telemetry:" + runs.size());
                System.out.println("  • Audit Journal:      " + events.size());
            } catch (SQLException e) {
                System.out.println("❌ Error fetching table counts: " + e.getMessage());
            }
        }
    }

    private void browseDatabaseRecords() {
        System.out.println("\n--- Browse Live Database Records ---");
        System.out.println("Select entity table to inspect:");
        System.out.println("1. Books  2. Locations  3. Members  4. Service Requests  5. Audit Journal");
        String choice = readInput("Select table [1-5, default=1]: ");
        try {
            switch (choice.trim()) {
                case "2":
                    List<Location> locs = loader.loadLocations();
                    System.out.println("\n--- Locations Table (Total: " + locs.size() + ") ---");
                    for (int i = 0; i < Math.min(8, locs.size()); i++) System.out.println("  " + locs.get(i));
                    break;
                case "3":
                    List<Member> members = loader.loadMembers();
                    System.out.println("\n--- Members Table (Total: " + members.size() + ") ---");
                    for (int i = 0; i < Math.min(8, members.size()); i++) System.out.println("  " + members.get(i));
                    break;
                case "4":
                    List<ServiceRequest> reqs = loader.loadServiceRequests();
                    System.out.println("\n--- Service Requests Table (Total: " + reqs.size() + ") ---");
                    for (int i = 0; i < Math.min(8, reqs.size()); i++) System.out.println("  " + reqs.get(i));
                    break;
                case "5":
                    List<AuditEvent> events = loader.loadAuditEvents();
                    System.out.println("\n--- Audit Events Journal (Total: " + events.size() + ") ---");
                    for (int i = 0; i < Math.min(8, events.size()); i++) System.out.println("  " + events.get(i));
                    break;
                default:
                    List<Book> books = loader.loadBooks();
                    System.out.println("\n--- Books Table (Total: " + books.size() + ") ---");
                    for (int i = 0; i < Math.min(8, books.size()); i++) System.out.println("  " + books.get(i));
                    break;
            }
        } catch (SQLException e) {
            System.out.println("❌ Database read error: " + e.getMessage());
        }
    }

    /* -----------------------------------------------------------------
     * Option 2: Spatial Graph Routing
     * ----------------------------------------------------------------- */
    private void demoGraphRouting() {
        System.out.println("--- 🗺 Spatial Graph Routing & Corridor Optimization ---");
        System.out.println("1. Dijkstra's Shortest Path Routing (Location ID to Location ID)");
        System.out.println("2. Route Pending Service Request directly from Database");
        System.out.println("3. BFS Reachability Analysis");
        System.out.println("4. Corridor Minimum Spanning Tree (Kruskal's & Prim's MST)");
        String sub = readInput("Choice [1-4, default=1]: ");

        switch (sub.trim()) {
            case "2":
                demoRouteDatabaseServiceRequest();
                break;
            case "3":
                demoBFS();
                break;
            case "4":
                demoMST();
                break;
            default:
                demoDijkstra();
                break;
        }
    }

    private void demoDijkstra() {
        System.out.println("\n--- Dijkstra Shortest Path Routing (Live Graph) ---");
        try {
            List<Location> locs = loader.loadLocations();
            if (locs.isEmpty()) {
                System.out.println("⚠ No locations found in database. Please seed database first.");
                return;
            }
            System.out.println("Sample Live Locations in Graph:");
            for (int i = 0; i < Math.min(5, locs.size()); i++) {
                Location l = locs.get(i);
                System.out.println("  ID " + l.locationId + ": " + l.name + " (" + l.type + ")");
            }

            int srcId = readInt("Enter Source Location ID [default=1]: ", 1);
            int dstId = readInt("Enter Destination Location ID [default=5]: ", 5);

            GraphAlgorithms.PathResult<Location> result = graphService.findFastestRoute(srcId, dstId);
            if (!result.isReachable()) {
                System.out.println("❌ No path found between location " + srcId + " and " + dstId);
            } else {
                System.out.println("\n✔ Route Found!");
                System.out.printf("  • Total Weighted Cost (Dist × Friction): %.2f meters/units%n", result.getTotalDistance());
                System.out.print("  • Path: ");
                CustomLinkedList<Location> path = result.getPath();
                for (int i = 0; i < path.size(); i++) {
                    System.out.print(path.get(i).name + (i < path.size() - 1 ? " ➔ " : ""));
                }
                System.out.println();
            }
        } catch (Exception e) {
            System.out.println("❌ Routing error: " + e.getMessage());
        }
    }

    private void demoRouteDatabaseServiceRequest() {
        System.out.println("\n--- Route Pending Service Request from SQLite Database ---");
        try {
            List<ServiceRequest> pending = loader.loadPendingServiceRequests();
            if (pending.isEmpty()) {
                System.out.println("⚠ No pending service requests in database.");
                return;
            }
            System.out.println("Sample Pending Service Requests in Database:");
            for (int i = 0; i < Math.min(6, pending.size()); i++) {
                ServiceRequest r = pending.get(i);
                Location src = graphService.getLocation(r.sourceLocationId != null ? r.sourceLocationId : 1);
                Location dst = graphService.getLocation(r.destinationLocationId != null ? r.destinationLocationId : 5);
                System.out.println("  Req #" + r.requestId + " [" + r.category + "] Urgency=" + r.urgency +
                        " | From: " + (src != null ? src.name : "ID " + r.sourceLocationId) +
                        " ➔ To: " + (dst != null ? dst.name : "ID " + r.destinationLocationId));
            }

            int reqId = readInt("\nEnter Request ID to Route [default=" + pending.get(0).requestId + "]: ", pending.get(0).requestId);
            ServiceRequest targetReq = null;
            for (ServiceRequest r : pending) {
                if (r.requestId == reqId) {
                    targetReq = r;
                    break;
                }
            }
            if (targetReq == null) targetReq = pending.get(0);

            GraphAlgorithms.PathResult<Location> result = graphService.routeServiceRequest(targetReq);
            System.out.println("\n✔ Routing Result for Service Request #" + targetReq.requestId + " (" + targetReq.category + "):");
            if (!result.isReachable()) {
                System.out.println("❌ Path Unreachable!");
            } else {
                System.out.printf("  • Total Route Distance / Cost: %.2f units%n", result.getTotalDistance());
                System.out.print("  • Dispatch Route: ");
                CustomLinkedList<Location> path = result.getPath();
                for (int i = 0; i < path.size(); i++) {
                    System.out.print(path.get(i).name + (i < path.size() - 1 ? " ➔ " : ""));
                }
                System.out.println();
            }
        } catch (Exception e) {
            System.out.println("❌ Request routing error: " + e.getMessage());
        }
    }

    private void demoBFS() {
        System.out.println("\n--- BFS Reachability Analysis (Live Graph) ---");
        int srcId = readInt("Enter Starting Location ID [default=1]: ", 1);
        try {
            CustomLinkedList<Location> reachable = graphService.getReachableLocations(srcId);
            System.out.println("✔ BFS Reachable Nodes from Location ID " + srcId + " (" + reachable.size() + " total):");
            for (int i = 0; i < reachable.size(); i++) {
                Location l = reachable.get(i);
                System.out.println("  [" + (i + 1) + "] ID " + l.locationId + ": " + l.name + " (" + l.type + ")");
            }
        } catch (Exception e) {
            System.out.println("❌ BFS error: " + e.getMessage());
        }
    }

    private void demoMST() {
        System.out.println("\n--- Corridor Network Minimum Spanning Tree (Live Graph MST) ---");
        try {
            GraphAlgorithms.MSTResult<Location> mstKruskal = GraphAlgorithms.kruskalMST(graphService.getGraph());
            GraphAlgorithms.MSTResult<Location> mstPrim = GraphAlgorithms.primMST(graphService.getGraph());

            System.out.println("✔ Kruskal's MST Result:");
            System.out.println("  • Edges in MST: " + mstKruskal.getEdges().size());
            System.out.printf("  • Total Network Weight: %.2f%n", mstKruskal.getTotalWeight());

            System.out.println("\n✔ Prim's MST Result:");
            System.out.println("  • Edges in MST: " + mstPrim.getEdges().size());
            System.out.printf("  • Total Network Weight: %.2f%n", mstPrim.getTotalWeight());
        } catch (Exception e) {
            System.out.println("❌ MST computation error: " + e.getMessage());
        }
    }

    /* -----------------------------------------------------------------
     * Option 3: Search Engine
     * ----------------------------------------------------------------- */
    private void demoSearchEngine() {
        System.out.println("--- 🔍 Search Engine & Defensive Validation ---");
        System.out.println("1. Search Books by Title (Live Database Books)");
        System.out.println("2. Search Pending Service Requests by ID (Interpolation Search)");
        System.out.println("3. Defensive Sortedness Precondition Demo (UnsortedDataException)");
        String sub = readInput("Choice [1-3, default=1]: ");

        switch (sub.trim()) {
            case "2":
                demoInterpolationSearch();
                break;
            case "3":
                demoSortednessException();
                break;
            default:
                demoBookSearch();
                break;
        }
    }

    private void demoBookSearch() {
        System.out.println("\n--- Book Search Demonstration (Live Database) ---");
        List<SearchEngine.Book> bookList = new ArrayList<>();
        try {
            List<Book> dbBooks = loader.loadBooks();
            for (Book b : dbBooks) {
                bookList.add(new SearchEngine.Book(b.bookId, b.isbn != null ? b.isbn : "", b.title));
            }
        } catch (Exception e) {
            System.out.println("⚠ Could not load database books: " + e.getMessage());
        }

        if (bookList.isEmpty()) {
            bookList.add(new SearchEngine.Book(101, "978-0134685991", "Algorithms in Java"));
            bookList.add(new SearchEngine.Book(102, "978-0262033848", "Introduction to Algorithms"));
        }

        System.out.println("Loaded " + bookList.size() + " books from SQLite Database. Sample Titles:");
        for (int i = 0; i < Math.min(5, bookList.size()); i++) {
            System.out.println("  • " + bookList.get(i).title);
        }

        String defaultTarget = bookList.get(0).title;
        String target = readInput("\nEnter book title to search [default='" + defaultTarget + "']: ");
        if (target.trim().isEmpty()) target = defaultTarget;

        int linearIdx = SearchEngine.linearSearchByTitle(bookList, target);
        System.out.println("\n✔ Linear Search Result: " + (linearIdx >= 0 ? "Found at index " + linearIdx + ": " + bookList.get(linearIdx) : "Not Found"));

        // Sort catalog for binary search
        bookList.sort((a, b) -> a.title.compareToIgnoreCase(b.title));
        try {
            int binaryIdx = SearchEngine.binarySearchByTitle(bookList, target);
            System.out.println("✔ Binary Search Result (Sorted): " + (binaryIdx >= 0 ? "Found at index " + binaryIdx + ": " + bookList.get(binaryIdx) : "Not Found"));
        } catch (Exception e) {
            System.out.println("❌ Binary Search Error: " + e.getMessage());
        }
    }

    private void demoInterpolationSearch() {
        System.out.println("\n--- Interpolation Search on Live Database Service Request IDs ---");
        List<SearchEngine.ServiceRequest> reqList = new ArrayList<>();
        try {
            List<ServiceRequest> dbReqs = loader.loadPendingServiceRequests();
            for (ServiceRequest r : dbReqs) {
                reqList.add(new SearchEngine.ServiceRequest(r.requestId, r.status));
            }
        } catch (Exception e) {
            System.out.println("⚠ Database load error: " + e.getMessage());
        }

        if (reqList.isEmpty()) {
            for (int i = 10; i <= 100; i += 10) reqList.add(new SearchEngine.ServiceRequest(i, "PENDING"));
        }

        reqList.sort((a, b) -> Integer.compare(a.requestId, b.requestId));
        System.out.println("Sorted Database Request IDs (" + reqList.size() + " total):");
        for (int i = 0; i < Math.min(10, reqList.size()); i++) {
            System.out.print(reqList.get(i).requestId + " ");
        }
        System.out.println("...");

        int defaultId = reqList.get(Math.min(3, reqList.size() - 1)).requestId;
        int targetId = readInt("Enter Target Request ID [default=" + defaultId + "]: ", defaultId);
        try {
            int idx = SearchEngine.interpolationSearchByRequestId(reqList, targetId);
            System.out.println("✔ Interpolation Search Result: " + (idx >= 0 ? "Found at index " + idx + " (" + reqList.get(idx) + ")" : "Not Found"));
        } catch (Exception e) {
            System.out.println("❌ Interpolation search failed: " + e.getMessage());
        }
    }

    private void demoSortednessException() {
        System.out.println("\n--- Defensive Sortedness Precondition Validation ---");
        System.out.println("Attempting Binary Search on intentionally UNSORTED database catalog...");
        List<SearchEngine.Book> unsortedBooks = new ArrayList<>();
        try {
            List<Book> dbBooks = loader.loadBooks();
            for (int i = dbBooks.size() - 1; i >= 0; i--) {
                Book b = dbBooks.get(i);
                unsortedBooks.add(new SearchEngine.Book(b.bookId, b.isbn != null ? b.isbn : "", b.title));
            }
        } catch (Exception e) {
            unsortedBooks.add(new SearchEngine.Book(1, "111", "Zebra Analytics"));
            unsortedBooks.add(new SearchEngine.Book(2, "222", "Artificial Intelligence"));
        }

        try {
            SearchEngine.binarySearchByTitle(unsortedBooks, "Data Structures");
        } catch (SearchEngine.UnsortedDataException e) {
            System.out.println("✔ Caught Expected Exception!");
            System.out.println("  Exception Type: SearchEngine.UnsortedDataException");
            System.out.println("  Message: " + e.getMessage());
        }
    }

    /* -----------------------------------------------------------------
     * Option 4: Greedy Scheduling
     * ----------------------------------------------------------------- */
    private void demoGreedyScheduling() {
        System.out.println("--- ⚡ Greedy Service Request Scheduling (Live DB Requests) ---");
        try {
            List<ServiceRequest> pendingReqs = loader.loadPendingServiceRequests();
            List<Resource> resources = loader.loadResources();

            int staff = 0, carts = 0, kiosks = 0;
            for (Resource r : resources) {
                if ("AVAILABLE".equalsIgnoreCase(r.availabilityStatus)) {
                    if ("STAFF".equalsIgnoreCase(r.type)) staff++;
                    else if ("CART".equalsIgnoreCase(r.type)) carts++;
                    else if ("KIOSK".equalsIgnoreCase(r.type)) kiosks++;
                }
            }

            System.out.println("SQLite Resources Available: Staff=" + staff + ", Carts=" + carts + ", Kiosks=" + kiosks);
            int userStaff = readInt("Enter Staff Count to allocate [default=" + Math.max(2, staff) + "]: ", Math.max(2, staff));
            int userCarts = readInt("Enter Cart Count to allocate [default=" + Math.max(1, carts) + "]: ", Math.max(1, carts));
            int userKiosks = readInt("Enter Kiosk Count to allocate [default=" + Math.max(1, kiosks) + "]: ", Math.max(1, kiosks));

            GreedyAlgorithms.runDemoWithDatabase(pendingReqs, userStaff, userCarts, userKiosks);
        } catch (Exception e) {
            System.out.println("❌ Error fetching live resources/requests: " + e.getMessage());
        }
    }

    /* -----------------------------------------------------------------
     * Option 5: 0/1 Knapsack DP
     * ----------------------------------------------------------------- */
    private void demoKnapsackOptimization() {
        System.out.println("--- 💼 0/1 Knapsack Request Selection (Live DB Service Tasks) ---");
        try {
            List<ServiceRequest> pendingReqs = loader.loadPendingServiceRequests();
            int capacity = readInt("Enter Staff Time Budget in Minutes [default=60]: ", 60);

            DynamicProgramming.runDemoWithDatabase(pendingReqs, capacity);
        } catch (Exception e) {
            System.out.println("❌ Error fetching live pending requests: " + e.getMessage());
        }
    }

    /* -----------------------------------------------------------------
     * Option 6: Custom Generic Data Structures
     * ----------------------------------------------------------------- */
    private void demoCustomDataStructures() {
        System.out.println("--- 🧩 Custom Generic Data Structures Suite Showcase ---");
        System.out.println("Testing zero-standard-library custom data structures populated from SQLite:\n");

        try {
            // 1. Min-Heap populated with real book available copy counts
            System.out.println("1. GenericHeap (Min-Heap of Book Available Copies):");
            List<Book> books = loader.loadBooks();
            GenericHeap<Integer> heap = new GenericHeap<>();
            for (int i = 0; i < Math.min(10, books.size()); i++) {
                heap.add(books.get(i).availableCopies);
            }
            System.out.print("   Inserted copy counts -> Polling Min Available Copies: ");
            while (!heap.isEmpty()) {
                System.out.print(heap.poll() + " ");
            }
            System.out.println();

            // 2. GenericHashtable populated with real Locations
            System.out.println("\n2. GenericHashtable (Chaining - Location Lookups):");
            List<Location> locs = loader.loadLocations();
            GenericHashtable<Integer, String> table = new GenericHashtable<>();
            for (Location l : locs) {
                table.put(l.locationId, l.name + " (" + l.type + ")");
            }
            System.out.println("   table.get(1) ➔ " + table.get(1));
            System.out.println("   table.get(2) ➔ " + table.get(2));

            // 3. GenericDisjointSet populated with real physical Corridor (Road) connections
            System.out.println("\n3. GenericDisjointSet (Union-Find Corridor Networks):");
            List<Road> roads = loader.loadRoads();
            GenericDisjointSet<Integer> ds = new GenericDisjointSet<>(100);
            for (Location l : locs) ds.makeSet(l.locationId);
            for (Road r : roads) ds.union(r.fromLocationId, r.toLocationId);
            System.out.println("   Union-Find Location Connectedness:");
            System.out.println("   connected(1, 2): " + ds.connected(1, 2));
            System.out.println("   connected(1, 5): " + ds.connected(1, 5));

            // 4. Red-Black Tree populated with Member Index Numbers
            System.out.println("\n4. RedBlackTree (Balanced Indexing of Member IDs):");
            List<Member> members = loader.loadMembers();
            RedBlackTree<String> rbt = new RedBlackTree<>();
            for (int i = 0; i < Math.min(15, members.size()); i++) {
                rbt.insert(members.get(i).indexNumber);
            }
            String testIndex = members.get(0).indexNumber;
            System.out.println("   search('" + testIndex + "'): " + rbt.search(testIndex) + ", search('INVALID-999'): " + rbt.search("INVALID-999"));

            // 5. GenericStack (Audit Undo Journal)
            System.out.println("\n5. GenericStack (Audit Undo Journal Stack):");
            List<AuditEvent> events = loader.loadAuditEvents();
            GenericStack<String> stack = new GenericStack<>();
            for (int i = 0; i < Math.min(10, events.size()); i++) {
                AuditEvent e = events.get(i);
                stack.push("EVENT #" + e.eventId + " [" + e.eventType + "] Target: " + e.entityType + ":" + e.entityId);
            }
            System.out.println("   Top of Audit Undo Stack: " + stack.peek());
            System.out.println("   Popping Top Event:        " + stack.pop());

        } catch (Exception e) {
            System.out.println("❌ Data structure showcase error: " + e.getMessage());
        }
    }

    /* -----------------------------------------------------------------
     * Option 7: Automated System Smoke Test
     * ----------------------------------------------------------------- */
    private void runSystemSmokeTest() {
        System.out.println("--- 🧪 Running Full Application Smoke Test ---");
        try {
            Main.main(new String[0]);
        } catch (Exception e) {
            System.out.println("❌ Smoke test error: " + e.getMessage());
        }
    }

    /* -----------------------------------------------------------------
     * Helper Methods
     * ----------------------------------------------------------------- */
    private String readInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private int readInt(String prompt, int defaultValue) {
        System.out.print(prompt);
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) return defaultValue;
        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            System.out.println("⚠ Invalid number, using default: " + defaultValue);
            return defaultValue;
        }
    }
}
