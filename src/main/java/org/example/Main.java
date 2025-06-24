package org.example;

import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java -jar target/TestForUnoSoftCom-1.0-SNAPSHOT.jar <path_to_input_file>");
            return;
        }

        String inputFile = args[0];
        String outputFile = "output.txt";

        long start = System.nanoTime();

        Solution solution = new Solution();
        Set<List<String>> filteredLines = solution.readAndFilterLines(inputFile);
        List<Set<List<String>>> groups = solution.groupLines(filteredLines);
        solution.writeGroupsToFile(groups, outputFile);

        long end = System.nanoTime();
        System.out.println("Время выполнения: " + (end - start) / 1_000_000 + " ms");
    }
}