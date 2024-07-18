package org.y1000.entities.players.event;


import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.AbstractCreatureAttackEvent;
import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.CreatureAttackEventPacket;

public final class PlayerAttackEvent extends AbstractCreatureAttackEvent
        implements PlayerEvent {

    private final State attackState;

    private final Integer effectId;

    public PlayerAttackEvent(Player source, State attackState, Integer effectId) {
        super(source, source.coordinate(), source.direction());
        this.attackState = attackState;
        this.effectId = effectId;
    }
    public PlayerAttackEvent(Player source, State attackState) {
        this(source, attackState, null);
    }

    public static PlayerAttackEvent of(Player player, Integer effectId) {
        return new PlayerAttackEvent(player, player.stateEnum(), effectId);
    }

     @Override
    protected CreatureAttackEventPacket.Builder setCreatureSpecificFields(CreatureAttackEventPacket.Builder builder) {
        builder.setPlayer(true)
                .setState(attackState.value());
        if (effectId != null) {
            builder.setEffectId(effectId);
        }
        return builder;
    }

    @Override
    public void accept(PlayerEventVisitor visitor) {
        visitor.visit(this);
    }
}
