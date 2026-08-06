package library.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility to initialize the SQLite database schema and seed all tables from
 * CSV files in the data/ directory.
 */
public class DatabaseSeeder {

    private static final String DATA_DIR = "data";

    public static void main(String[] args) {
        try {
            System.out.println("Initializing Database Schema...");
            initSchema();
            System.out.println("Seeding data from CSV files...");
            seedDatabase();
            System.out.println("Database seeding completed successfully!");
        } catch (Exception e) {
            System.err.println("Seeding failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Executes schema.sql script to ensure tables exist in library.db.
     */
    public static void initSchema() throws SQLException {
        String schemaSql = loadSchemaSql();
        if (schemaSql == null || schemaSql.isBlank()) {
            System.err.println("Could not load schema.sql");
            return;
        }

        // Remove SQL comments before splitting by semicolon
        String cleanSql = schemaSql.replaceAll("(?m)^\\s*--.*$", "").replaceAll("(?m)--.*$", "");
        String[] statements = cleanSql.split(";");
        for (String sql : statements) {
            String trimmed = sql.trim();
            if (!trimmed.isEmpty() && !trimmed.toUpperCase().startsWith("PRAGMA")) {
                trimmed = trimmed.replaceAll("(?i)\\bCREATE\\s+TABLE\\s+", "CREATE TABLE IF NOT EXISTS ");
                trimmed = trimmed.replaceAll("(?i)\\bCREATE\\s+INDEX\\s+", "CREATE INDEX IF NOT EXISTS ");
                try (Connection conn = DatabaseConnection.connect();
                     Statement stmt = conn.createStatement()) {
                    stmt.execute(trimmed);
                }
            }
        }
        System.out.println("Schema applied successfully.");
    }

    /**
     * Seeds all 10 tables from CSV files into SQLite database.
     */
    public static void seedDatabase() throws SQLException {
        File dataFolder = new File(DATA_DIR);
        if (!dataFolder.exists() || !dataFolder.isDirectory()) {
            System.err.println("Data folder '" + DATA_DIR + "' not found.");
            return;
        }

        try (Connection conn = DatabaseConnection.connect()) {
            conn.setAutoCommit(false);
            try {
                // Clear existing data in reverse FK order to prevent FK violations
                clearTables(conn);

                seedLocations(conn, new File(dataFolder, "locations.csv"));
                seedRoads(conn, new File(dataFolder, "roads.csv"));
                seedMembers(conn, new File(dataFolder, "members.csv"));
                seedBooks(conn, new File(dataFolder, "books.csv"));
                seedServiceRequests(conn, new File(dataFolder, "service_requests.csv"));
                seedIssueLogs(conn, new File(dataFolder, "issue_logs.csv"));
                seedResources(conn, new File(dataFolder, "resources.csv"));
                seedAlgorithmRuns(conn, new File(dataFolder, "algorithm_runs.csv"));
                seedAuditEvents(conn, new File(dataFolder, "audit_events.csv"));
                seedAlgorithmParameters(conn, new File(dataFolder, "algorithm parameters.csv"));

                conn.commit();
                System.out.println("All CSV datasets successfully imported into library.db.");
            } catch (Exception e) {
                conn.rollback();
                throw new SQLException("Transaction rolled back due to error: " + e.getMessage(), e);
            }
        }
    }

    private static void clearTables(Connection conn) throws SQLException {
        String[] tables = {
            "algorithm_parameters", "audit_events", "algorithm_runs",
            "resources", "issue_logs", "service_requests", "books",
            "members", "roads", "locations"
        };
        try (Statement stmt = conn.createStatement()) {
            for (String table : tables) {
                stmt.execute("DELETE FROM " + table + ";");
            }
        }
    }

    private static void seedLocations(Connection conn, File file) throws Exception {
        if (!file.exists()) return;
        String sql = "INSERT INTO locations (location_id, name, area, type, latitude, longitude) VALUES (?, ?, ?, ?, ?, ?)";
        List<List<String>> rows = readCsv(file);
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (List<String> row : rows) {
                if (row.size() < 4) continue;
                ps.setInt(1, Integer.parseInt(row.get(0)));
                ps.setString(2, row.get(1));
                ps.setString(3, row.get(2));
                ps.setString(4, row.get(3));
                setNullableDouble(ps, 5, row.size() > 4 ? row.get(4) : "");
                setNullableDouble(ps, 6, row.size() > 5 ? row.get(5) : "");
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("Seeded locations: " + rows.size() + " rows.");
        }
    }

    private static void seedRoads(Connection conn, File file) throws Exception {
        if (!file.exists()) return;
        String sql = "INSERT INTO roads (road_id, from_location_id, to_location_id, distance, travel_time, road_condition_weight) VALUES (?, ?, ?, ?, ?, ?)";
        List<List<String>> rows = readCsv(file);
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (List<String> row : rows) {
                if (row.size() < 5) continue;
                ps.setInt(1, Integer.parseInt(row.get(0)));
                ps.setInt(2, Integer.parseInt(row.get(1)));
                ps.setInt(3, Integer.parseInt(row.get(2)));
                ps.setDouble(4, Double.parseDouble(row.get(3)));
                ps.setDouble(5, Double.parseDouble(row.get(4)));
                ps.setDouble(6, row.size() > 5 && !row.get(5).isEmpty() ? Double.parseDouble(row.get(5)) : 1.0);
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("Seeded roads: " + rows.size() + " rows.");
        }
    }

    private static void seedMembers(Connection conn, File file) throws Exception {
        if (!file.exists()) return;
        String sql = "INSERT INTO members (member_id, index_number, name, membership_type, registered_date) VALUES (?, ?, ?, ?, ?)";
        List<List<String>> rows = readCsv(file);
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (List<String> row : rows) {
                if (row.size() < 5) continue;
                ps.setInt(1, Integer.parseInt(row.get(0)));
                ps.setString(2, row.get(1));
                ps.setString(3, row.get(2));
                ps.setString(4, row.get(3));
                ps.setString(5, row.get(4));
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("Seeded members: " + rows.size() + " rows.");
        }
    }

    private static void seedBooks(Connection conn, File file) throws Exception {
        if (!file.exists()) return;
        String sql = "INSERT INTO books (book_id, isbn, title, author, category, shelf_location_id, total_copies, available_copies) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        List<List<String>> rows = readCsv(file);
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (List<String> row : rows) {
                if (row.size() < 5) continue;
                ps.setInt(1, Integer.parseInt(row.get(0)));
                setNullableString(ps, 2, row.get(1));
                ps.setString(3, row.get(2));
                ps.setString(4, row.get(3));
                ps.setString(5, row.get(4));
                setNullableInt(ps, 6, row.size() > 5 ? row.get(5) : "");
                ps.setInt(7, row.size() > 6 && !row.get(6).isEmpty() ? Integer.parseInt(row.get(6)) : 1);
                ps.setInt(8, row.size() > 7 && !row.get(7).isEmpty() ? Integer.parseInt(row.get(7)) : 1);
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("Seeded books: " + rows.size() + " rows.");
        }
    }

    private static void seedServiceRequests(Connection conn, File file) throws Exception {
        if (!file.exists()) return;
        String sql = "INSERT INTO service_requests (request_id, member_id, book_id, source_location_id, destination_location_id, category, urgency, time_submitted, deadline, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        List<List<String>> rows = readCsv(file);
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (List<String> row : rows) {
                if (row.size() < 8) continue;
                ps.setInt(1, Integer.parseInt(row.get(0)));
                ps.setInt(2, Integer.parseInt(row.get(1)));
                ps.setInt(3, Integer.parseInt(row.get(2)));
                setNullableInt(ps, 4, row.get(3));
                setNullableInt(ps, 5, row.get(4));
                ps.setString(6, row.get(5));
                ps.setInt(7, Integer.parseInt(row.get(6)));
                ps.setString(8, row.get(7));
                setNullableString(ps, 9, row.size() > 8 ? row.get(8) : "");
                ps.setString(10, row.size() > 9 && !row.get(9).isEmpty() ? row.get(9) : "PENDING");
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("Seeded service_requests: " + rows.size() + " rows.");
        }
    }

    private static void seedIssueLogs(Connection conn, File file) throws Exception {
        if (!file.exists()) return;
        String sql = "INSERT INTO issue_logs (issue_log_id, request_id, book_id, member_id, issue_date, due_date, return_date, fine_amount) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        List<List<String>> rows = readCsv(file);
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (List<String> row : rows) {
                if (row.size() < 6) continue;
                ps.setInt(1, Integer.parseInt(row.get(0)));
                ps.setInt(2, Integer.parseInt(row.get(1)));
                ps.setInt(3, Integer.parseInt(row.get(2)));
                ps.setInt(4, Integer.parseInt(row.get(3)));
                ps.setString(5, row.get(4));
                ps.setString(6, row.get(5));
                setNullableString(ps, 7, row.size() > 6 ? row.get(6) : "");
                setNullableDouble(ps, 8, row.size() > 7 ? row.get(7) : "0.0");
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("Seeded issue_logs: " + rows.size() + " rows.");
        }
    }

    private static void seedResources(Connection conn, File file) throws Exception {
        if (!file.exists()) return;
        String sql = "INSERT INTO resources (resource_id, type, home_location_id, capacity, availability_status) VALUES (?, ?, ?, ?, ?)";
        List<List<String>> rows = readCsv(file);
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (List<String> row : rows) {
                if (row.size() < 5) continue;
                ps.setInt(1, Integer.parseInt(row.get(0)));
                ps.setString(2, row.get(1));
                setNullableInt(ps, 3, row.get(2));
                ps.setInt(4, Integer.parseInt(row.get(3)));
                ps.setString(5, row.get(4));
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("Seeded resources: " + rows.size() + " rows.");
        }
    }

    private static void seedAlgorithmRuns(Connection conn, File file) throws Exception {
        if (!file.exists()) return;
        String sql = "INSERT INTO algorithm_runs (run_id, algorithm_name, input_size, time_ns, memory_kb, date_run) VALUES (?, ?, ?, ?, ?, ?)";
        List<List<String>> rows = readCsv(file);
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (List<String> row : rows) {
                if (row.size() < 6) continue;
                ps.setInt(1, Integer.parseInt(row.get(0)));
                ps.setString(2, row.get(1));
                ps.setInt(3, Integer.parseInt(row.get(2)));
                ps.setLong(4, Long.parseLong(row.get(3)));
                setNullableDouble(ps, 5, row.get(4));
                ps.setString(6, row.get(5));
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("Seeded algorithm_runs: " + rows.size() + " rows.");
        }
    }

    private static void seedAuditEvents(Connection conn, File file) throws Exception {
        if (!file.exists()) return;
        String sql = "INSERT INTO audit_events (event_id, event_type, entity_type, entity_id, performed_by, event_details, event_timestamp, is_undone) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        List<List<String>> rows = readCsv(file);
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (List<String> row : rows) {
                if (row.size() < 8) continue;
                ps.setInt(1, Integer.parseInt(row.get(0)));
                ps.setString(2, row.get(1));
                ps.setString(3, row.get(2));
                ps.setInt(4, Integer.parseInt(row.get(3)));
                setNullableString(ps, 5, row.get(4));
                setNullableString(ps, 6, row.get(5));
                ps.setString(7, row.get(6));
                ps.setInt(8, Integer.parseInt(row.get(7)));
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("Seeded audit_events: " + rows.size() + " rows.");
        }
    }

    private static void seedAlgorithmParameters(Connection conn, File file) throws Exception {
        if (!file.exists()) return;
        List<List<String>> rows = readCsv(file);

        // Ensure member_index_number exists in members table to avoid FK violation
        Set<String> existingMemberIndexes = getExistingMemberIndexes(conn);
        String insertMemberSql = "INSERT INTO members (index_number, name, membership_type, registered_date) VALUES (?, ?, 'STUDENT', '2024-01-01')";
        try (PreparedStatement psMem = conn.prepareStatement(insertMemberSql)) {
            boolean added = false;
            for (List<String> row : rows) {
                if (row.size() < 5) continue;
                String indexNo = row.get(1);
                if (!existingMemberIndexes.contains(indexNo)) {
                    psMem.setString(1, indexNo);
                    psMem.setString(2, "Student " + indexNo);
                    psMem.addBatch();
                    existingMemberIndexes.add(indexNo);
                    added = true;
                }
            }
            if (added) {
                psMem.executeBatch();
            }
        }

        String sql = "INSERT INTO algorithm_parameters (param_id, member_index_number, param_name, derived_value, derivation_note) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (List<String> row : rows) {
                if (row.size() < 5) continue;
                ps.setInt(1, Integer.parseInt(row.get(0)));
                ps.setString(2, row.get(1));
                ps.setString(3, row.get(2));
                ps.setDouble(4, Double.parseDouble(row.get(3)));
                ps.setString(5, row.get(4));
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("Seeded algorithm_parameters: " + rows.size() + " rows.");
        }
    }

    private static Set<String> getExistingMemberIndexes(Connection conn) throws SQLException {
        Set<String> indexes = new HashSet<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT index_number FROM members")) {
            while (rs.next()) {
                indexes.add(rs.getString("index_number"));
            }
        }
        return indexes;
    }

    private static String loadSchemaSql() {
        try (InputStream is = DatabaseSeeder.class.getResourceAsStream("/schema.sql")) {
            if (is != null) {
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (Exception ignored) {}

        File schemaFile = new File("src/main/resources/schema.sql");
        if (schemaFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(schemaFile, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static List<List<String>> readCsv(File file) throws Exception {
        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            boolean isHeader = true;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (isHeader) {
                    isHeader = false;
                    continue; // Skip CSV header line
                }
                records.add(parseCsvLine(line));
            }
        }
        return records;
    }

    public static List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        if (line == null) return result;
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(cur.toString().trim());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        result.add(cur.toString().trim());
        return result;
    }

    private static void setNullableInt(PreparedStatement ps, int paramIndex, String val) throws SQLException {
        if (val == null || val.isBlank()) {
            ps.setNull(paramIndex, Types.INTEGER);
        } else {
            ps.setInt(paramIndex, Integer.parseInt(val));
        }
    }

    private static void setNullableDouble(PreparedStatement ps, int paramIndex, String val) throws SQLException {
        if (val == null || val.isBlank()) {
            ps.setNull(paramIndex, Types.DOUBLE);
        } else {
            ps.setDouble(paramIndex, Double.parseDouble(val));
        }
    }

    private static void setNullableString(PreparedStatement ps, int paramIndex, String val) throws SQLException {
        if (val == null || val.isBlank()) {
            ps.setNull(paramIndex, Types.VARCHAR);
        } else {
            ps.setString(paramIndex, val);
        }
    }
}
