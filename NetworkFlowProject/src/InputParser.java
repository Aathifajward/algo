import java.io.*;
import java.util.*;

public class InputParser {
    public static FlowNetwork parse(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        int n = Integer.parseInt(br.readLine().trim());
        FlowNetwork network = new FlowNetwork(n);

        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.trim().split("\\s+");
            int from = Integer.parseInt(parts[0]);
            int to = Integer.parseInt(parts[1]);
            int capacity = Integer.parseInt(parts[2]);
            network.addEdge(from, to, capacity);
        }

        br.close();
        return network;
    }
}

