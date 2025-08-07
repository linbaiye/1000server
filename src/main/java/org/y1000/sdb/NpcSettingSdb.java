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

    private String buyTitle;
    private String sellTitle;
    private String title;
    private String sellCaption;
    private String buyCaption;


    private int sellImage;
    private int buyImage;
    private int image;
    private final List<String> buyItems = new ArrayList<>();
    private final List<String> sellItems = new ArrayList<>();
    private final List<String> quests = new ArrayList<>();

    private static final Map<String, NpcSettingSdb> CACHE = new HashMap<>();

    public int getAnyImage() {
        if (image != 0)
            return image;
        return sellImage == 0 ? buyImage : sellImage;
    }

    public String getAnyTitle() {
        if (title != null)
            return title;
        return buyTitle != null ? buyTitle : sellTitle;
    }

    private NpcSettingSdb() {
    }

    private void validate() {
        if (buyTitle == null && sellTitle == null && title == null)
            throw new IllegalStateException("No title");
        if (buyImage == 0 && sellImage == 0 && image == 0)
            throw new IllegalStateException("No image");
        if (buyItems.isEmpty() && sellItems.isEmpty() && quests.isEmpty())
            throw new IllegalStateException("No items");
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
                        case "SELLTITLE" -> npcSettingsdb.sellTitle = split[1];
                        case "BUYTITLE" -> npcSettingsdb.buyTitle = split[1];
                        case "BUYCAPTION" -> npcSettingsdb.buyCaption = split[1];
                        case "SELLCAPTION" -> npcSettingsdb.sellCaption = split[1];
                        case "BUYIMAGE" -> npcSettingsdb.buyImage = Integer.parseInt(split[1]);
                        case "SELLIMAGE" -> npcSettingsdb.sellImage = Integer.parseInt(split[1]);
                        case "TITLE" -> npcSettingsdb.title = split[1];
                        case "IMAGE" -> npcSettingsdb.image = Integer.parseInt(split[1]);
                        case "QUEST" -> npcSettingsdb.quests.add(split[1]);
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
