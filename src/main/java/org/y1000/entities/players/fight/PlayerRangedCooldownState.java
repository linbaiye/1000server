package org.y1000.entities.players.fight;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.Direction;
import org.y1000.entities.PhysicalEntity;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.PlayerState;

@Slf4j
public final class PlayerRangedCooldownState extends AbstractCooldownState {

    private final int attackedCounter;

    private PlayerRangedCooldownState(int length,
                                     PhysicalEntity target,
                                     int attackedCounter) {
        super(length, target);
        this.attackedCounter = attackedCounter;
    }

    @Override
    public Logger logger() {
        return log;
    }

    @Override
    public PlayerState stateForMove(PlayerImpl player, Direction direction) {
        return PlayerRangedEnfightWalkState.walk(player, direction, getTarget(), attackedCounter);
    }

    @Override
    public AbstractPlayerAttackState rangedAttackState(PlayerImpl player, PhysicalEntity target) {
        return PlayerRangedAttackState.rangedAttack(player, target, attackedCounter);
    }

    public static PlayerRangedCooldownState cooldown(PlayerImpl player, PhysicalEntity target, int attackCounter) {
        return new PlayerRangedCooldownState(player.cooldown(), target, attackCounter);
    }

    public static PlayerRangedCooldownState cooldown(int millis, PhysicalEntity target, int attackCounter) {
        return new PlayerRangedCooldownState(millis, target, attackCounter);
    }
}
