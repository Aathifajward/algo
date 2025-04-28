import java.io.*;

public class Main {
    public static void main(String[] args) {
        try {
            FlowNetwork network = InputParser.parse("network.txt"); // example input file
            int maxFlow = MaxFlowCalculator.fordFulkerson(network, 0, network.n - 1);
            System.out.println("Maximum flow: " + maxFlow);
        } catch (IOException e) {
            System.out.println("Error reading input file: " + e.getMessage());
        }
    }
}
