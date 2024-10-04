package org.y1000.util;


import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ChangeCharsetUtil {

    private static void convert(Path path) {
        try {
            String s = Files.readString(path, Charset.forName("GBK"));
            Files.write(path, s.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        //Files.list(Path.of("D:\\work\\java\\1000server\\src\\main\\resources\\sdb\\")).forEach(System.out::println);
        //convert(Path.of("D:/work/java/1000server/src/main/resources/sdb/Init/1.0Item.sdb"));
        convert(Path.of("./src/main/resources/sdb/Npc.sdb"));
    }
}
