package org.y1000.sdb;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.Function;

public abstract class AbstractCSVSdbReader {

    /*
     * First column to the row map, for example "Cat, 1" will result: {"Cat": ["Cat", "1"]}.
     */
    private final Map<String, String[]> firstColumnAndValuesIndices;

    /*
     * Header to column index, for example "Name, Life" will result: {Name: 0}, {Life:  1}.
     */
    private final Map<String, Integer> headerColumnIndices;

    public static final String SDB_PATH = "/sdb";

    protected AbstractCSVSdbReader() {
        firstColumnAndValuesIndices = new HashMap<>();
        headerColumnIndices = new HashMap<>();
    }

    public boolean contains(String name) {
        return firstColumnAndValuesIndices.containsKey(name);
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
        String u = get(name, key);
        return StringUtils.isEmpty(u) ? null : Integer.parseInt(u);
    }

    protected Integer getIntOrZero(String name, String key) {
        Integer anInt = getInt(name, key);
        return anInt != null ? anInt : 0;
    }

    protected String get(String itemName, String key) {
        String[] strings = firstColumnAndValuesIndices.get(itemName);
        if (strings == null) {
            return null;
        }
        Integer index = headerColumnIndices.get(key);
        if (index == null || index >= strings.length) {
            return null;
        }
        return strings[index].trim();
    }

    Optional<String> getOptional(String name, String value) {
        var str = get(name, value);
        return !StringUtils.isEmpty(str) ? Optional.of(str) : Optional.empty();
    }

    protected String getOrNull(String itemName, String key) {
        var str = get(itemName, key);
        return StringUtils.isEmpty(str) ? null : str;
    }

    public Set<String> uniqueIds() {
        return firstColumnAndValuesIndices.keySet();
    }

    public Set<String> columnNames() {
        return headerColumnIndices.keySet();
    }


    protected void read(BufferedReader bufferedReader) {
        List<String> lines = bufferedReader.lines().toList();
        if (lines.isEmpty()) {
            throw new NoSuchElementException("Bad sdb");
        }
        String headerLine = lines.get(0).trim();
        if (headerLine.startsWith(";")) {
            return;
        }
        String[] header = headerLine.split(",");
        if (header.length == 0) {
            throw new NoSuchElementException("Bad sdb");
        }
        for (int i = 0; i < header.length; i++) {
            headerColumnIndices.put(header[i], i);
        }
        for (int i = 1; i < lines.size(); i++) {
            String item = lines.get(i).trim();
            String[] values = item.split(",");
            if (values.length == 0 || StringUtils.isBlank(values[0])) {
                continue;
            }
            this.firstColumnAndValuesIndices.put(values[0], values);
        }
    }

    protected void read(String name, String charset) {
        try (var inputstream = getClass().getResourceAsStream("/sdb/" + name)) {
            if (inputstream == null) {
                throw new NoSuchElementException("Sdb does not exist, " + name);
            }
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputstream, Charset.forName(charset)))) {
                read(bufferedReader);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void read(String name) {
        read(name, "GBK");
    }

}
