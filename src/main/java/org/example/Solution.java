package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class Solution implements SolutionInterface{
    private final Map<String, List<Integer>> positionValueMap = new HashMap<>();

    @Override
    public Set<List<String>> readAndFilterLines(String filePath) {
        Set<List<String>> lines = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))){
            for(String line; (line = br.readLine())!= null;){
                List<String> columns = Arrays.asList(line.split(";"));
                int count = 0;
                for (String column : columns) {
                    if (!isLineCorrect(column)) count++;
                }
                if(count == 0){
                    lines.add(columns);
                }


            }
        }catch (Exception e) {
            System.out.println("open file error");
        }
        return lines;
    }

    @Override
    public List<Set<List<String>>> groupLines(Set<List<String>> lines) {
        // Все строки (после фильтрации)
        List<List<String>> lineList = new ArrayList<>(lines);
        int n = lineList.size();
        UnionFind uf = new UnionFind(n);


        for (int i = 0; i < n; i++) {
            List<String> cols = lineList.get(i);
            for (int pos = 0; pos < cols.size(); pos++) {
                String value = cols.get(pos).trim();


                if (!value.isEmpty() && !(value.equals("\"\"")) ) {
                    String key = pos + ":" + value;
                    positionValueMap.computeIfAbsent(key, k -> new ArrayList<>()).add(i);
                }
            }
        }


        for (List<Integer> ids : positionValueMap.values()) {
            for (int i = 1; i < ids.size(); i++) {
                uf.union(ids.get(0), ids.get(i));
            }
        }


        Map<Integer, Set<List<String>>> groups = new HashMap<>();
        for (int i = 0; i < n; i++) {
            int root = uf.find(i);
            groups.computeIfAbsent(root, k -> new HashSet<>()).add(lineList.get(i));
        }

        return new ArrayList<>(groups.values());
    }

    @Override
    public void writeGroupsToFile(List<Set<List<String>>> groups, String outputPath) {

        groups.sort((a, b) -> Integer.compare(b.size(), a.size()));

        int nonSingleGroups = 0;
        for (Set<List<String>> group : groups) {
            if (group.size() > 1) nonSingleGroups++;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(new File(outputPath).toPath(), StandardCharsets.UTF_8)) {
            writer.write("Количество групп с более чем одним элементом: " + nonSingleGroups + "\n");

            int groupNumber = 1;
            for (Set<List<String>> group : groups) {
                if (group.size() > 1) {
                    writer.write("Группа " + groupNumber++ + "\n");
                    for (List<String> row : group) {
                        writer.write(String.join(";", row) + "\n");
                    }
                    writer.write("\n");
                }
            }

        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
        }
    }

    @Override
    public boolean isLineCorrect(String line) {
        int quoteCount = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '"') quoteCount++;
        }
        if(quoteCount == 2){
            return line.startsWith("\"") && line.endsWith("\"");
        }
        return quoteCount == 0;
    }
}
