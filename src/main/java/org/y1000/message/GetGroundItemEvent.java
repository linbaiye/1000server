package org.y1000.message;


import lombok.Getter;
import org.y1000.entities.AttackableActiveEntity;
import org.y1000.entities.GroundedItem;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.PlayerEvent;
import org.y1000.message.serverevent.PlayerEventVisitor;

@Getter
public final class GetGroundItemEvent implements PlayerEvent {


    private final GroundedItem pickingItem;
    private final Player player;

    public GetGroundItemEvent(Player player, GroundedItem pickedItem) {
        this.pickingItem = pickedItem;
        this.player = player;
    }


    public Player player() {
        return player;
    }

    @Override
    public AttackableActiveEntity source() {
        return player;
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {
        playerEventHandler.visit(this);
    }
}
