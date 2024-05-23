package org.y1000.entities.players.fight;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.Direction;
import org.y1000.entities.PhysicalEntity;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.PlayerState;

@Slf4j
public final class PlayerMeleeAttackState extends AbstractPlayerAttackState {

    private PlayerMeleeAttackState(int length,
                                  PhysicalEntity target,
                                  State state) {
        super(length, target, state);
    }


    @Override
    public void attackKungFuTypeChanged(PlayerImpl player) {
        if (player.attackKungFu().isRanged()) {
            player.changeState(PlayerRangedCooldownState.cooldown(player, getTarget(), 1));
        } else {
            player.changeState(new PlayerMeleeCooldownState(player.cooldown(), getTarget()));
        }
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

    public static PlayerMeleeAttackState meleeAttackState(PlayerImpl player, PhysicalEntity target) {
        State state = player.attackKungFu().randomAttackState();
        return new PlayerMeleeAttackState(player.getStateMillis(state), target, state);
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
