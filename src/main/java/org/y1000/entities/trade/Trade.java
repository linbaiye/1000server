package org.y1000.entities.trade;

import org.y1000.entities.players.Player;
import org.y1000.item.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class Trade {
    private final Player participant1;
    private boolean participant1Confirmed;
    private final Map<Integer, Item> participant1Items;
    private final Player participant2;
    private boolean participant2Confirmed;
    private final Map<Integer, Item> participant2Items;

    public Trade(Player participant1, Player participant2) {
        this.participant1 = participant1;
        this.participant2 = participant2;
        participant1Items = new HashMap<>();
        participant2Items = new HashMap<>();
        participant1Confirmed = false;
        participant2Confirmed = false;
    }

    private void add(Map<Integer, Item> items, int slot, Item item) {
        if (items.containsKey(slot)) {
            return;
        }
        items.put(slot, item);
    }

    public void addToTrade(Player from, int slot, Item item) {
        Objects.requireNonNull(from, "from can't be null.");
        Objects.requireNonNull(item, "item can't be null.");
        if (from.equals(participant1)) {
            add(participant1Items, slot, item);
        } else if (from.equals(participant2)) {
            add(participant2Items, slot, item);
        }
    }


    public void toggleConfirm(Player player) {
        if (player.equals(participant1)) {
            participant1Confirmed = !participant1Confirmed;
        } else if (player.equals(participant2)) {
            participant2Confirmed = !participant2Confirmed;
        }
        if (!participant1Confirmed || !participant2Confirmed) {
            return;
        }
    }
}
