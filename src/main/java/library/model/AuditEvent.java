package library.model;

/** Mirrors the `audit_events` table. Stack-based undo/audit trail. */
public class AuditEvent {
    public int eventId;
    public String eventType;    // ISSUE | RETURN | REQUEST_CREATED | REQUEST_CANCELLED | UNDO
    public String entityType;   // table name affected
    public int entityId;        // row id affected
    public String performedBy;  // nullable
    public String eventDetails; // nullable, snapshot of prior state for undo
    public String eventTimestamp;
    public boolean isUndone;

    public AuditEvent() { }

    public AuditEvent(int eventId, String eventType, String entityType, int entityId,
                       String performedBy, String eventDetails, String eventTimestamp,
                       boolean isUndone) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.entityType = entityType;
        this.entityId = entityId;
        this.performedBy = performedBy;
        this.eventDetails = eventDetails;
        this.eventTimestamp = eventTimestamp;
        this.isUndone = isUndone;
    }

    @Override
    public String toString() {
        return "AuditEvent{id=" + eventId + ", type=" + eventType +
               ", entity=" + entityType + ":" + entityId + ", undone=" + isUndone + "}";
    }
}
