/**
 * @author Aathif Ajward
 * @id w2052865/20230911
 *
 * Implementation of the Ford-Fulkerson algorithm for maximum flow.
 * Uses Breadth-First Search to find augmenting paths in the network
 * (also known as the Edmonds-Karp algorithm).
 *
 * The algorithm works by repeatedly finding augmenting paths from source to sink
 * in the residual network and pushing the maximum possible flow through these paths.
 * The process continues until no more augmenting paths can be found.
 *
 * Using BFS ensures that the shortest paths (in terms of number of edges) are found first,
 * which improves the algorithm's efficiency to O(V*E²) where V is the number of vertices
 * and E is the number of edges.
 *
 * The implementation provides detailed output of each step in the algorithm, showing:
 * - The augmenting path found in each iteration
 * - The bottleneck capacity of the path
 * - The updated flow values after each iteration
 * - The final maximum flow value
 */
import java.util.*;

public class MaxFlowCalculator {
    // Static reusable data structures to avoid repeated allocations
    private static List<Integer> reusablePath = new ArrayList<>();
    private static Queue<Integer> reusableQueue = new LinkedList<>();
    private static boolean[] reusableVisited;
    private static StringBuilder reusableStringBuilder = new StringBuilder(128);

    // Flag to control verbose output for performance testing
    private static boolean VERBOSE_OUTPUT = true;

    /**
     * Implements the Ford-Fulkerson algorithm with BFS path finding (Edmonds-Karp)
     * to calculate the maximum flow in a network.
     *
     * @param network The flow network to calculate maximum flow for
     * @param source  The source node index (typically 0)
     * @param sink    The sink node index (typically network.n-1)
     * @return The maximum flow value
     */
    public static int fordFulkerson(FlowNetwork network, int source, int sink) {
        return fordFulkerson(network, source, sink, true);
    }

    /**
     * Implements the Ford-Fulkerson algorithm with BFS path finding (Edmonds-Karp)
     * to calculate the maximum flow in a network, with optional verbose output.
     *
     * @param network The flow network to calculate maximum flow for
     * @param source  The source node index (typically 0)
     * @param sink    The sink node index (typically network.n-1)
     * @param verbose Whether to print detailed output
     * @return The maximum flow value
     */
    public static int fordFulkerson(FlowNetwork network, int source, int sink, boolean verbose) {
        int maxFlow = 0;
        int iteration = 0;
        VERBOSE_OUTPUT = verbose;

        if (VERBOSE_OUTPUT) {
            System.out.println("Network Flow Calculation");
            System.out.println("=======================");
        }

        // Main loop: continue until no more augmenting paths can be found
        while (true) {
            iteration++;

            // The parent array will store the edges of the augmenting path
            Edge[] parent = new Edge[network.n];

            // Try to find an augmenting path using BFS
            boolean foundAugmentingPath = bfs(network, source, sink, parent);

            // If no augmenting path exists, we've found the maximum flow
            if (!foundAugmentingPath) {
                break;
            }

            // Find the path and calculate its flow
            List<Integer> path = reconstructPath(parent, source, sink);
            int pathFlow = calculatePathFlow(parent, source, sink);

            // Update the flow values in the network (optimized with direct reverse edge references)
            updateNetworkFlow(parent, source, sink, pathFlow);

            // Update the total maximum flow
            maxFlow += pathFlow;

            // Print details about the current iteration if verbose output is enabled
            if (VERBOSE_OUTPUT) {
                System.out.println("\n* Iteration " + iteration + ":");
                System.out.println("  Found path: " + formatPath(path));
                System.out.println("  Bottleneck capacity: " + pathFlow);
                System.out.println("  Path flow: " + pathFlow);
                System.out.println("  Current maximum flow: " + maxFlow);

                // Print the updated edge flows after this iteration
                printEdgeFlows(network, iteration);
            }
        }

        // Print a final summary of the results if verbose output is enabled
        if (VERBOSE_OUTPUT) {
            printFinalSummary(maxFlow, iteration);
        }

        return maxFlow;
    }

    /**
     * Reconstructs the augmenting path from source to sink using the parent edge array.
     * Uses a reusable list to avoid repeated memory allocations.
     *
     * @param parent The array of parent edges that forms the path
     * @param source The source node index
     * @param sink   The sink node index
     * @return A list of node indices representing the path from source to sink
     */
    private static List<Integer> reconstructPath(Edge[] parent, int source, int sink) {
        reusablePath.clear(); // Reuse existing list instead of creating a new one

        // Start from the sink and trace back to the source
        for (int v = sink; v != source; ) {
            reusablePath.add(v);
            Edge e = parent[v];
            v = e.from;
        }
        reusablePath.add(source);

        // Reverse to get path from source to sink
        Collections.reverse(reusablePath);
        return reusablePath;
    }

