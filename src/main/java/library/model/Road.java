package library.model;

/** Mirrors the `roads` table. Weighted edge feeding Dijkstra/Prim/Kruskal/BFS/DFS. */
public class Road {
    public int roadId;
    public int fromLocationId;
    public int toLocationId;
    public double distance;
    public double travelTime;
    public double roadConditionWeight;

    public Road() { }

    public Road(int roadId, int fromLocationId, int toLocationId,
                double distance, double travelTime, double roadConditionWeight) {
        this.roadId = roadId;
        this.fromLocationId = fromLocationId;
        this.toLocationId = toLocationId;
        this.distance = distance;
        this.travelTime = travelTime;
        this.roadConditionWeight = roadConditionWeight;
    }

    @Override
    public String toString() {
        return "Road{id=" + roadId + ", " + fromLocationId + " -> " + toLocationId +
               ", distance=" + distance + "}";
    }
}
