package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.AbstractCreateState;
import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.CreatureState;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.kungfu.FootKungFu;
import org.y1000.message.clientevent.ClientAttackEvent;
import org.y1000.message.clientevent.ClientMovementEvent;
import org.y1000.message.clientevent.ClientEventVisitor;

import java.util.Optional;

@Slf4j
public final class PlayerIdleState extends AbstractCreateState<PlayerImpl>
        implements AttackableState, MovableState, ClientEventVisitor {

    public PlayerIdleState(int millis) {
        super(millis);
    }

    @Override
    public State stateEnum() {
        return State.IDLE;
    }

    @Override
    public void update(PlayerImpl player, int deltaMillis) {
        if (elapse(deltaMillis)) {
            reset();
        }
        player.takeClientEvent().ifPresent(e -> e.accept(player, this));
    }

    @Override
    public void afterAttacked(PlayerImpl player, Creature attacker) {
        player.changeState(this);
    }

    @Override
    public void visit(PlayerImpl player,
                      ClientAttackEvent event) {
        attackIfInsight(player, event);
    }

    @Override
    public void visit(PlayerImpl player, ClientMovementEvent movementEvent) {
        move(player, movementEvent);
    }

    @Override
    public CreatureState<PlayerImpl> stateForStopMoving(PlayerImpl player) {
        return new PlayerIdleState(player.getStateMillis(State.IDLE));
    }

    @Override
    public CreatureState<PlayerImpl> stateForMove(PlayerImpl player, Direction direction) {
        Optional<FootKungFu> footMagic = player.footKungFu();
        State state = footMagic.map(magic -> magic.canFly() ? State.FLY : State.RUN)
                .orElse(State.WALK);
        return PlayerMoveState.moveBy(player, state, direction);
    }

    public static PlayerIdleState of(PlayerImpl player) {
        return new PlayerIdleState(player.getStateMillis(State.IDLE));
    }
}
