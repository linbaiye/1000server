package org.y1000.entities.players.fight;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.Direction;
import org.y1000.entities.PhysicalEntity;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.AbstractPlayerStillState;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.PlayerState;
import org.y1000.entities.players.PlayerStillState;

@Slf4j
public final class PlayerMeleeAttackReadyState extends AbstractPlayerStillState {
    private final PhysicalEntity target;

    public PlayerMeleeAttackReadyState(int totalMillis, PhysicalEntity target) {
        super(totalMillis, State.COOLDOWN);
        this.target = target;
    }


    @Override
    public void update(PlayerImpl player, int delta) {
        if (target.coordinate().directDistance(player.coordinate()) <= 1) {
            fireAttack(player, target, PlayerMeleeAttackState.meleeAttackState(player, target));
        } else {
            elapseAndHandleInput(player, delta);
        }
    }

    @Override
    public Logger logger() {
        return log;
    }

    @Override
    public PlayerState stateForMove(PlayerImpl player, Direction direction) {
        return PlayerMeleeEnfightWalkState.move(player, direction, target);
    }

    public static PlayerMeleeAttackReadyState prepareSwing(PlayerImpl player, PhysicalEntity target) {
        return new PlayerMeleeAttackReadyState(player.getStateMillis(State.COOLDOWN), target);
    }

    @Override
    public void attackKungFuTypeChanged(PlayerImpl player) {
        if (player.attackKungFu().isRanged()) {
            player.changeState(PlayerRangedCooldownState.cooldown(player, target, 1));
        } else {
            player.changeState(new PlayerMeleeCooldownState(player.cooldown(), target));
        }
    }
}
