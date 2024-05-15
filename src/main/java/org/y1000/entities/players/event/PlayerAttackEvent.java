package org.y1000.entities.players.event;


import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.AbstractCreatureAttackEvent;
import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.CreatureAttackEventPacket;

public final class PlayerAttackEvent extends AbstractCreatureAttackEvent
        implements PlayerEvent {

    private final State attackState;

    public PlayerAttackEvent(Player source) {
        super(source);
        this.attackState = source.stateEnum();
    }

    public PlayerAttackEvent(Player source, State attackState) {
        super(source);
        this.attackState = attackState;
    }

    @Override
    protected CreatureAttackEventPacket.Builder setCreatureSpecificFields(CreatureAttackEventPacket.Builder builder) {
        return builder.setPlayer(true)
                .setState(attackState.value());
    }

    @Override
    public void accept(PlayerEventVisitor visitor) {
        visitor.visit(this);
    }
}
