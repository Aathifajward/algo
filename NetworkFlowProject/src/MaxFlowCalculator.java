import java.util.*;

public class MaxFlowCalculator {
    public static int fordFulkerson(FlowNetwork network, int source, int sink) {
        int maxFlow = 0;
        while (true) {
            Edge[] parent = new Edge[network.n];
            boolean foundAugmentingPath = bfs(network, source, sink, parent);

            if (!foundAugmentingPath) {
                break;
            }

            // Find minimum residual capacity
            int pathFlow = Integer.MAX_VALUE;
            for (int v = sink; v != source; ) {
                Edge e = parent[v];
                pathFlow = Math.min(pathFlow, e.residualCapacity());
                v = e.from;
            }

            // Update flow along the path
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

            maxFlow += pathFlow;
            System.out.println("Augmented Path Flow: " + pathFlow + ", Current Max Flow: " + maxFlow);
        }
        return maxFlow;
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
