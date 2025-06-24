package org.example;

import java.util.List;
import java.util.Set;

public interface SolutionInterface {

    Set<List<String>> readAndFilterLines(String filePath);

    List<Set<List<String>>> groupLines(Set<List<String>> lines);

    void writeGroupsToFile(List<Set<List<String>>> groups, String outputPath);


    boolean isLineCorrect(String line);
}