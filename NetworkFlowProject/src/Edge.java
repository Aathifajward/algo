/**
 * @author Aathif Ajward
 * @id w2052865/20230911
 *
 * Represents an edge in the flow network with capacity and flow properties.
 * Used to model connections between nodes in the network.
 *
 * An edge contains information about its source node (from), destination node (to),
 * its capacity (maximum flow possible), and the current flow through the edge.
 * This implementation supports the Ford-Fulkerson algorithm by tracking flow
 * and calculating residual capacity.
 */
public class Edge {
    /** Source node of the edge */
    int from;

    /** Destination node of the edge */
    int to;

    /** Maximum flow capacity of this edge */
    int capacity;

    /** Current flow through this edge */
    int flow;

    /** Reference to the reverse edge for efficient updates */
    Edge reverse;

    /**
     * Creates a new edge with the specified source, destination, and capacity.
     * Initial flow is set to zero.
     *
     * @param from     The source node index
     * @param to       The destination node index
     * @param capacity The maximum flow capacity of this edge
     */
    public Edge(int from, int to, int capacity) {
        this.from = from;
        this.to = to;
        this.capacity = capacity;
        this.flow = 0;
    }

    /**
     * Calculates the remaining capacity available for flow in this edge.
     *
     * @return The residual capacity (capacity - current flow)
     */
    public int residualCapacity() {
        return capacity - flow;
    }
}