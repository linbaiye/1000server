package org.y1000.sdb;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.creatures.npc.MerchantItem;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class NpcSetting {

    private String buyTitle;
    private String sellTitle;
    private String sellCaption;
    private String buyCaption;

    private int sellImage;
    private int buyImage;
    private List<String> buyItems;
    private List<String> sellItems;

    private NpcSetting() {
    }


    public static Optional<NpcSetting> tryLoad(String npcIdName) {
        var u = NpcSetting.class.getResource("/sdb/NpcSetting/" + npcIdName + ".txt");
        if (u == null) {
            return Optional.empty();
        }
        try (var stream = u.openStream()){
            List<MerchantItem> buy = new ArrayList<>();
            List<MerchantItem> sell = new ArrayList<>();
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream))) {
                List<String> lines = bufferedReader.lines().toList();
                for (String line : lines) {
                    String[] split = line.split(":");
                    if (split[0].equals("SELLITEM")) {
                        String itemName = split[1];
                        MerchantItem item = new MerchantItem(itemName, itemSdb.getPrice(itemName), itemSdb.getShape(itemName), itemSdb.getColor(itemName), itemSdb.canStack(itemName));
                        sell.add(item);
                    } else if (split[0].equals("BUYITEM")) {
                        String itemName = split[1];
                        MerchantItem item = new MerchantItem(itemName, itemSdb.getPrice(itemName), itemSdb.getShape(itemName), itemSdb.getColor(itemName), itemSdb.canStack(itemName));
                        buy.add(item);
                    }
                }
                return new MerchantItemSdb(buy, sell);
            }
        } catch (Exception e) {
            log.error("Failed to parse file, ", e);
            throw new RuntimeException(e);
        }
    }

}
