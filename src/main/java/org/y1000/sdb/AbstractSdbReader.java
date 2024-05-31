package org.y1000.sdb;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

public abstract class AbstractSdbReader {

    private final Map<String, String[]> values;

    private final Map<String, Integer> headerIndex;

    protected AbstractSdbReader() {
        values = new HashMap<>();
        headerIndex = new HashMap<>();
    }

    public boolean contains(String itemName) {
        return values.containsKey(itemName);
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

    public Integer getInt(String name, String key) {
        String[] strings = values.get(name);
        if (strings == null) {
            return null;
        }
        Integer index = headerIndex.get(key);
        return index != null ? Integer.parseInt(strings[index]) : null;
    }


    public String get(String itemName, String key) {
        String[] strings = values.get(itemName);
        if (strings == null) {
            return null;
        }
        Integer index = headerIndex.get(key);
        return index != null ? strings[index] : null;
    }



    public void read(String name) {
        try {
            URL resource = Thread.currentThread().getContextClassLoader().getResource("sdb/" + name);
            if (resource == null) {
                throw new NoSuchElementException("cant get db: " + name);
            }
            URI uri = resource.toURI();
            List<String> lines = Files.readAllLines(Paths.get(uri), Charset.forName("GBK"));
            if (lines.isEmpty()) {
                throw new NoSuchElementException("Empty sdb: " + name);
            }
            String[] header = lines.get(0).split(",");
            if (header.length == 0) {
                throw new NoSuchElementException("Empty sdb: " + name);
            }
            for (int i = 0; i < header.length; i++) {
                headerIndex.put(header[i], i);
            }
            for (int i = 1; i < lines.size(); i++) {
                String item = lines.get(i);
                String[] values = item.split(",");
                if (values.length == 0 || StringUtils.isBlank(values[0])) {
                    continue;
                }
                this.values.put(values[0], values);
            }
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
