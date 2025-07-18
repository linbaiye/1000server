package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.item.Item;
import org.y1000.message.AbstractPlayerEvent;
import org.y1000.realm.PlayerEventHandler;
import org.y1000.util.Coordinate;

public class PlayerDropItemEvent extends AbstractPlayerEvent  {

    private final Item item;
    private final Coordinate coordinate;

    public PlayerDropItemEvent(Player player,
                               Item item, Coordinate coordinate) {
        super(player);
        this.item = item;
        this.coordinate = coordinate;
    }

    @Override
    public void accept(PlayerEventHandler handler) {
        handler.dropItem(item, coordinate);
    }
}
