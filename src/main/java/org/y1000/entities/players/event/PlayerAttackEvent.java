package org.y1000.entities.players.event;


import org.y1000.entities.creatures.event.AbstractCreatureAttackEvent;
import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.CreatureAttackEventPacket;

public final class PlayerAttackEvent extends AbstractCreatureAttackEvent
        implements PlayerEvent {

    private final boolean below50;

    public PlayerAttackEvent(Player source,
                             int millisPerSprite, boolean below50) {
        super(source, millisPerSprite);
        this.below50 = below50;
    }

    @Override
    protected CreatureAttackEventPacket.Builder setCreatureSpecificFields(CreatureAttackEventPacket.Builder builder) {
        return builder.setPlayer(true)
                .setBelow50(below50);
    }

    @Override
    public void accept(PlayerEventVisitor visitor) {
        visitor.visit(this);
    }
}
