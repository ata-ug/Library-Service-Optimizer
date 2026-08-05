package library.model;

/** Mirrors the `resources` table. Staff, book carts, or kiosks that fulfill requests. */
public class Resource {
    public int resourceId;
    public String type; // STAFF | CART | KIOSK
    public Integer homeLocationId; // nullable
    public int capacity;
    public String availabilityStatus; // AVAILABLE | BUSY | OFFLINE

    public Resource() { }

    public Resource(int resourceId, String type, Integer homeLocationId,
                     int capacity, String availabilityStatus) {
        this.resourceId = resourceId;
        this.type = type;
        this.homeLocationId = homeLocationId;
        this.capacity = capacity;
        this.availabilityStatus = availabilityStatus;
    }

    @Override
    public String toString() {
        return "Resource{id=" + resourceId + ", type=" + type + ", status=" + availabilityStatus + "}";
    }
}
