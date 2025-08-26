package org.y1000.realm.event;

import org.y1000.entities.players.Player;
import org.y1000.util.Coordinate;

public class PlayerDropGuildStoneEvent implements RealmEvent  {

    private final Player source;
    private final Coordinate at;
    private final int slot;

    public PlayerDropGuildStoneEvent(Player source,
                                     Coordinate at,
                                     int slot) {
        this.source = source;
        this.slot = slot;
        this.at = at;
    }

    @Override
    public void accept(RealmEventHandler handler) {
        handler.playerDropGuildStone(source, at, slot);
    }
}
