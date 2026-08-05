package library.model;

/** Mirrors the `issue_logs` table. Historical record once a service_request is fulfilled. */
public class IssueLog {
    public int issueLogId;
    public int requestId;
    public int bookId;
    public int memberId;
    public String issueDate;
    public String dueDate;
    public String returnDate; // nullable until returned
    public double fineAmount;

    public IssueLog() { }

    public IssueLog(int issueLogId, int requestId, int bookId, int memberId,
                     String issueDate, String dueDate, String returnDate, double fineAmount) {
        this.issueLogId = issueLogId;
        this.requestId = requestId;
        this.bookId = bookId;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.fineAmount = fineAmount;
    }

    @Override
    public String toString() {
        return "IssueLog{id=" + issueLogId + ", request=" + requestId +
               ", due=" + dueDate + ", returned=" + returnDate + "}";
    }
}
