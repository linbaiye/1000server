package org.y1000.sdb;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public abstract class AbstractKeyValuesSdbReader {

    protected Map<String, List<String>> readKeyValues(String fileName) {
        try (var inputstream = getClass().getResourceAsStream(fileName)) {
            if (inputstream == null) {
                throw new NoSuchElementException("Sdb does not exist, " + fileName);
            }
            Map<String, List<String>> result = new HashMap<>();
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputstream))) {
                List<String> lines = bufferedReader.lines().toList();
                for (String line : lines) {
                    String[] split = line.split(":");
                    if (split.length != 2)
                        continue;
                    String key = split[0].trim();
                    result.computeIfAbsent(key, (k) -> new ArrayList<>());
                    result.get(key).add(split[1].trim());
                }
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
