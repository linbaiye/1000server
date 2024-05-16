package org.y1000.entities.players;

import org.y1000.entities.Direction;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.AbstractCreateState;
import org.y1000.entities.creatures.State;
import org.y1000.message.clientevent.ClientMovementEvent;
import org.y1000.message.clientevent.ClientAttackEvent;
import org.y1000.message.clientevent.ClientEventVisitor;


final class PlayerCooldownState extends AbstractCreateState<PlayerImpl> implements
        AttackableState, MovableState, ClientEventVisitor, PlayerState {

    private final Entity target;

    public PlayerCooldownState(int length, Entity target) {
        super(length);
        this.target = target;
    }

    @Override
    public State stateEnum() {
        return State.COOLDOWN;
    }

    @Override
    public void update(PlayerImpl player, int delta) {
        if (elapse(delta)) {
            player.attack(target);
        } else {
            player.takeClientEvent().ifPresent(e -> e.accept(player, this));
        }
    }

    @Override
    public void visit(PlayerImpl player, ClientMovementEvent event) {
        move(player, event);
    }

    @Override
    public void visit(PlayerImpl player, ClientAttackEvent event) {
        attackIfInsight(player, event);
    }

    @Override
    public PlayerState stateForStopMoving(PlayerImpl player) {
        return new PlayerCooldownState(player.getStateMillis(stateEnum()), target);
    }

    @Override
    public PlayerState stateForMove(PlayerImpl player, Direction direction) {
        return PlayerMoveState.moveBy(player, State.ENFIGHT_WALK, direction);
    }

    @Override
    public void afterHurt(PlayerImpl player) {
        int cooldown = player.cooldown();
        if (cooldown > 0) {
            player.changeState(new PlayerCooldownState(cooldown, target));
        } else {
            player.attack(target);
        }
    }
}
