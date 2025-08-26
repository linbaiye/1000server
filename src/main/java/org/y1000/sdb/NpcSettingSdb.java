package org.y1000.sdb;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

@Getter
@Slf4j
public class NpcSettingSdb {

    private String title;
    private String sellCaption;
    private String buyCaption;


    private int image;
    private final List<String> buyItems = new ArrayList<>();
    private final List<String> sellItems = new ArrayList<>();
    private final List<String> quests = new ArrayList<>();

    private boolean bank = false;

    private static final Map<String, NpcSettingSdb> CACHE = new HashMap<>();

    private NpcSettingSdb() {
    }

    private void validate() {
        if (title == null)
            throw new IllegalStateException("No title");
        if (image == 0)
            throw new IllegalStateException("No image");
        if (!buyItems.isEmpty())
            Validate.isTrue(buyCaption != null);
        if (!sellItems.isEmpty())
            Validate.isTrue(sellCaption != null);
    }


    public static synchronized Optional<NpcSettingSdb> tryLoad(String npcIdName) {
        if (CACHE.containsKey(npcIdName)) {
            return Optional.of(CACHE.get(npcIdName));
        }
        var u = NpcSettingSdb.class.getResource("/sdb/NpcSetting/" + npcIdName + ".txt");
        if (u == null) {
            return Optional.empty();
        }
        NpcSettingSdb npcSettingsdb = new NpcSettingSdb();
        try (var stream = u.openStream()){
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream))) {
                List<String> lines = bufferedReader.lines().toList();
                for (String line : lines) {
                    String[] split = line.split(":");
                    switch (split[0]) {
                        case "SELLITEM" -> npcSettingsdb.sellItems.add(split[1]);
                        case "BUYITEM" -> npcSettingsdb.buyItems.add(split[1]);
                        case "SELLTITLE", "BUYTITLE", "TITLE" -> npcSettingsdb.title = split[1];
                        case "BUYCAPTION" -> npcSettingsdb.buyCaption = split[1];
                        case "SELLCAPTION" -> npcSettingsdb.sellCaption = split[1];
                        case "BUYIMAGE", "IMAGE", "SELLIMAGE" -> npcSettingsdb.image = Integer.parseInt(split[1]);
                        case "QUEST" -> npcSettingsdb.quests.add(split[1]);
                        case "BANK" -> npcSettingsdb.bank = true;
                    }
                }
            }
            npcSettingsdb.validate();
            CACHE.put(npcIdName, npcSettingsdb);
            return Optional.of(npcSettingsdb);
        } catch (Exception e) {
            log.error("Failed to parse file for {}, ", npcIdName, e);
            throw new RuntimeException(e);
        }
    }

}