    /**
     * Formats a path list into a readable string representation.
     * Uses a StringBuilder for better performance.
     *
     * @param path The list of node indices representing a path
     * @return A string representation of the path
     */
    private static String formatPath(List<Integer> path) {
        if (path.isEmpty()) return "";

        reusableStringBuilder.setLength(0); // Clear the StringBuilder

        // Append the first node
        reusableStringBuilder.append(path.get(0));

        // Append remaining nodes with separator
        for (int i = 1; i < path.size(); i++) {
            reusableStringBuilder.append(" -> ").append(path.get(i));
        }

        return reusableStringBuilder.toString();
    }

    /**
     * Calculates the maximum flow that can be pushed through the given path.
     * This is determined by the minimum residual capacity of any edge in the path.
     *
     * @param parent The array of parent edges that forms the path
     * @param source The source node index
     * @param sink   The sink node index
     * @return The maximum flow that can be pushed through the path
     */
    private static int calculatePathFlow(Edge[] parent, int source, int sink) {
        int pathFlow = Integer.MAX_VALUE;

        // Start from the sink and find the minimum residual capacity
        for (int v = sink; v != source; ) {
            Edge e = parent[v];
            // Calculate residual capacity directly rather than calling method
            int residual = e.capacity - e.flow;
            pathFlow = Math.min(pathFlow, residual);
            v = e.from;
        }

        return pathFlow;
    }

    /**
     * Updates the flow values in the network after finding an augmenting path.
     * Uses direct references to reverse edges for efficient updates.
     *
     * @param parent   The array of parent edges that forms the path
     * @param source   The source node index
     * @param sink     The sink node index
     * @param pathFlow The amount of flow to push through the path
     */
    private static void updateNetworkFlow(Edge[] parent, int source, int sink, int pathFlow) {
        // Start from the sink and update flows back to the source
        for (int v = sink; v != source; ) {
            Edge e = parent[v];
            // Increase flow on the forward edge
            e.flow += pathFlow;

            // Decrease flow on the reverse edge using direct reference
            e.reverse.flow -= pathFlow;

            v = e.from;
        }
    }

    /**
     * Prints the current flow values for all edges in the network.
     * Only edges with positive flow are displayed.
     *
     * @param network   The flow network
     * @param iteration The current iteration number
     */
    private static void printEdgeFlows(FlowNetwork network, int iteration) {
        System.out.println("  Edge Flows after Iteration " + iteration + ":");
        for (int i = 0; i < network.n; i++) {
            for (Edge edge : network.getEdgesFrom(i)) {
                if (edge.flow > 0) {
                    System.out.printf("  Edge %d->%d: Flow = %d (Capacity = %d)%n",
                            edge.from, edge.to, edge.flow, edge.capacity);
                }
            }
        }
    }

    /**
     * Prints a summary of the maximum flow calculation.
     *
     * @param maxFlow    The calculated maximum flow
     * @param iterations The number of iterations performed
     */
    private static void printFinalSummary(int maxFlow, int iterations) {
        System.out.println("\nFinal Network Flow Analysis");
        System.out.println("==========================");
    }

    /**
     * Implements a Breadth-First Search to find an augmenting path from source to sink.
     * Only considers edges with positive residual capacity.
     * Uses reusable data structures to improve performance.
     *
     * @param network The flow network
     * @param source  The source node index
     * @param sink    The sink node index
     * @param parent  An array to store the edges that form the augmenting path
     * @return true if an augmenting path was found, false otherwise
     */
    private static boolean bfs(FlowNetwork network, int source, int sink, Edge[] parent) {
        // Initialize or resize the visited array if needed
        if (reusableVisited == null || reusableVisited.length < network.n) {
            reusableVisited = new boolean[network.n];
        } else {
            Arrays.fill(reusableVisited, false);
        }

        // Clear the queue and add the source
        reusableQueue.clear();
        reusableQueue.add(source);
        reusableVisited[source] = true;

        // Standard BFS loop
        while (!reusableQueue.isEmpty()) {
            int u = reusableQueue.poll();

            // Early termination if we reached the sink
            if (u == sink) {
                return true;
            }

            // Explore all edges from the current node
            for (Edge e : network.getEdgesFrom(u)) {
                // Only consider edges with remaining capacity
                if (!reusableVisited[e.to] && e.residualCapacity() > 0) {
                    // Record this edge in the parent array
                    parent[e.to] = e;
                    reusableVisited[e.to] = true;
                    reusableQueue.add(e.to);
                }
            }
        }

        // Return whether we reached the sink
        return reusableVisited[sink];
    }

    /**
     * Sets the verbosity level for output.
     *
     * @param verbose Whether to print detailed output
     */
    public static void setVerboseOutput(boolean verbose) {
        VERBOSE_OUTPUT = verbose;
    }
}