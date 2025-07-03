package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class Solution implements SolutionInterface{


    @Override
    public List<String> readAndFilterLines(String filePath) {
        List<String> lines = new ArrayList<>();

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
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        return lines;
    }


    @Override
    public List<List<String>> groupLines(List<String> lines) {
        // Для отображения: позиция
        Map<Integer, Map<String, Integer>> positionToValueToIndex = new HashMap<>();

        int n = lines.size();
        UnionFind uf = new UnionFind(n);
        List<String> validLines = new ArrayList<>();

        // Чтение и обработка строк
        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            String[] columns = line.split(";");

            // Валидация
            boolean isValid = true;
            for (String col : columns) {
                if (!isLineCorrect(col)) {
                    isValid = false;
                    break;
                }
            }
            if (!isValid) continue;

            validLines.add(line); // сохранить валидную строку

            for (int pos = 0; pos < columns.length; pos++) {
                String value = columns[pos].trim();
                if (value.isEmpty() || value.equals("\"\"")) continue;

                positionToValueToIndex
                        .computeIfAbsent(pos, k -> new HashMap<>());

                Map<String, Integer> valueToIndex = positionToValueToIndex.get(pos);

                if (valueToIndex.containsKey(value)) {
                    int otherIndex = valueToIndex.get(value);
                    uf.union(index, otherIndex);
                } else {
                    valueToIndex.put(value, index);
                }
            }
        }

        // Сбор групп по корню
        Map<Integer, List<String>> groups = new HashMap<>();
        for (int i = 0; i < validLines.size(); i++) {
            int root = uf.find(i);
            groups.computeIfAbsent(root, k -> new ArrayList<>()).add(validLines.get(i));
        }

        // Сортировка групп по убыванию размера
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
