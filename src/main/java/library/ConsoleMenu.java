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
 * Allows examiners and reviewers to run end-to-end algorithmic, spatial,
 * database, and data structure demonstrations without editing source code.
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
        System.out.println("  1. 🗄  Database & Persistence Operations (Seed / Stats)");
        System.out.println("  2. 🗺  Spatial Graph Routing & Network Optimization (Dijkstra / MST / BFS)");
        System.out.println("  3. 🔍 Search Engine & Defensive Validation (Linear / Binary / Interpolation)");
        System.out.println("  4. ⚡ Greedy Service Request Scheduling & Resource Allocation");
        System.out.println("  5. 💼 0/1 Knapsack Request Selection (Dynamic Programming)");
        System.out.println("  6. 🧩 Custom Generic Data Structures Suite Showcase");
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
        String sub = readInput("Choice [1-2, default=1]: ");
        if ("2".equals(sub.trim())) {
            try {
                System.out.println("Seeding database from data/*.csv...");
                DatabaseSeeder.main(new String[0]);
                graphService.loadFromDatabase(loader);
                System.out.println("✔ Database re-seeded and graph re-loaded successfully!");
            } catch (Exception e) {
                System.out.println("❌ Database seeding failed: " + e.getMessage());
            }
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

                System.out.println("\n📊 Current Database Statistics:");
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

    /* -----------------------------------------------------------------
     * Option 2: Spatial Graph Routing
     * ----------------------------------------------------------------- */
    private void demoGraphRouting() {
        System.out.println("--- 🗺 Spatial Graph Routing & Corridor Optimization ---");
        System.out.println("1. Dijkstra's Shortest Path Routing (Cart / Personnel Dispatch)");
        System.out.println("2. BFS Reachability Analysis");
        System.out.println("3. Corridor Minimum Spanning Tree (Kruskal's & Prim's MST)");
        String sub = readInput("Choice [1-3, default=1]: ");

        switch (sub.trim()) {
            case "2":
                demoBFS();
                break;
            case "3":
                demoMST();
                break;
            default:
                demoDijkstra();
                break;
        }
    }

    private void demoDijkstra() {
        System.out.println("\n--- Dijkstra Shortest Path Routing ---");
        try {
            List<Location> locs = loader.loadLocations();
            if (locs.isEmpty()) {
                System.out.println("⚠ No locations found in database. Please seed database first.");
                return;
            }
            System.out.println("Sample Locations Available:");
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

    private void demoBFS() {
        System.out.println("\n--- BFS Reachability Analysis ---");
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
        System.out.println("\n--- Corridor Network Minimum Spanning Tree (MST) ---");
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
        System.out.println("1. Search Books by Title (Linear vs Binary Search)");
        System.out.println("2. Search Service Requests by ID (Interpolation Search)");
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
        System.out.println("\n--- Book Search Demonstration ---");
        List<SearchEngine.Book> bookList = new ArrayList<>();
        try {
            List<library.model.Book> dbBooks = loader.loadBooks();
            for (library.model.Book b : dbBooks) {
                bookList.add(new SearchEngine.Book(b.bookId, b.isbn != null ? b.isbn : "", b.title));
            }
        } catch (Exception e) {
            System.out.println("⚠ Could not load database books: " + e.getMessage());
        }

        if (bookList.isEmpty()) {
            bookList.add(new SearchEngine.Book(101, "978-0134685991", "Algorithms in Java"));
            bookList.add(new SearchEngine.Book(102, "978-0262033848", "Introduction to Algorithms"));
            bookList.add(new SearchEngine.Book(103, "978-0321573513", "Operating System Concepts"));
            bookList.add(new SearchEngine.Book(104, "978-0596009205", "Head First Design Patterns"));
        }

        System.out.println("Loaded " + bookList.size() + " books from Database / CSV Catalog. Sample Titles:");
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
        System.out.println("\n--- Interpolation Search on Service Request IDs ---");
        List<SearchEngine.ServiceRequest> reqList = new ArrayList<>();
        for (int i = 10; i <= 100; i += 10) {
            reqList.add(new SearchEngine.ServiceRequest(i, "PENDING"));
        }
        System.out.println("Sorted Uniform Request ID List:");
        for (SearchEngine.ServiceRequest r : reqList) System.out.print(r.requestId + " ");
        System.out.println();

        int targetId = readInt("Enter Target Request ID [default=50]: ", 50);
        try {
            int idx = SearchEngine.interpolationSearchByRequestId(reqList, targetId);
            System.out.println("✔ Interpolation Search Result: " + (idx >= 0 ? "Found at index " + idx + " (" + reqList.get(idx) + ")" : "Not Found"));
        } catch (Exception e) {
            System.out.println("❌ Interpolation search failed: " + e.getMessage());
        }
    }

    private void demoSortednessException() {
        System.out.println("\n--- Defensive Sortedness Precondition Validation ---");
        System.out.println("Attempting Binary Search on intentionally UNSORTED catalog...");
        List<SearchEngine.Book> unsortedBooks = new ArrayList<>();
        unsortedBooks.add(new SearchEngine.Book(1, "111", "Zebra Analytics"));
        unsortedBooks.add(new SearchEngine.Book(2, "222", "Artificial Intelligence"));
        unsortedBooks.add(new SearchEngine.Book(3, "333", "Data Structures"));

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
        System.out.println("--- ⚡ Greedy Service Request Scheduling ---");
        int staff = readInt("Available Staff Count [default=2]: ", 2);
        int carts = readInt("Available Cart Count [default=1]: ", 1);
        int kiosks = readInt("Available Kiosk Count [default=1]: ", 1);

        GreedyAlgorithms.runDemo(staff, carts, kiosks);
    }

    /* -----------------------------------------------------------------
     * Option 5: 0/1 Knapsack DP
     * ----------------------------------------------------------------- */
    private void demoKnapsackOptimization() {
        System.out.println("--- 💼 0/1 Knapsack Request Selection (Dynamic Programming) ---");
        int capacity = readInt("Staff Time Budget in Minutes [default=50]: ", 50);

        DynamicProgramming.runDemo(capacity);
    }

    /* -----------------------------------------------------------------
     * Option 6: Custom Generic Data Structures
     * ----------------------------------------------------------------- */
    private void demoCustomDataStructures() {
        System.out.println("--- 🧩 Custom Generic Data Structures Suite Showcase ---");
        System.out.println("Testing zero-standard-library custom data structures:\n");

        // 1. Min-Heap
        System.out.println("1. GenericHeap (Min-Heap):");
        GenericHeap<Integer> heap = new GenericHeap<>();
        heap.add(42); heap.add(15); heap.add(88); heap.add(8);
        System.out.print("   Inserted [42, 15, 88, 8] -> Extracting Mins: ");
        while (!heap.isEmpty()) {
            System.out.print(heap.poll() + " ");
        }
        System.out.println();

        // 2. GenericHashtable
        System.out.println("2. GenericHashtable (Chaining):");
        GenericHashtable<String, String> table = new GenericHashtable<>();
        table.put("SHELF_A", "Computer Science Section");
        table.put("DESK_1", "Main Entrance Kiosk");
        System.out.println("   get('SHELF_A') ➔ " + table.get("SHELF_A"));
        System.out.println("   get('DESK_1')   ➔ " + table.get("DESK_1"));

        // 3. GenericDisjointSet
        System.out.println("3. GenericDisjointSet (Union-Find):");
        GenericDisjointSet<String> ds = new GenericDisjointSet<>(10);
        ds.makeSet("NodeA"); ds.makeSet("NodeB"); ds.makeSet("NodeC");
        ds.union("NodeA", "NodeB");
        System.out.println("   union('NodeA', 'NodeB') -> connected('NodeA', 'NodeB'): " + ds.connected("NodeA", "NodeB"));
        System.out.println("   connected('NodeA', 'NodeC'): " + ds.connected("NodeA", "NodeC"));

        // 4. Red-Black Tree
        System.out.println("4. RedBlackTree (Balanced BST):");
        RedBlackTree<Integer> rbt = new RedBlackTree<>();
        rbt.insert(30); rbt.insert(10); rbt.insert(50);
        System.out.println("   search(50): " + rbt.search(50) + ", search(99): " + rbt.search(99));

        // 5. GenericStack (Undo Engine)
        System.out.println("5. GenericStack (Audit Undo Journal):");
        GenericStack<String> stack = new GenericStack<>();
        stack.push("EVENT_1: BORROW Book #101");
        stack.push("EVENT_2: RETURN Book #105");
        System.out.println("   Top of Undo Stack: " + stack.peek());
        System.out.println("   Popping Top Event: " + stack.pop());
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
