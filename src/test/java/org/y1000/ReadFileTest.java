package org.y1000;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ThreadLocalRandom;

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

    @Test
    void random() {
        int odd = 0, even = 0;
        int[] arr = new int[2];
        for (int i = 0; i < 1000000; i++) {
            int index = ThreadLocalRandom.current().nextInt(0, 2) % 2 ;
            arr[index]++;
        }
        System.out.println(arr[0]);
        System.out.println(arr[1]);
    }
}
