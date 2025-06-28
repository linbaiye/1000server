package org.y1000.entities.players.event;


import org.y1000.entities.creatures.PlayerStateEnum;
import org.y1000.entities.creatures.event.AbstractCreatureAttackEvent;
import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.CreatureAttackEventPacket;

public final class PlayerAttackEvent extends AbstractCreatureAttackEvent
        implements PlayerEvent {

    private final PlayerStateEnum attackPlayerStateEnum;

    private final Integer effectId;

    public PlayerAttackEvent(Player source, PlayerStateEnum attackPlayerStateEnum, Integer effectId) {
        super(source, source.coordinate(), source.direction());
        this.attackPlayerStateEnum = attackPlayerStateEnum;
        this.effectId = effectId;
    }
    public PlayerAttackEvent(Player source, PlayerStateEnum attackPlayerStateEnum) {
        this(source, attackPlayerStateEnum, null);
    }

    public static PlayerAttackEvent of(Player player, Integer effectId) {
        return new PlayerAttackEvent(player, player.stateEnum(), effectId);
    }

     @Override
    protected CreatureAttackEventPacket.Builder setCreatureSpecificFields(CreatureAttackEventPacket.Builder builder) {
        builder.setPlayer(true)
                .setState(attackPlayerStateEnum.value());
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
