/**
 * @author Aathif Ajward
 * @id w2052865/20230911
 *
 * Main class for the Network Flow project.
 * Handles file selection, parsing, and running the maximum flow algorithm.
 * Includes performance measurement for algorithm analysis.
 *
 * This class provides a command-line interface for:
 * 1. Selecting a network file from the resources directory
 * 2. Parsing the selected file into a flow network
 * 3. Running the Ford-Fulkerson algorithm to find the maximum flow
 * 4. Displaying the results and performance metrics
 *
 * The class also includes utility methods for counting edges and
 * measuring execution time to support the performance analysis.
 */
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    // Custom comparator for natural sorting of filenames with numbers
    private static Comparator<Path> naturalOrderComparator = (path1, path2) -> {
        String name1 = path1.getFileName().toString();
        String name2 = path2.getFileName().toString();

        // Extract prefix and numeric part
        Pattern pattern = Pattern.compile("(.*?)([0-9]+)\\.txt$");
        Matcher matcher1 = pattern.matcher(name1);
        Matcher matcher2 = pattern.matcher(name2);

        if (matcher1.matches() && matcher2.matches()) {
            String prefix1 = matcher1.group(1);
            String prefix2 = matcher2.group(1);

            // Compare prefixes first
            int prefixCompare = prefix1.compareTo(prefix2);
            if (prefixCompare != 0) {
                return prefixCompare;
            }

            // If prefixes are the same, compare numeric parts
            int num1 = Integer.parseInt(matcher1.group(2));
            int num2 = Integer.parseInt(matcher2.group(2));
            return Integer.compare(num1, num2);
        }

        // Fallback to default string comparison
        return name1.compareTo(name2);
    };

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            // List resource files
            Path resourceDir = Paths.get("src/resources");
            if (!Files.exists(resourceDir)) {
                System.out.println("Resources directory not found!");
                System.out.println("Creating resources directory...");
                try {
                    Files.createDirectories(resourceDir);
                    System.out.println("Resources directory created. Please add network files to: " + resourceDir.toAbsolutePath());
                    return;
                } catch (IOException e) {
                    System.out.println("Failed to create resources directory: " + e.getMessage());
                    return;
                }
            }

            // Get list of text files sorted using natural order
            List<Path> textFiles = Files.list(resourceDir)
                    .filter(path -> path.toString().endsWith(".txt"))
                    .sorted(naturalOrderComparator)
                    .collect(Collectors.toList());

            if (textFiles.isEmpty()) {
                System.out.println("No network files found in resources directory!");
                System.out.println("Please add network files to: " + resourceDir.toAbsolutePath());
                return;
            }

            // Display available files
            System.out.println("Available network files:");
            for (int i = 0; i < textFiles.size(); i++) {
                System.out.println((i + 1) + ". " + textFiles.get(i).getFileName());
            }

            // Get user selection
            System.out.print("Enter the number of the file you want to use (1-" + textFiles.size() + "): ");
            int fileChoice = scanner.nextInt();

            // Validate input
            if (fileChoice < 1 || fileChoice > textFiles.size()) {
                System.out.println("Invalid file selection. Please enter a number between 1 and " + textFiles.size() + ".");
                return;
            }

            // Selected file path
            String filename = textFiles.get(fileChoice - 1).toString();

            // Parse the network (measure parsing time separately)
            long parseStartTime = System.currentTimeMillis();
            FlowNetwork network = InputParser.parse(filename);
            long parseEndTime = System.currentTimeMillis();
            long parseTime = parseEndTime - parseStartTime;

            System.out.println("\nFile parsed in " + parseTime + " ms");
            System.out.println("Running Ford-Fulkerson algorithm...");

            // Set performance mode based on network size
            int totalEdges = countEdges(network);
            boolean verboseMode = totalEdges < 4000; // Only use for small networks
            MaxFlowCalculator.setVerboseOutput(verboseMode);

            // Performance tracking for just the algorithm
            long startTime = System.currentTimeMillis();

            // Calculate max flow
            int maxFlow = MaxFlowCalculator.fordFulkerson(network, 0, network.n - 1, verboseMode);

            // Calculate execution time
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            // Output results with performance metrics
            System.out.println("Input file: " + textFiles.get(fileChoice - 1).getFileName());
            System.out.println("Number of nodes: " + network.n);
            System.out.println("Number of edges: " + totalEdges);
            System.out.println("Maximum flow: " + maxFlow);
            System.out.println("Algorithm execution time: " + executionTime + " ms");
            System.out.println("Total execution time (parsing + algorithm): " + (parseTime + executionTime) + " ms");

            // Display network complexity metrics
            System.out.println("\nNetwork complexity:");
            System.out.println("- Total nodes: " + network.n);
            System.out.println("- Total edges: " + totalEdges);
            System.out.println("- Average edges per node: " + String.format("%.2f", (double)totalEdges / network.n));
            System.out.println("where V is the number of vertices and E is the number of edges.");
            // Run benchmark if requested
            System.out.print("\nWould you like to run a benchmark test for performance analysis? (y/n): ");
            String benchmarkChoice = scanner.next();

            if (benchmarkChoice.equalsIgnoreCase("y")) {
                runBenchmark(network, 10); // Run 10 iterations for benchmark
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
        } finally {
            scanner.close();
        }
    }

    /**
     * Counts the total number of forward edges in the network.
     * This excludes the reverse edges added for the residual graph.
     */
    private static int countEdges(FlowNetwork network) {
        int count = 0;
        for (int i = 0; i < network.n; i++) {
            for (Edge e : network.getEdgesFrom(i)) {
                if (e.capacity > 0) {  // Only count forward edges, not residual edges
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Runs a benchmark test on the algorithm to measure performance.
     *
     * @param network The network to run the benchmark on
     * @param iterations Number of iterations to run
     */
    private static void runBenchmark(FlowNetwork network, int iterations) {
        System.out.println("\nRunning benchmark test (" + iterations + " iterations)...");

        // Disable verbose output for benchmarking
        MaxFlowCalculator.setVerboseOutput(false);

        long[] times = new long[iterations];
        long totalTime = 0;

        for (int i = 0; i < iterations; i++) {
            // Reset the network (clear all flows)
            resetNetwork(network);

            // Measure execution time
            long startTime = System.currentTimeMillis();
            MaxFlowCalculator.fordFulkerson(network, 0, network.n - 1, false);
            long endTime = System.currentTimeMillis();

            times[i] = endTime - startTime;
            totalTime += times[i];

            System.out.println("Iteration " + (i+1) + ": " + times[i] + " ms");
        }

        // Calculate statistics
        double averageTime = (double) totalTime / iterations;

        // Calculate standard deviation
        double variance = 0;
        for (long time : times) {
            variance += Math.pow(time - averageTime, 2);
        }
        variance /= iterations;
        double stdDev = Math.sqrt(variance);

        // Find min and max times
        long minTime = times[0];
        long maxTime = times[0];
        for (int i = 1; i < iterations; i++) {
            if (times[i] < minTime) minTime = times[i];
            if (times[i] > maxTime) maxTime = times[i];
        }

        // Print benchmark results
        System.out.println("\nBenchmark Results:");
        System.out.println("- Average execution time: " + String.format("%.2f", averageTime) + " ms");
        System.out.println("- Standard deviation: " + String.format("%.2f", stdDev) + " ms");
        System.out.println("- Minimum time: " + minTime + " ms");
        System.out.println("- Maximum time: " + maxTime + " ms");
    }

    /**
     * Resets all flows in the network to zero for re-running the algorithm.
     *
     * @param network The network to reset
     */
    private static void resetNetwork(FlowNetwork network) {
        for (int i = 0; i < network.n; i++) {
            for (Edge e : network.getEdgesFrom(i)) {
                e.flow = 0;
            }
        }
    }
}