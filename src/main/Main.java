import java.io.File;
import java.io.IOException;

public class Main {
    // Detects whether we're running from src/ or project root
    private static String dataPath(String filename) {
        File fromSrc = new File("../data/" + filename);
        if (fromSrc.exists()) return "../data/" + filename;
        return "data/" + filename;
    }

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("LIBRARY DATA STRUCTURES - REAL DATA DEMO");
        System.out.println("========================================");

        try {
            demoLinkedList();
            demoQueue();
            demoDeque();
            demoCircularQueue();
            demoIntegration();
        } catch (IOException e) {
            System.out.println("ERROR reading CSV: " + e.getMessage());
            System.out.println("Make sure CSV files are in the 'data' folder.");
        }

        System.out.println("\n========================================");
        System.out.println("ALL DEMOS COMPLETED SUCCESSFULLY");
        System.out.println("========================================");
    }

    // ---------- LINKED LIST DEMO ----------
    static void demoLinkedList() throws IOException {
        System.out.println("\n--- CUSTOM LINKED LIST (Books) ---");
        CustomLinkedList<Book> books = CSVLoader.loadBooks(dataPath("books.csv"));
        System.out.println("Loaded " + books.size() + " books from CSV");

        // Show first 3 books
        System.out.println("First 3 books:");
        int count = 0;
        for (Book b : books) {
            System.out.println("  " + b.getBookId() + ": " + b.getTitle() + " by " + b.getAuthor());
            if (++count >= 3) break;
        }

        // Test contains and remove
        Book first = books.get(0);
        System.out.println("Contains first book? " + books.contains(first));
        books.remove(first);
        System.out.println("After removing first, size = " + books.size());
        System.out.println("Contains first book now? " + books.contains(first));

        // Edge: single item operations
        CustomLinkedList<Member> members = CSVLoader.loadMembers(dataPath("members.csv"));
        System.out.println("Loaded " + members.size() + " members");
        System.out.println("Member #1: " + members.get(0).getName() + " (" + members.get(0).getMembershipType() + ")");
    }

    // ---------- QUEUE DEMO ----------
    static void demoQueue() throws IOException {
        System.out.println("\n--- CUSTOM QUEUE (Pending Requests - FIFO) ---");
        CustomLinkedList<ServiceRequest> allReqs = CSVLoader.loadServiceRequests(dataPath("service_requests.csv"));
        CustomQueue<ServiceRequest> pendingQueue = new CustomQueue<>();

        // Enqueue only PENDING requests
        int pendingCount = 0;
        for (ServiceRequest r : allReqs) {
            if (r.getStatus().equals("PENDING")) {
                pendingQueue.enqueue(r);
                pendingCount++;
            }
        }
        System.out.println("Enqueued " + pendingCount + " PENDING requests");

        if (!pendingQueue.isEmpty()) {
            System.out.println("Front of queue: Request #" + pendingQueue.peek().getRequestId() +
                               " (Book " + pendingQueue.peek().getBookId() + ")");
            ServiceRequest handled = pendingQueue.dequeue();
            System.out.println("Dequeued: Request #" + handled.getRequestId() + " - " + handled.getCategory());
            System.out.println("Remaining in queue: " + pendingQueue.size());
        }
    }

    // ---------- DEQUE DEMO ----------
    static void demoDeque() throws IOException {
        System.out.println("\n--- CUSTOM DEQUE (High-Priority Requests) ---");
        CustomLinkedList<ServiceRequest> allReqs = CSVLoader.loadServiceRequests(dataPath("service_requests.csv"));
        CustomDeque<ServiceRequest> priorityDeque = new CustomDeque<>();

        // Add high-urgency (>=4) to front, low-urgency to back
        int added = 0;
        for (ServiceRequest r : allReqs) {
            if (added >= 10) break; // demo with 10 items
            if (r.getUrgency() >= 4) priorityDeque.addFirst(r);
            else priorityDeque.addLast(r);
            added++;
        }
        System.out.println("Loaded " + priorityDeque.size() + " requests into deque");
        System.out.println("Front (highest priority): Request #" + priorityDeque.peekFirst().getRequestId() +
                           " urgency=" + priorityDeque.peekFirst().getUrgency());
        System.out.println("Back (lowest priority): Request #" + priorityDeque.peekLast().getRequestId() +
                           " urgency=" + priorityDeque.peekLast().getUrgency());

        ServiceRequest urgent = priorityDeque.removeFirst();
        System.out.println("Handled urgent request #" + urgent.getRequestId());
        System.out.println("Remaining: " + priorityDeque.size());
    }

    // ---------- CIRCULAR QUEUE DEMO ----------
    static void demoCircularQueue() throws IOException {
        System.out.println("\n--- CIRCULAR QUEUE (Recent Requests Buffer) ---");
        CustomLinkedList<ServiceRequest> allReqs = CSVLoader.loadServiceRequests(dataPath("service_requests.csv"));
        CircularQueue<ServiceRequest> recentBuffer = new CircularQueue<>(5);

        // Fill buffer with first 5 requests
        int filled = 0;
        for (ServiceRequest r : allReqs) {
            if (filled >= 5) break;
            recentBuffer.enqueue(r);
            filled++;
        }
        System.out.println("Filled circular buffer (capacity=" + recentBuffer.capacity() + ")");
        System.out.println("Front: Request #" + recentBuffer.peek().getRequestId());
        System.out.println("Full? " + recentBuffer.isFull());

        // Dequeue oldest, enqueue newest (wrap-around demo)
        ServiceRequest oldest = recentBuffer.dequeue();
        System.out.println("Removed oldest: Request #" + oldest.getRequestId());

        // Find next request to add
        int skip = 5;
        for (ServiceRequest r : allReqs) {
            if (skip-- > 0) continue;
            recentBuffer.enqueue(r);
            System.out.println("Wrapped around and added Request #" + r.getRequestId());
            break;
        }
        System.out.println("Buffer size: " + recentBuffer.size() + ", Full? " + recentBuffer.isFull());
    }

    // ---------- INTEGRATION DEMO ----------
    static void demoIntegration() throws IOException {
        System.out.println("\n--- INTEGRATION: Loading All Tables ---");

        CustomLinkedList<Location> locations = CSVLoader.loadLocations(dataPath("locations.csv"));
        System.out.println("Locations: " + locations.size());

        CustomLinkedList<Road> roads = CSVLoader.loadRoads(dataPath("roads.csv"));
        System.out.println("Roads: " + roads.size());

        CustomLinkedList<Resource> resources = CSVLoader.loadResources(dataPath("resources.csv"));
        System.out.println("Resources: " + resources.size());

        CustomLinkedList<IssueLog> issueLogs = CSVLoader.loadIssueLogs(dataPath("issue_logs.csv"));
        System.out.println("Issue Logs: " + issueLogs.size());

        CustomLinkedList<AlgorithmRun> algoRuns = CSVLoader.loadAlgorithmRuns(dataPath("algorithm_runs.csv"));
        System.out.println("Algorithm Runs: " + algoRuns.size());

        CustomLinkedList<AuditEvent> auditEvents = CSVLoader.loadAuditEvents(dataPath("audit_events.csv"));
        System.out.println("Audit Events: " + auditEvents.size());

        CustomLinkedList<AlgorithmParameter> algoParams = CSVLoader.loadAlgorithmParameters(dataPath("algorithm_parameters.csv"));
        System.out.println("Algorithm Parameters: " + algoParams.size());

        // Show a cross-reference example
        CustomLinkedList<Book> books = CSVLoader.loadBooks(dataPath("books.csv"));
        CustomLinkedList<Member> members = CSVLoader.loadMembers(dataPath("members.csv"));

        Book sampleBook = books.get(0);
        Member sampleMember = members.get(0);
        System.out.println("\nSample cross-reference:");
        System.out.println("  Book: " + sampleBook.getTitle() + " (shelf location " + sampleBook.getShelfLocationId() + ")");
        System.out.println("  Member: " + sampleMember.getName() + " (" + sampleMember.getIndexNumber() + ")");
    }
}
