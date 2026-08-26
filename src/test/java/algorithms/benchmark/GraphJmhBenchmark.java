package algorithms.benchmark;

import algorithms.GraphAlgorithms;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import structures.Graph;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * OpenJDK JMH (Java Microbenchmark Harness) Benchmark Suite for Graph Algorithms.
 *
 * Measures scientific Average Time (microseconds/operation) with JIT warmup,
 * fork isolation, dead-code elimination (Blackhole), and confidence error intervals
 * across scaling graph sizes (N = 10, 50, 100, 250, 500, 1000).
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 2, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 3, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class GraphJmhBenchmark {

    @Param({"10", "50", "100", "250", "500", "1000"})
    public int vertexCount;

    private Graph<Integer> graph;
    private int destination;

    @Setup(Level.Trial)
    public void setUp() {
        this.graph = generateCorridorTopology(vertexCount, 42L + vertexCount);
        this.destination = vertexCount - 1;
    }

    @Benchmark
    public void dijkstraShortestPath(Blackhole bh) {
        bh.consume(GraphAlgorithms.dijkstra(graph, 0, destination));
    }

    @Benchmark
    public void bfsReachability(Blackhole bh) {
        bh.consume(GraphAlgorithms.bfs(graph, 0));
    }

    @Benchmark
    public void dfsTraversal(Blackhole bh) {
        bh.consume(GraphAlgorithms.dfs(graph, 0));
    }

    @Benchmark
    public void primMST(Blackhole bh) {
        bh.consume(GraphAlgorithms.primMST(graph));
    }

    @Benchmark
    public void kruskalMST(Blackhole bh) {
        bh.consume(GraphAlgorithms.kruskalMST(graph));
    }

    private static Graph<Integer> generateCorridorTopology(int numVertices, long seed) {
        Graph<Integer> graph = new Graph<>(false);
        Random rand = new Random(seed);

        for (int i = 0; i < numVertices; i++) {
            graph.addVertex(i);
        }

        for (int i = 0; i < numVertices - 1; i++) {
            double distance = 5.0 + rand.nextDouble() * 20.0;
            double friction = 0.9 + rand.nextDouble() * 0.6;
            double weight = Math.round(distance * friction * 10.0) / 10.0;
            graph.addEdge(i, i + 1, weight);
        }

        int crossAisles = numVertices * 3 / 2;
        for (int k = 0; k < crossAisles; k++) {
            int u = rand.nextInt(numVertices);
            int v = rand.nextInt(numVertices);
            if (u != v) {
                double distance = 10.0 + rand.nextDouble() * 35.0;
                double friction = 1.0 + rand.nextDouble() * 1.0;
                double weight = Math.round(distance * friction * 10.0) / 10.0;
                graph.addEdge(u, v, weight);
            }
        }

        return graph;
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(GraphJmhBenchmark.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
