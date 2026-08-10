public class ServiceRequest {
    private int requestId;
    private int memberId;
    private int bookId;
    private int sourceLocationId;
    private int destinationLocationId;
    private String category;
    private int urgency;
    private String timeSubmitted;
    private String deadline;
    private String status;

    public ServiceRequest(int requestId, int memberId, int bookId, int sourceLocationId, int destinationLocationId,
                          String category, int urgency, String timeSubmitted, String deadline, String status) {
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

    public int getRequestId() { return requestId; }
    public int getMemberId() { return memberId; }
    public int getBookId() { return bookId; }
    public int getSourceLocationId() { return sourceLocationId; }
    public int getDestinationLocationId() { return destinationLocationId; }
    public String getCategory() { return category; }
    public int getUrgency() { return urgency; }
    public String getTimeSubmitted() { return timeSubmitted; }
    public String getDeadline() { return deadline; }
    public String getStatus() { return status; }

    @Override
    public String toString() {
        return "ServiceRequest{id=" + requestId + ", bookId=" + bookId + ", memberId=" + memberId + ", status='" + status + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceRequest that = (ServiceRequest) o;
        return requestId == that.requestId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(requestId);
    }
}
