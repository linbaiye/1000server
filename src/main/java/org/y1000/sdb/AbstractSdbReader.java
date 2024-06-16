package org.y1000.sdb;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;

public abstract class AbstractSdbReader {

    private final Map<String, String[]> values;

    private final Map<String, Integer> headerIndex;

    protected AbstractSdbReader() {
        values = new HashMap<>();
        headerIndex = new HashMap<>();
    }

    public boolean contains(String name) {
        return values.containsKey(name);
    }

    protected <E extends Enum<E>> E getEnum(String itemName, String key, Function<Integer, E> creator) {
        if (!contains(itemName)) {
            throw new NoSuchElementException(itemName + " does not exist.");
        }
        String s = get(itemName, key);
        if (s == null) {
            throw new NoSuchElementException(itemName + ":" + key + " does not exist.");
        }
        try {
            return creator.apply(Integer.parseInt(s));
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    protected Integer getInt(String name, String key) {
        String[] strings = values.get(name);
        if (strings == null) {
            return null;
        }
        Integer index = headerIndex.get(key);
        if (index == null) {
            return null;
        }
        String u = strings[index];
        return StringUtils.isEmpty(u) ? null : Integer.parseInt(u);
    }

    protected Integer getIntOrZero(String name, String key) {
        Integer anInt = getInt(name, key);
        return anInt != null ? anInt : 0;
    }

    protected String get(String itemName, String key) {
        String[] strings = values.get(itemName);
        if (strings == null) {
            return null;
        }
        Integer index = headerIndex.get(key);
        return index != null ? strings[index] : null;
    }

    public Set<String> monsterNames() {
        return values.keySet();
    }


    protected void read(String name) {
        try {
            try (var inputstream = getClass().getResourceAsStream("/sdb/" + name)) {
                if (inputstream == null) {
                    throw new NoSuchElementException("Sdb does not exist, " + name);
                }
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputstream, Charset.forName("GBK")));
                List<String> lines = bufferedReader.lines().toList();
                if (lines.isEmpty()) {
                    throw new NoSuchElementException("Empty sdb: " + name);
                }
                String headerLine = lines.get(0).trim();
                String[] header = headerLine.split(",");
                if (header.length == 0) {
                    throw new NoSuchElementException("Empty sdb: " + name);
                }
                for (int i = 0; i < header.length; i++) {
                    headerIndex.put(header[i], i);
                }
                for (int i = 1; i < lines.size(); i++) {
                    String item = lines.get(i).trim();
                    String[] values = item.split(",");
                    if (values.length == 0 || StringUtils.isBlank(values[0])) {
                        continue;
                    }
                    this.values.put(values[0], values);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
