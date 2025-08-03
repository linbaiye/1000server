package org.y1000.realm;

import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.PlayerLetFlyProjectileEvent;
import org.y1000.item.Item;
import org.y1000.message.I2ClientMessage;
import org.y1000.realm.event.RealmEvent;
import org.y1000.util.Coordinate;

public interface PlayerEventHandler extends EntityEventHandler {

    void sendTo(Player player, I2ClientMessage message);

    void sendToVisiblePlayersAndSelf(Player player, I2ClientMessage message);

    void updateAOI(Player player);

    void onPlayerFireProjectile(PlayerLetFlyProjectileEvent event);

    void dropItem(Item item, Coordinate droppedAt);

    void sendBroadcast(RealmEvent event);

}
