package org.y1000.item;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

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

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class ItemSdbTest {

    @Test
    public void test1() {
        var name = "Magic.sdb";
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
            int index = 0;
            for ( ; index < header.length; index++) {
                if (header[index].equals("Shape")) {
                    break;
                }
            }
            Map<String, String[]> result = new HashMap<>();
            for (int i = 1; i < lines.size(); i++) {
                String item = lines.get(i);
                String[] values = item.split(",");
                if (values.length == 0 || StringUtils.isBlank(values[0])) {
                    continue;
                }
                if (values[0].equals("无名拳法")) {
                    System.out.println(values[index]);
                }
                result.put(values[0], values);
            }
        } catch (URISyntaxException | IOException e) {
            log.error("Failed to read sdb {}.", name, e);
        }
    }
}