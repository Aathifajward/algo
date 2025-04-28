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
                return;
            }

            // Get list of text files sorted using natural order
            List<Path> textFiles = Files.list(resourceDir)
                    .filter(path -> path.toString().endsWith(".txt"))
                    .sorted(naturalOrderComparator)
                    .collect(Collectors.toList());

            // Display available files
            System.out.println("Available network files:");
            for (int i = 0; i < textFiles.size(); i++) {
                System.out.println((i + 1) + ". " + textFiles.get(i).getFileName());
            }

            // Get user selection
            System.out.print("Enter the number of the file you want to use: ");
            int fileChoice = scanner.nextInt();

            // Validate input
            if (fileChoice < 1 || fileChoice > textFiles.size()) {
                System.out.println("Invalid file selection.");
                return;
            }

            // Selected file path
            String filename = textFiles.get(fileChoice - 1).toString();

            // Performance tracking
            long startTime = System.currentTimeMillis();

            // Parse and calculate max flow
            FlowNetwork network = InputParser.parse(filename);
            int maxFlow = MaxFlowCalculator.fordFulkerson(network, 0, network.n - 1);


            // Output results
            System.out.println("\nInput file: " + filename);
            System.out.println("Number of nodes: " + network.n);
            System.out.println("Maximum flow: " + maxFlow);

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
        } finally {
            scanner.close();
        }
    }
}