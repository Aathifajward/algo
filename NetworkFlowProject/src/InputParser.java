/**
 * @author Aathif Ajward
 * @id w2052865/20230911
 *
 * Parser for reading network flow problems from input files.
 * Handles the file format specified in the coursework description.
 *
 * The expected file format is:
 * - First line: number of nodes n (nodes are numbered 0 to n-1)
 * - Each subsequent line: three integers "a b c" representing an edge
 *   from node a to node b with capacity c
 *
 * The parser assumes node 0 is the source and node (n-1) is the sink.
 * Empty lines in the file are skipped.
 */
import java.io.*;
import java.util.*;

public class InputParser {
    /**
     * Parses a network flow problem from a text file.
     *
     * @param filename The path to the file containing the network description
     * @return A FlowNetwork object representing the parsed network
     * @throws IOException If an error occurs while reading the file
     * @throws NumberFormatException If the file contains invalid number formats
     * @throws IllegalArgumentException If the file format is invalid
     */
    public static FlowNetwork parse(String filename) throws IOException {
        // Use a buffered reader for efficient file reading
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            // Debug: Print full path of the file being read
            System.out.println("Reading network file from: " + filename);

            // Read the number of nodes
            String firstLine = br.readLine();
            if (firstLine == null) {
                throw new IllegalArgumentException("Empty file or unable to read first line");
            }

            int n;
            try {
                n = Integer.parseInt(firstLine.trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("First line must contain the number of nodes: " + e.getMessage());
            }

            if (n <= 1) {
                throw new IllegalArgumentException("Network must have at least 2 nodes (source and sink)");
            }

            // Create the network
            FlowNetwork network = new FlowNetwork(n);

            // Use a reusable string tokenizer to avoid string splitting overhead
            StringTokenizer tokenizer;

            // Parse each edge line
            String line;
            int lineNumber = 1;
            while ((line = br.readLine()) != null) {
                lineNumber++;

                // Skip empty lines
                if (line.trim().isEmpty()) continue;

                try {
                    tokenizer = new StringTokenizer(line.trim());

                    if (tokenizer.countTokens() != 3) {
                        throw new IllegalArgumentException("Line " + lineNumber +
                                ": Expected 3 values (from, to, capacity), found " + tokenizer.countTokens());
                    }

                    int from = Integer.parseInt(tokenizer.nextToken());
                    int to = Integer.parseInt(tokenizer.nextToken());
                    int capacity = Integer.parseInt(tokenizer.nextToken());

                    // Validate node indices
                    if (from < 0 || from >= n) {
                        throw new IllegalArgumentException("Line " + lineNumber +
                                ": Source node index " + from + " out of range [0," + (n-1) + "]");
                    }

                    if (to < 0 || to >= n) {
                        throw new IllegalArgumentException("Line " + lineNumber +
                                ": Destination node index " + to + " out of range [0," + (n-1) + "]");
                    }

                    if (capacity < 0) {
                        throw new IllegalArgumentException("Line " + lineNumber +
                                ": Negative capacity " + capacity + " not allowed");
                    }

                    network.addEdge(from, to, capacity);
                } catch (NoSuchElementException | NumberFormatException e) {
                    throw new IllegalArgumentException("Line " + lineNumber +
                            ": Invalid format: " + e.getMessage());
                }
            }

            return network;
        }
    }
}