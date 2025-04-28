import java.io.*;
import java.util.*;

public class InputParser {
    public static FlowNetwork parse(String filename) throws IOException {
        // Use the full file path directly
        BufferedReader br = new BufferedReader(new FileReader(filename));

        // Debug: Print full path of the file being read
        System.out.println("Reading network file from: " + filename);

        int n = Integer.parseInt(br.readLine().trim());
        FlowNetwork network = new FlowNetwork(n);

        String line;
        while ((line = br.readLine()) != null) {
            // Skip empty lines
            if (line.trim().isEmpty()) continue;

            String[] parts = line.trim().split("\\s+");
            if (parts.length == 3) {
                int from = Integer.parseInt(parts[0]);
                int to = Integer.parseInt(parts[1]);
                int capacity = Integer.parseInt(parts[2]);
                network.addEdge(from, to, capacity);
            }
        }

        br.close();
        return network;
    }
}