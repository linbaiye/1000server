package org.y1000;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

public class ReadFileTest {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("/Users/ab000785/Downloads/Init/Item.sdb"));
        String line;
        int count = 0;
        while ((line = br.readLine()) != null) {
            String[] split = line.split(",");
            if (!split[6].equals("9") && count++ > 0) {
                continue;
            }
            for (int i = 2; i < split.length; i++) {
                System.out.printf("%-12s", split[i]);
            }
            System.out.println();
        }
        br.close();
    }
}
