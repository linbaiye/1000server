package org.y1000.entities.players.event;


import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.AbstractCreatureAttackEvent;
import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.CreatureAttackEventPacket;

public final class PlayerAttackEvent extends AbstractCreatureAttackEvent
        implements PlayerEvent {

    private final State attackState;
    private final long targetId;

    public PlayerAttackEvent(Player source, State attackState) {
        this(source, attackState, 0);
    }

    private PlayerAttackEvent(Player source, State attackState, long targetId) {
        super(source, source.coordinate(), source.direction());
        this.attackState = attackState;
        this.targetId = targetId;
    }

    public static PlayerAttackEvent of(Player player) {
        return new PlayerAttackEvent(player, player.stateEnum());
    }

    public static PlayerAttackEvent of(Player player, long id) {
        return new PlayerAttackEvent(player, player.stateEnum(), id);
    }

    @Override
    protected CreatureAttackEventPacket.Builder setCreatureSpecificFields(CreatureAttackEventPacket.Builder builder) {
        if (targetId != 0) {
            builder.setTargetId(targetId);
        }
        return builder.setPlayer(true)
                .setState(attackState.value());
    }

    @Override
    public void accept(PlayerEventVisitor visitor) {
        visitor.visit(this);
    }
}
