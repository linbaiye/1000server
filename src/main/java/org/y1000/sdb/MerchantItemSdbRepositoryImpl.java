package org.y1000.sdb;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.creatures.npc.MerchantItem;
import org.y1000.item.ItemSdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public final class MerchantItemSdbRepositoryImpl implements MerchantItemSdbRepository {

    private final ItemSdb itemSdb;

    public MerchantItemSdbRepositoryImpl(ItemSdb itemSdb) {
        this.itemSdb = itemSdb;
    }

    @Override
    public MerchantItemSdb load(String name) {
        Validate.notNull(name, "file name can't be null.");
        try (var inputstream = getClass().getResourceAsStream("/sdb/npc/" + name)) {
            if (inputstream == null) {
                throw new NoSuchElementException("Sdb does not exist, " + name);
            }
            List<MerchantItem> buy = new ArrayList<>();
            List<MerchantItem> sell = new ArrayList<>();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputstream));
            List<String> lines = bufferedReader.lines().toList();
            for (String line : lines) {
                String[] split = line.split(":");
                if (split[0].equals("SELLITEM")) {
                    sell.add(new MerchantItem(split[1], itemSdb.getPrice(split[1])));
                } else if (split[0].equals("BUYITEM")) {
                    buy.add(new MerchantItem(split[1], itemSdb.getBuyPrice(split[1])));
                }
            }
            return new MerchantItemSdb(buy, sell);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
