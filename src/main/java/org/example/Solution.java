package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class Solution implements SolutionInterface{


    @Override

    public List<String> readAndFilterLines(String filePath) {
        Set<String> uniqueLines = new HashSet<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(";");
                boolean valid = true;

                for (String column : columns) {
                    if (!isLineCorrect(column)) {
                        valid = false;
                        break;
                    }
                }

                if (valid) {
                    uniqueLines.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        return new ArrayList<>(uniqueLines);
    }





    @Override
    public List<List<String>> groupLines(List<String> lines) {
        Map<String, Integer> positionValueToIndex = new HashMap<>(lines.size() * 2);
        UnionFind uf = new UnionFind(lines.size());
        StringBuilder keyBuilder = new StringBuilder();

        for (int index = 0; index < lines.size(); index++) {
            String[] columns = lines.get(index).split(";");
            for (int pos = 0; pos < columns.length; pos++) {
                String value = columns[pos];
                if (!value.isEmpty() && !value.equals("\"\"")) {
                    keyBuilder.setLength(0);
                    keyBuilder.append(pos).append(':').append(value);
                    String key = keyBuilder.toString();

                    Integer otherIndex = positionValueToIndex.get(key);
                    if (otherIndex != null) {
                        uf.union(index, otherIndex);
                    } else {
                        positionValueToIndex.put(key, index);
                    }
                }
            }
        }

        Map<Integer, List<String>> groups = new HashMap<>();
        for (int i = 0; i < lines.size(); i++) {
            int root = uf.find(i);
            groups.computeIfAbsent(root, k -> new ArrayList<>()).add(lines.get(i));
        }

        List<List<String>> result = new ArrayList<>(groups.values());
        result.sort((a, b) -> Integer.compare(b.size(), a.size()));
        return result;
    }





    @Override
    public void writeGroupsToFile(List<List<String>> groups, String outputPath) {
        int nonSingleGroups = 0;
        for (List<String> group : groups) {
            if (group.size() > 1) nonSingleGroups++;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(new File(outputPath).toPath(), StandardCharsets.UTF_8)) {
            writer.write("Количество групп с более чем одним элементом: " + nonSingleGroups + "\n\n");

            int groupNumber = 1;
            for (List<String> group : groups) {
                if (group.size() > 1) {
                    writer.write("Группа " + groupNumber++ + "\n");
                    for (String line : group) {
                        writer.write(line + "\n");
                    }
                    writer.write("\n");
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при записи в файл: " + e.getMessage());
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
