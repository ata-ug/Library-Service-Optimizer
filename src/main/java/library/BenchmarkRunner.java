package library;

import library.db.DatabaseConnection;
import library.db.LibraryDataLoader;
import library.model.AlgorithmRun;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

/**
 * Performance Analysis Benchmark Runner for Library Service Optimizer.
 * Evaluates empirical execution time (ns/ms) and memory usage (KB) across
 * input sizes N = 100, N = 1,000, N = 10,000 for all core algorithms.
 */
public class BenchmarkRunner {

    public static void main(String[] args) {
        System.out.println("==========================================================================================");
        System.out.println("                   LIBRARY SERVICE OPTIMIZER - PERFORMANCE ANALYSIS                       ");
        System.out.println("==========================================================================================");
        
        try {
            LibraryDataLoader loader = new LibraryDataLoader();
            List<AlgorithmRun> runs = loader.loadAlgorithmRuns();

            if (runs.isEmpty()) {
                System.out.println("No benchmark records found in database. Running DatabaseSeeder first...");
                library.db.DatabaseSeeder.main(args);
                runs = loader.loadAlgorithmRuns();
            }

            System.out.printf("%-6s | %-16s | %-10s | %-14s | %-12s | %-12s | %-10s%n",
                    "Run ID", "Algorithm Name", "Input Size", "Time (ns)", "Time (ms)", "Memory (KB)", "Date Run");
            System.out.println("------------------------------------------------------------------------------------------");

            for (AlgorithmRun r : runs) {
                double timeMs = r.timeNs / 1_000_000.0;
                double memoryKb = r.memoryKb != null ? r.memoryKb : 0.0;
                System.out.printf("%-6d | %-16s | %-10d | %-14d | %-12.3f | %-12.2f | %-10s%n",
                        r.runId,
                        r.algorithmName,
                        r.inputSize,
                        r.timeNs,
                        timeMs,
                        memoryKb,
                        r.dateRun);
            }

            System.out.println("------------------------------------------------------------------------------------------");
            System.out.println("Total benchmark records loaded: " + runs.size());
            System.out.println("==========================================================================================");

            printCategorySummary();

        } catch (Exception e) {
            System.err.println("Error executing performance analysis: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printCategorySummary() {
        System.out.println("\n==========================================================================================");
        System.out.println("                         ALGORITHM SCALING ANALYSIS (N = 10,000)                          ");
        System.out.println("==========================================================================================");
        String sql = "SELECT algorithm_name, time_ns, memory_kb FROM algorithm_runs WHERE input_size = 10000 ORDER BY time_ns ASC;";
        
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.printf("%-18s | %-16s | %-14s | %-20s%n",
                    "Algorithm", "Time (ms)", "Memory (KB)", "Asymptotic Complexity");
            System.out.println("------------------------------------------------------------------------------------------");

            while (rs.next()) {
                String name = rs.getString("algorithm_name");
                long ns = rs.getLong("time_ns");
                double mem = rs.getDouble("memory_kb");
                double ms = ns / 1_000_000.0;
                String complexity = getComplexityLabel(name);

                System.out.printf("%-18s | %-16.3f | %-14.2f | %-20s%n",
                        name, ms, mem, complexity);
            }
            System.out.println("==========================================================================================");

        } catch (Exception e) {
            System.err.println("Failed to print summary: " + e.getMessage());
        }
    }

    private static String getComplexityLabel(String algo) {
        return switch (algo.toLowerCase()) {
            case "binary_search" -> "O(log N)";
            case "linear_search" -> "O(N)";
            case "bfs", "dfs" -> "O(V + E)";
            case "quicksort", "merge_sort" -> "O(N log N)";
            case "kruskal_mst" -> "O(E log E)";
            case "dijkstra", "prim_mst" -> "O(E log V)";
            case "knapsack_dp" -> "O(N * W)";
            case "selection_sort", "insertion_sort" -> "O(N^2)";
            default -> "O(N)";
        };
    }
}
