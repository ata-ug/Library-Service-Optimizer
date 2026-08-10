import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * CSV Loader that reads CSV files using only BufferedReader and String.split().
 * No built-in Java collections are used. Data is loaded directly into our
 * custom data structures.
 */
public class CSVLoader {

    /**
     * Resolves a file path relative to the project root.
     * Assumes CSV files live in a 'data' folder next to 'src'.
     */
    public static String resolvePath(String filename) {
        return "data/" + filename;
    }

    // ---------- locations ----------
    public static CustomLinkedList<Location> loadLocations(String filepath) throws IOException {
        CustomLinkedList<Location> list = new CustomLinkedList<>();
        BufferedReader br = new BufferedReader(new FileReader(filepath));
        String line;
        boolean first = true;
        while ((line = br.readLine()) != null) {
            if (first) { first = false; continue; } // skip header
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split(",", -1);
            int id = Integer.parseInt(parts[0].trim());
            String name = parts[1].trim();
            String area = parts[2].trim();
            String type = parts[3].trim();
            double lat = parts[4].trim().isEmpty() ? 0.0 : Double.parseDouble(parts[4].trim());
            double lon = parts[5].trim().isEmpty() ? 0.0 : Double.parseDouble(parts[5].trim());
            list.addLast(new Location(id, name, area, type, lat, lon));
        }
        br.close();
        return list;
    }

    // ---------- roads ----------
    public static CustomLinkedList<Road> loadRoads(String filepath) throws IOException {
        CustomLinkedList<Road> list = new CustomLinkedList<>();
        BufferedReader br = new BufferedReader(new FileReader(filepath));
        String line;
        boolean first = true;
        while ((line = br.readLine()) != null) {
            if (first) { first = false; continue; }
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split(",", -1);
            int id = Integer.parseInt(parts[0].trim());
            int from = Integer.parseInt(parts[1].trim());
            int to = Integer.parseInt(parts[2].trim());
            double dist = Double.parseDouble(parts[3].trim());
            double time = Double.parseDouble(parts[4].trim());
            double weight = Double.parseDouble(parts[5].trim());
            list.addLast(new Road(id, from, to, dist, time, weight));
        }
        br.close();
        return list;
    }

    // ---------- books ----------
    public static CustomLinkedList<Book> loadBooks(String filepath) throws IOException {
        CustomLinkedList<Book> list = new CustomLinkedList<>();
        BufferedReader br = new BufferedReader(new FileReader(filepath));
        String line;
        boolean first = true;
        while ((line = br.readLine()) != null) {
            if (first) { first = false; continue; }
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split(",", -1);
            int id = Integer.parseInt(parts[0].trim());
            String isbn = parts[1].trim();
            String title = parts[2].trim();
            String author = parts[3].trim();
            String category = parts[4].trim();
            int shelfId = Integer.parseInt(parts[5].trim());
            int total = Integer.parseInt(parts[6].trim());
            int avail = Integer.parseInt(parts[7].trim());
            list.addLast(new Book(id, isbn, title, author, category, shelfId, total, avail));
        }
        br.close();
        return list;
    }

    // ---------- members ----------
    public static CustomLinkedList<Member> loadMembers(String filepath) throws IOException {
        CustomLinkedList<Member> list = new CustomLinkedList<>();
        BufferedReader br = new BufferedReader(new FileReader(filepath));
        String line;
        boolean first = true;
        while ((line = br.readLine()) != null) {
            if (first) { first = false; continue; }
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split(",", -1);
            int id = Integer.parseInt(parts[0].trim());
            String index = parts[1].trim();
            String name = parts[2].trim();
            String type = parts[3].trim();
            String date = parts[4].trim();
            list.addLast(new Member(id, index, name, type, date));
        }
        br.close();
        return list;
    }

    // ---------- service_requests ----------
    public static CustomLinkedList<ServiceRequest> loadServiceRequests(String filepath) throws IOException {
        CustomLinkedList<ServiceRequest> list = new CustomLinkedList<>();
        BufferedReader br = new BufferedReader(new FileReader(filepath));
        String line;
        boolean first = true;
        while ((line = br.readLine()) != null) {
            if (first) { first = false; continue; }
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split(",", -1);
            int reqId = Integer.parseInt(parts[0].trim());
            int memId = Integer.parseInt(parts[1].trim());
            int bookId = Integer.parseInt(parts[2].trim());
            int srcLoc = Integer.parseInt(parts[3].trim());
            int dstLoc = Integer.parseInt(parts[4].trim());
            String category = parts[5].trim();
            int urgency = Integer.parseInt(parts[6].trim());
            String submitted = parts[7].trim();
            String deadline = parts[8].trim();
            String status = parts[9].trim();
            list.addLast(new ServiceRequest(reqId, memId, bookId, srcLoc, dstLoc, category, urgency, submitted, deadline, status));
        }
        br.close();
        return list;
    }

