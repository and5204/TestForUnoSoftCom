package org.example;

import java.util.List;
import java.util.Set;

public interface SolutionInterface {

    List<String> readAndFilterLines(String filePath);

    List<List<String>> groupLines(List<String> lines);



    void writeGroupsToFile(List<List<String>> groups, String outputPath);


    boolean isLineCorrect(String line);
}