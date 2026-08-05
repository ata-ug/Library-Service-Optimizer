package library.db;

import library.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data & DB squad's loader: reads rows straight off library.db via plain
 * JDBC (no ORM) and hands each squad plain Java objects (POJOs from
 * library.model.*).
 *
 * DESIGN NOTE FOR THE TEAM / ORAL DEFENSE:
 * These load* methods return java.util.ArrayList<T> purely as a staging
 * container -- it is NOT the graded data structure. The rubric bans
 * built-in HashMap/TreeMap/PriorityQueue/Stack/ArrayDeque for GRADED LOGIC
 * (i.e. the structures Structures Core squad hand-builds and the algorithms
 * that run over them). This loader's only job is "rows out of SQLite,
 * POJOs in memory" -- it hands the ArrayList off, and each squad is
 * responsible for feeding these POJOs into their own hand-built structure
 * (e.g. Graph & Analytics builds its adjacency list from loadRoads(),
 * Algorithms Engine builds its heap from loadServiceRequests(), etc).
 * If asked at defense: "the loader stages data, it does not implement any
 * graded structure or algorithm."
 *
 * Every method opens and closes its own connection (try-with-resources) so
 * this class has no shared mutable state and is safe to call from multiple
 * squads independently.
 */
public class LibraryDataLoader {

