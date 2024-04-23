package org.y1000.entities.repository;

import org.y1000.entities.players.Player;
import org.y1000.util.Coordinate;

import java.util.HashMap;
import java.util.Map;

public final class PlayerRepositoryImpl implements PlayerRepository {

    private static final int[] slots = new int[]{-1, -1, 1,-1,-1,-1,-1,-1,-1,-1};

    @Override
    public Player load(long id) {
        return Player.create(id, new Coordinate(39,27));
    }

    private int findSlot() {
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] == -1) {
                slots[i] = 0;
                return i;
            }
        }
        return 0;
    }

    @Override
    public Player load() {
        int slot = findSlot();
        return Player.create(slot, new Coordinate(39 + slot, 27));
    }

    @Override
    public void save(Player player) {
        slots[(int)player.id()] = -1;
    }

}
