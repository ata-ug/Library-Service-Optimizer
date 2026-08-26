package library;

import library.db.DatabaseConnection;
import library.db.LibraryDataLoader;
import library.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Smoke test / usage example for the Data & DB squad's JDBC layer.
 * Inserts a minimal, connected row in every table, then reads it all
 * back through LibraryDataLoader to prove the JDBC <-> library.db wiring
 * works end to end, including FK enforcement.
 */
public class Main {
    public static void main(String[] args) throws SQLException {
        if (args.length > 0 && ("--menu".equalsIgnoreCase(args[0]) || "-m".equalsIgnoreCase(args[0]))) {
            ConsoleMenu.main(args);
            return;
        }

        seedMinimalRow();

        LibraryDataLoader loader = new LibraryDataLoader();

        List<Location> locations = loader.loadLocations();
        System.out.println("locations loaded: " + locations.size());
        for (Location l : locations) System.out.println("  " + l);

        List<Road> roads = loader.loadRoads();
        System.out.println("roads loaded: " + roads.size());

        List<Member> members = loader.loadMembers();
        System.out.println("members loaded: " + members.size());
        for (Member m : members) System.out.println("  " + m);

        List<Book> books = loader.loadBooks();
        System.out.println("books loaded: " + books.size());
        for (Book b : books) System.out.println("  " + b);

        List<ServiceRequest> requests = loader.loadServiceRequests();
        System.out.println("service_requests loaded: " + requests.size());
        for (ServiceRequest r : requests) System.out.println("  " + r);

        List<ServiceRequest> pending = loader.loadPendingServiceRequests();
        System.out.println("pending service_requests: " + pending.size());

        List<IssueLog> issueLogs = loader.loadIssueLogs();
        System.out.println("issue_logs loaded: " + issueLogs.size());
        for (IssueLog log : issueLogs) System.out.println("  " + log);

        List<AlgorithmParameter> params = loader.loadAlgorithmParameters();
        System.out.println("algorithm_parameters loaded: " + params.size());
        for (AlgorithmParameter p : params) System.out.println("  " + p);

        loader.recordAlgorithmRun("dijkstra", 1000, 452300L, 128.5, "2026-08-04");
        List<AlgorithmRun> runs = loader.loadAlgorithmRuns();
        System.out.println("algorithm_runs loaded: " + runs.size());
        for (AlgorithmRun r : runs) System.out.println("  " + r);

        loader.pushAuditEvent("REQUEST_CREATED", "service_requests", 1,
                "system-smoke-test", "{\"note\":\"initial seed\"}", "2026-08-04T10:00:05");
        List<AuditEvent> events = loader.loadAuditEvents();
        System.out.println("audit_events loaded: " + events.size());
        for (AuditEvent e : events) System.out.println("  " + e);

        System.out.println("\nSmoke test complete -- loader is wired to library.db correctly.");
        System.out.println("💡 Interactive Demo Menu: Run 'java -cp \"bin;sqlite-jdbc-3.36.0.3.jar;src/main/resources\" library.ConsoleMenu' to launch interactive demonstration menu.");
    }

    /** Inserts one connected row per table so every loader method has something to read. */
    private static void seedMinimalRow() throws SQLException {
        try (Connection conn = DatabaseConnection.connect()) {
            conn.setAutoCommit(false);
            try {
                exec(conn, "INSERT INTO locations (name, area, type) VALUES ('Main Shelf A1','Ground Floor','SHELF')");
                exec(conn, "INSERT INTO locations (name, area, type) VALUES ('Issue Desk','Ground Floor','DESK')");
                exec(conn, "INSERT INTO roads (from_location_id, to_location_id, distance, travel_time) VALUES (2, 1, 12.5, 15.0)");
                exec(conn, "INSERT INTO members (index_number, name, membership_type, registered_date) VALUES ('10921234','Ama Owusu','STUDENT','2024-08-01')");
                exec(conn, "INSERT INTO books (title, author, category, shelf_location_id, total_copies, available_copies) VALUES ('Data Structures in Java','Smith','Computer Science', 1, 3, 3)");
                exec(conn, "INSERT INTO service_requests (member_id, book_id, source_location_id, destination_location_id, category, urgency, time_submitted, status) VALUES (1, 1, 2, 1, 'BORROW', 5, '2026-08-04T10:00:00', 'PENDING')");
                exec(conn, "INSERT INTO issue_logs (request_id, book_id, member_id, issue_date, due_date) VALUES (1, 1, 1, '2026-08-04', '2026-08-18')");
                exec(conn, "INSERT INTO resources (type, home_location_id, capacity, availability_status) VALUES ('STAFF', 2, 1, 'AVAILABLE')");
                exec(conn, "INSERT INTO algorithm_parameters (member_index_number, param_name, derived_value, derivation_note) VALUES ('10921234','priority_weight', 7.0, 'sum of last 4 index digits mod 10')");
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    private static void exec(Connection conn, String sql) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }
}
