package library.model;

/** Mirrors the `service_requests` table. The jobs queued/prioritised/sorted by Algorithms Engine. */
public class ServiceRequest {
    public int requestId;
    public int memberId;
    public int bookId;
    public Integer sourceLocationId;      // nullable
    public Integer destinationLocationId; // nullable, derived from book's shelf location
    public String category;   // BORROW | RETURN | RESERVE | RENEW
    public int urgency;       // priority weight, derived per-member from index_number
    public String timeSubmitted;
    public String deadline;   // nullable
    public String status;     // PENDING | IN_PROGRESS | FULFILLED | CANCELLED

    public ServiceRequest() { }

    public ServiceRequest(int requestId, int memberId, int bookId,
                           Integer sourceLocationId, Integer destinationLocationId,
                           String category, int urgency, String timeSubmitted,
                           String deadline, String status) {
        this.requestId = requestId;
        this.memberId = memberId;
        this.bookId = bookId;
        this.sourceLocationId = sourceLocationId;
        this.destinationLocationId = destinationLocationId;
        this.category = category;
        this.urgency = urgency;
        this.timeSubmitted = timeSubmitted;
        this.deadline = deadline;
        this.status = status;
    }

    @Override
    public String toString() {
        return "ServiceRequest{id=" + requestId + ", member=" + memberId +
               ", book=" + bookId + ", urgency=" + urgency + ", status=" + status + "}";
    }
}
