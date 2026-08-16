package library.model;

/** Mirrors the `locations` table. Every physical node in the library graph. */
public class Location {
    public int locationId;
    public String name;
    public String area;
    public String type;      // SHELF | DESK | ROOM | ENTRANCE | STAFF_ROOM
    public Double latitude;  // nullable
    public Double longitude; // nullable

    public Location() { }

    public Location(int locationId, String name, String area, String type,
                     Double latitude, Double longitude) {
        this.locationId = locationId;
        this.name = name;
        this.area = area;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return locationId == location.locationId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(locationId);
    }

    @Override
    public String toString() {
        return "Location{id=" + locationId + ", name='" + name + "', type=" + type + "}";
    }
}
