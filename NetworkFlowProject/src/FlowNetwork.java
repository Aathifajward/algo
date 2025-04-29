/**
 * @author Aathif Ajward
 * @id w2052865/20230911
 *
 * Represents a flow network using adjacency lists.
 * Contains methods for adding edges and retrieving edges from specific nodes.
 *
 * The flow network is represented as a directed graph with capacities on the edges.
 * For each edge, a reverse edge with zero capacity is also added to support the
 * residual graph needed for the Ford-Fulkerson algorithm. The network assumes
 * that node 0 is the source and node (n-1) is the sink.
 *
 * The adjacency list representation is chosen for its efficiency in sparse networks
 * where most nodes are not directly connected to each other, which is common in
 * flow network problems.
 */
import java.util.*;

public class FlowNetwork {
    /** Number of nodes in the network */
    int n;

    /** Adjacency list representation of the network.
     * For each node index i, adjList[i] contains all edges originating from node i.
     */
    List<Edge>[] adjList;

    /** Map for quick edge lookup by (from, to) pair */
    Map<Long, Edge> edgeMap;

    /**
     * Creates a new flow network with the specified number of nodes.
     * Initializes empty adjacency lists for each node.
     *
     * @param n The number of nodes in the network
     */
    public FlowNetwork(int n) {
        this.n = n;
        adjList = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            adjList[i] = new ArrayList<>();
        }
        edgeMap = new HashMap<>();
    }

    /**
     * Adds a new edge to the flow network.
     * Also adds a reverse edge with zero capacity to support the residual graph
     * needed for the Ford-Fulkerson algorithm.
     *
     * @param from     The source node index
     * @param to       The destination node index
     * @param capacity The maximum flow capacity of this edge
     */
    public void addEdge(int from, int to, int capacity) {
        Edge forward = new Edge(from, to, capacity);
        Edge backward = new Edge(to, from, 0); // reverse edge for residual graph

        // Set reverse edge references for quick updates
        forward.reverse = backward;
        backward.reverse = forward;

        // Add edges to adjacency lists
        adjList[from].add(forward);
        adjList[to].add(backward);

        // Store in edge map for quick lookup
        long forwardKey = ((long)from << 32) | to;
        long backwardKey = ((long)to << 32) | from;
        edgeMap.put(forwardKey, forward);
        edgeMap.put(backwardKey, backward);
    }

    /**
     * Retrieves all edges originating from the specified node.
     *
     * @param node The node index to get edges from
     * @return A list of all edges originating from the specified node
     */
    public List<Edge> getEdgesFrom(int node) {
        return adjList[node];
    }

    /**
     * Gets an edge between two nodes.
     *
     * @param from The source node
     * @param to The destination node
     * @return The edge from source to destination, or null if none exists
     */
    public Edge getEdge(int from, int to) {
        long key = ((long)from << 32) | to;
        return edgeMap.get(key);
    }
}