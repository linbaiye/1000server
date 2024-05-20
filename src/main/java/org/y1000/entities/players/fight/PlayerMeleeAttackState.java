package org.y1000.entities.players.fight;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.Direction;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.PlayerState;

@Slf4j
public final class PlayerMeleeAttackState extends AbstractPlayerAttackState {

    public PlayerMeleeAttackState(int length,
                                  Entity target,
                                  State state) {
        super(length, target, state);
    }

    @Override
    public void afterHurt(PlayerImpl player) {
        player.changeState(new PlayerMeleeCooldownState(player.cooldown(), getTarget()));
    }

    @Override
    public void update(PlayerImpl player, int delta) {
        if (elapsedMillis() == 0) {
            player.cooldownAttack();
            getTarget().attackedBy(player);
        }
        player.takeClientEvent().ifPresent(e -> e.accept(player, this));
        if (elapse(delta)) {
            attack(player, getTarget());
        }
    }

    public static PlayerMeleeAttackState attack(Entity target, State state, int length) {
        return new PlayerMeleeAttackState(length, target, state );
    }

    @Override
    public Logger logger() {
        return log;
    }

    @Override
    public PlayerState stateForStopMoving(PlayerImpl player) {
        return new PlayerMeleeCooldownState(player.getStateMillis(State.COOLDOWN), getTarget());
    }

    @Override
    public PlayerState stateForMove(PlayerImpl player, Direction direction) {
        return PlayerMeleeEnfightWalkState.move(player, direction, getTarget());
    }

    @Override
    public String toString() {
        return "PlayerAttackState{" +
                "target=" + getTarget() +
                ", attackState=" + stateEnum() +
                '}';
    }
}
