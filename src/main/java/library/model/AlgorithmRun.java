package library.model;

/** Mirrors the `algorithm_runs` table. Empirical runtime/memory measurements for M10. */
public class AlgorithmRun {
    public int runId;
    public String algorithmName; // e.g. 'dijkstra', 'merge_sort'
    public int inputSize;
    public long timeNs;
    public Double memoryKb; // nullable
    public String dateRun;

    public AlgorithmRun() { }

    public AlgorithmRun(int runId, String algorithmName, int inputSize,
                         long timeNs, Double memoryKb, String dateRun) {
        this.runId = runId;
        this.algorithmName = algorithmName;
        this.inputSize = inputSize;
        this.timeNs = timeNs;
        this.memoryKb = memoryKb;
        this.dateRun = dateRun;
    }

    @Override
    public String toString() {
        return "AlgorithmRun{id=" + runId + ", algo=" + algorithmName +
               ", n=" + inputSize + ", timeNs=" + timeNs + "}";
    }
}
