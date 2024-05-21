package org.y1000.entities.players.fight;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.Direction;
import org.y1000.entities.PhysicalEntity;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.PlayerState;

@Slf4j
public final class PlayerBowCooldownState extends AbstractCooldownState {

    private final int attackedCounter;

    public PlayerBowCooldownState(int length,
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
        return PlayerBowEnfightWalkState.walk(player, direction, getTarget(), attackedCounter);
    }

    @Override
    public PlayerState rangedAttackState(PlayerImpl player, PhysicalEntity target) {
        return PlayerBowAttackState.bow(player, target, attackedCounter);
    }

    public static PlayerBowCooldownState cooldown(PlayerImpl player, PhysicalEntity target, int attackedCounter) {
        return new PlayerBowCooldownState(player.cooldown(), target, attackedCounter);
    }

    public static PlayerBowCooldownState cooldown(int millis, PhysicalEntity target, int attackedCounter) {
        return new PlayerBowCooldownState(millis, target, attackedCounter);
    }
}
