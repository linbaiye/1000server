package org.y1000.entities.players;

import org.y1000.entities.Direction;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.AbstractCreateState;
import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.CreatureState;
import org.y1000.entities.creatures.State;
import org.y1000.message.clientevent.ClientMovementEvent;
import org.y1000.message.clientevent.ClientAttackEvent;
import org.y1000.message.clientevent.ClientEventVisitor;


public final class PlayerCooldownState extends AbstractCreateState<PlayerImpl> implements
        AttackableState, MovableState, ClientEventVisitor {

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
    public void afterAttacked(PlayerImpl player, Creature attacker) {
        int cooldown = player.cooldown();
        if (cooldown > 0) {
            player.changeState(new PlayerCooldownState(0, attacker));
        } else {
            player.attack(target);
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
    public CreatureState<PlayerImpl> stateForStopMoving(PlayerImpl player) {
        return new PlayerCooldownState(player.getStateMillis(stateEnum()), target);
    }

    @Override
    public CreatureState<PlayerImpl> stateForMove(PlayerImpl player, Direction direction) {
        return PlayerMoveState.moveBy(player, State.ENFIGHT_WALK, direction);
    }
}