    // ---------- issue_logs ----------
    public static CustomLinkedList<IssueLog> loadIssueLogs(String filepath) throws IOException {
        CustomLinkedList<IssueLog> list = new CustomLinkedList<>();
        BufferedReader br = new BufferedReader(new FileReader(filepath));
        String line;
        boolean first = true;
        while ((line = br.readLine()) != null) {
            if (first) { first = false; continue; }
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split(",", -1);
            int issueId = Integer.parseInt(parts[0].trim());
            int reqId = Integer.parseInt(parts[1].trim());
            int bookId = Integer.parseInt(parts[2].trim());
            int memId = Integer.parseInt(parts[3].trim());
            String issueDate = parts[4].trim();
            String dueDate = parts[5].trim();
            String returnDate = parts[6].trim();
            double fine = parts[7].trim().isEmpty() ? 0.0 : Double.parseDouble(parts[7].trim());
            list.addLast(new IssueLog(issueId, reqId, bookId, memId, issueDate, dueDate, returnDate, fine));
        }
        br.close();
        return list;
    }

    // ---------- resources ----------
    public static CustomLinkedList<Resource> loadResources(String filepath) throws IOException {
        CustomLinkedList<Resource> list = new CustomLinkedList<>();
        BufferedReader br = new BufferedReader(new FileReader(filepath));
        String line;
        boolean first = true;
        while ((line = br.readLine()) != null) {
            if (first) { first = false; continue; }
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split(",", -1);
            int id = Integer.parseInt(parts[0].trim());
            String type = parts[1].trim();
            int homeLoc = Integer.parseInt(parts[2].trim());
            int cap = Integer.parseInt(parts[3].trim());
            String status = parts[4].trim();
            list.addLast(new Resource(id, type, homeLoc, cap, status));
        }
        br.close();
        return list;
    }

    // ---------- algorithm_runs ----------
    public static CustomLinkedList<AlgorithmRun> loadAlgorithmRuns(String filepath) throws IOException {
        CustomLinkedList<AlgorithmRun> list = new CustomLinkedList<>();
        BufferedReader br = new BufferedReader(new FileReader(filepath));
        String line;
        boolean first = true;
        while ((line = br.readLine()) != null) {
            if (first) { first = false; continue; }
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split(",", -1);
            int id = Integer.parseInt(parts[0].trim());
            String name = parts[1].trim();
            int input = Integer.parseInt(parts[2].trim());
            long time = Long.parseLong(parts[3].trim());
            double mem = parts[4].trim().isEmpty() ? 0.0 : Double.parseDouble(parts[4].trim());
            String date = parts[5].trim();
            list.addLast(new AlgorithmRun(id, name, input, time, mem, date));
        }
        br.close();
        return list;
    }

    // ---------- audit_events ----------
    public static CustomLinkedList<AuditEvent> loadAuditEvents(String filepath) throws IOException {
        CustomLinkedList<AuditEvent> list = new CustomLinkedList<>();
        BufferedReader br = new BufferedReader(new FileReader(filepath));
        String line;
        boolean first = true;
        while ((line = br.readLine()) != null) {
            if (first) { first = false; continue; }
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split(",", -1);
            int id = Integer.parseInt(parts[0].trim());
            String type = parts[1].trim();
            String entityType = parts[2].trim();
            int entityId = Integer.parseInt(parts[3].trim());
            String by = parts[4].trim();
            String details = parts[5].trim();
            String ts = parts[6].trim();
            int undone = Integer.parseInt(parts[7].trim());
            list.addLast(new AuditEvent(id, type, entityType, entityId, by, details, ts, undone));
        }
        br.close();
        return list;
    }

    // ---------- algorithm_parameters ----------
    public static CustomLinkedList<AlgorithmParameter> loadAlgorithmParameters(String filepath) throws IOException {
        CustomLinkedList<AlgorithmParameter> list = new CustomLinkedList<>();
        BufferedReader br = new BufferedReader(new FileReader(filepath));
        String line;
        boolean first = true;
        while ((line = br.readLine()) != null) {
            if (first) { first = false; continue; }
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split(",", -1);
            int id = Integer.parseInt(parts[0].trim());
            String index = parts[1].trim();
            String name = parts[2].trim();
            double value = Double.parseDouble(parts[3].trim());
            String note = parts[4].trim();
            list.addLast(new AlgorithmParameter(id, index, name, value, note));
        }
        br.close();
        return list;
    }
}
