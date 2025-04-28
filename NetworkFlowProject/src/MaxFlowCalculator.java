import java.util.*;

public class MaxFlowCalculator {
    public static int fordFulkerson(FlowNetwork network, int source, int sink) {
        int maxFlow = 0;
        int iteration = 0;

        System.out.println("Network Flow Calculation");
        System.out.println("=======================");

        while (true) {
            iteration++;
            Edge[] parent = new Edge[network.n];
            boolean foundAugmentingPath = bfs(network, source, sink, parent);

            if (!foundAugmentingPath) {
                break;
            }

            // Find path and path flow
            List<Integer> path = reconstructPath(parent, source, sink);
            int pathFlow = calculatePathFlow(parent, source, sink);

            // Print iteration details
            System.out.println("\n* Iteration " + iteration + ":");
            System.out.println("  Found path: " + formatPath(path));
            System.out.println("  Bottleneck capacity: " + pathFlow);

            // Update network flow
            updateNetworkFlow(network, parent, source, sink, pathFlow);

            // Update max flow
            maxFlow += pathFlow;
            System.out.println("  Path flow: " + pathFlow);
            System.out.println("  Current maximum flow: " + maxFlow);

            // Print updated edge flows
            printEdgeFlows(network, iteration);
        }

        // Final summary
        printFinalSummary(maxFlow, iteration);

        return maxFlow;
    }

    private static List<Integer> reconstructPath(Edge[] parent, int source, int sink) {
        List<Integer> path = new ArrayList<>();
        for (int v = sink; v != source; ) {
            path.add(v);
            Edge e = parent[v];
            v = e.from;
        }
        path.add(source);
        Collections.reverse(path);
        return path;
    }

    private static String formatPath(List<Integer> path) {
        return path.toString().replace("[", "").replace("]", "").replace(",", " -> ");
    }

    private static int calculatePathFlow(Edge[] parent, int source, int sink) {
        int pathFlow = Integer.MAX_VALUE;
        for (int v = sink; v != source; ) {
            Edge e = parent[v];
            pathFlow = Math.min(pathFlow, e.residualCapacity());
            v = e.from;
        }
        return pathFlow;
    }

    private static void updateNetworkFlow(FlowNetwork network, Edge[] parent, int source, int sink, int pathFlow) {
        for (int v = sink; v != source; ) {
            Edge e = parent[v];
            e.flow += pathFlow;
            for (Edge rev : network.getEdgesFrom(e.to)) {
                if (rev.to == e.from) {
                    rev.flow -= pathFlow;
                }
            }
            v = e.from;
        }
    }

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

    private static void printFinalSummary(int maxFlow, int iterations) {
        System.out.println("\nFinal Network Flow Analysis");
        System.out.println("==========================");
    }

    private static boolean bfs(FlowNetwork network, int source, int sink, Edge[] parent) {
        boolean[] visited = new boolean[network.n];
        Queue<Integer> queue = new LinkedList<>();
        queue.add(source);
        visited[source] = true;

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (Edge e : network.getEdgesFrom(u)) {
                if (!visited[e.to] && e.residualCapacity() > 0) {
                    parent[e.to] = e;
                    visited[e.to] = true;
                    queue.add(e.to);
                }
            }
        }
        return visited[sink];
    }
}