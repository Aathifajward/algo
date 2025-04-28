
import java.util.*;

public class FlowNetwork {
    int n; // number of nodes
    List<Edge>[] adjList;

    public FlowNetwork(int n) {
        this.n = n;
        adjList = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            adjList[i] = new ArrayList<>();
        }
    }

    public void addEdge(int from, int to, int capacity) {
        Edge e = new Edge(from, to, capacity);
        Edge reverse = new Edge(to, from, 0); // reverse edge for residual graph
        adjList[from].add(e);
        adjList[to].add(reverse);
    }

    public List<Edge> getEdgesFrom(int node) {
        return adjList[node];
    }
}