    // ------------------------------------------------------------
    // locations
    // ------------------------------------------------------------
    public List<Location> loadLocations() throws SQLException {
        String sql = "SELECT location_id, name, area, type, latitude, longitude FROM locations";
        List<Location> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(new Location(
                    rs.getInt("location_id"),
                    rs.getString("name"),
                    rs.getString("area"),
                    rs.getString("type"),
                    nullableDouble(rs, "latitude"),
                    nullableDouble(rs, "longitude")
                ));
            }
        }
        return results;
    }

    // ------------------------------------------------------------
    // roads
    // ------------------------------------------------------------
    public List<Road> loadRoads() throws SQLException {
        String sql = "SELECT road_id, from_location_id, to_location_id, distance, " +
                     "travel_time, road_condition_weight FROM roads";
        List<Road> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(new Road(
                    rs.getInt("road_id"),
                    rs.getInt("from_location_id"),
                    rs.getInt("to_location_id"),
                    rs.getDouble("distance"),
                    rs.getDouble("travel_time"),
                    rs.getDouble("road_condition_weight")
                ));
            }
        }
        return results;
    }

    // ------------------------------------------------------------
    // members
    // ------------------------------------------------------------
    public List<Member> loadMembers() throws SQLException {
        String sql = "SELECT member_id, index_number, name, membership_type, " +
                     "registered_date FROM members";
        List<Member> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(new Member(
                    rs.getInt("member_id"),
                    rs.getString("index_number"),
                    rs.getString("name"),
                    rs.getString("membership_type"),
                    rs.getString("registered_date")
                ));
            }
        }
        return results;
    }

    // ------------------------------------------------------------
    // books
    // ------------------------------------------------------------
    public List<Book> loadBooks() throws SQLException {
        String sql = "SELECT book_id, isbn, title, author, category, shelf_location_id, " +
                     "total_copies, available_copies FROM books";
        List<Book> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(new Book(
                    rs.getInt("book_id"),
                    rs.getString("isbn"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("category"),
                    nullableInt(rs, "shelf_location_id"),
                    rs.getInt("total_copies"),
                    rs.getInt("available_copies")
                ));
            }
        }
        return results;
    }

    // ------------------------------------------------------------
    // service_requests
    // ------------------------------------------------------------
    public List<ServiceRequest> loadServiceRequests() throws SQLException {
        String sql = "SELECT request_id, member_id, book_id, source_location_id, " +
                     "destination_location_id, category, urgency, time_submitted, " +
                     "deadline, status FROM service_requests";
        List<ServiceRequest> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(new ServiceRequest(
                    rs.getInt("request_id"),
                    rs.getInt("member_id"),
                    rs.getInt("book_id"),
                    nullableInt(rs, "source_location_id"),
                    nullableInt(rs, "destination_location_id"),
                    rs.getString("category"),
                    rs.getInt("urgency"),
                    rs.getString("time_submitted"),
                    rs.getString("deadline"),
                    rs.getString("status")
                ));
            }
        }
        return results;
    }

    /** Convenience overload: only requests still awaiting service (feeds live scheduling demos). */
    public List<ServiceRequest> loadPendingServiceRequests() throws SQLException {
        String sql = "SELECT request_id, member_id, book_id, source_location_id, " +
                     "destination_location_id, category, urgency, time_submitted, " +
                     "deadline, status FROM service_requests WHERE status = 'PENDING'";
        List<ServiceRequest> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(new ServiceRequest(
                    rs.getInt("request_id"),
                    rs.getInt("member_id"),
                    rs.getInt("book_id"),
                    nullableInt(rs, "source_location_id"),
                    nullableInt(rs, "destination_location_id"),
                    rs.getString("category"),
                    rs.getInt("urgency"),
                    rs.getString("time_submitted"),
                    rs.getString("deadline"),
                    rs.getString("status")
                ));
            }
        }
        return results;
    }

    // ------------------------------------------------------------
    // issue_logs
    // ------------------------------------------------------------
    public List<IssueLog> loadIssueLogs() throws SQLException {
        String sql = "SELECT issue_log_id, request_id, book_id, member_id, issue_date, " +
                     "due_date, return_date, fine_amount FROM issue_logs";
        List<IssueLog> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(new IssueLog(
                    rs.getInt("issue_log_id"),
                    rs.getInt("request_id"),
                    rs.getInt("book_id"),
                    rs.getInt("member_id"),
                    rs.getString("issue_date"),
                    rs.getString("due_date"),
                    rs.getString("return_date"),
                    rs.getDouble("fine_amount")
                ));
            }
        }
        return results;
    }

    // ------------------------------------------------------------
    // resources
    // ------------------------------------------------------------
    public List<Resource> loadResources() throws SQLException {
        String sql = "SELECT resource_id, type, home_location_id, capacity, " +
                     "availability_status FROM resources";
        List<Resource> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(new Resource(
                    rs.getInt("resource_id"),
                    rs.getString("type"),
                    nullableInt(rs, "home_location_id"),
                    rs.getInt("capacity"),
                    rs.getString("availability_status")
                ));
            }
        }
        return results;
    }

    // ------------------------------------------------------------
    // algorithm_runs
    // ------------------------------------------------------------
    public List<AlgorithmRun> loadAlgorithmRuns() throws SQLException {
        String sql = "SELECT run_id, algorithm_name, input_size, time_ns, memory_kb, " +
                     "date_run FROM algorithm_runs";
        List<AlgorithmRun> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(new AlgorithmRun(
                    rs.getInt("run_id"),
                    rs.getString("algorithm_name"),
                    rs.getInt("input_size"),
                    rs.getLong("time_ns"),
                    nullableDouble(rs, "memory_kb"),
                    rs.getString("date_run")
                ));
            }
        }
        return results;
    }

    /** Writes a single performance measurement back to algorithm_runs (used by M10 experiments). */
    public void recordAlgorithmRun(String algorithmName, int inputSize, long timeNs,
                                    Double memoryKb, String dateRun) throws SQLException {
        String sql = "INSERT INTO algorithm_runs (algorithm_name, input_size, time_ns, " +
                     "memory_kb, date_run) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, algorithmName);
            ps.setInt(2, inputSize);
            ps.setLong(3, timeNs);
            if (memoryKb == null) ps.setNull(4, java.sql.Types.REAL); else ps.setDouble(4, memoryKb);
            ps.setString(5, dateRun);
            ps.executeUpdate();
        }
    }

    // ------------------------------------------------------------
    // audit_events
    // ------------------------------------------------------------
    public List<AuditEvent> loadAuditEvents() throws SQLException {
        String sql = "SELECT event_id, event_type, entity_type, entity_id, performed_by, " +
                     "event_details, event_timestamp, is_undone FROM audit_events " +
                     "ORDER BY event_timestamp";
        List<AuditEvent> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(new AuditEvent(
                    rs.getInt("event_id"),
                    rs.getString("event_type"),
                    rs.getString("entity_type"),
                    rs.getInt("entity_id"),
                    rs.getString("performed_by"),
                    rs.getString("event_details"),
                    rs.getString("event_timestamp"),
                    rs.getInt("is_undone") == 1
                ));
            }
        }
        return results;
    }

    /** Pushes a new audit event -- pairs with the hand-built Stack for undo. */
    public void pushAuditEvent(String eventType, String entityType, int entityId,
                                String performedBy, String eventDetails,
                                String eventTimestamp) throws SQLException {
        String sql = "INSERT INTO audit_events (event_type, entity_type, entity_id, " +
                     "performed_by, event_details, event_timestamp, is_undone) " +
                     "VALUES (?, ?, ?, ?, ?, ?, 0)";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, eventType);
            ps.setString(2, entityType);
            ps.setInt(3, entityId);
            ps.setString(4, performedBy);
            ps.setString(5, eventDetails);
            ps.setString(6, eventTimestamp);
            ps.executeUpdate();
        }
    }

    // ------------------------------------------------------------
    // algorithm_parameters
    // ------------------------------------------------------------
    public List<AlgorithmParameter> loadAlgorithmParameters() throws SQLException {
        String sql = "SELECT param_id, member_index_number, param_name, derived_value, " +
                     "derivation_note FROM algorithm_parameters";
        List<AlgorithmParameter> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(new AlgorithmParameter(
                    rs.getInt("param_id"),
                    rs.getString("member_index_number"),
                    rs.getString("param_name"),
                    rs.getDouble("derived_value"),
                    rs.getString("derivation_note")
                ));
            }
        }
        return results;
    }

    /** Fetches only the parameters for one member's index number (used at oral defense). */
    public List<AlgorithmParameter> loadAlgorithmParametersFor(String indexNumber) throws SQLException {
        String sql = "SELECT param_id, member_index_number, param_name, derived_value, " +
                     "derivation_note FROM algorithm_parameters WHERE member_index_number = ?";
        List<AlgorithmParameter> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, indexNumber);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(new AlgorithmParameter(
                        rs.getInt("param_id"),
                        rs.getString("member_index_number"),
                        rs.getString("param_name"),
                        rs.getDouble("derived_value"),
                        rs.getString("derivation_note")
                    ));
                }
            }
        }
        return results;
    }

    // ------------------------------------------------------------
    // helpers -- JDBC returns 0 (not null) for SQL NULL numeric columns
    // unless you check wasNull(), so nullable FK/optional columns go
    // through these two helpers instead of rs.getInt/getDouble directly.
    // ------------------------------------------------------------
    private static Integer nullableInt(ResultSet rs, String column) throws SQLException {
        int value = rs.getInt(column);
        return rs.wasNull() ? null : value;
    }

    private static Double nullableDouble(ResultSet rs, String column) throws SQLException {
        double value = rs.getDouble(column);
        return rs.wasNull() ? null : value;
    }
}
