package org.y1000.entities.players.fight;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.Direction;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.PlayerState;
import org.y1000.entities.players.PlayerStillState;

@Slf4j
public final class PlayerBowAttackState extends AbstractPlayerAttackState {

    private int arrowFlyingMillis;

    private final int counter;

    private PlayerBowAttackState(int totalMillis, Entity target, State state, int arrowFlyingMillis, int counter) {
        super(totalMillis, target, state);
        this.arrowFlyingMillis = arrowFlyingMillis;
        this.counter = counter;
    }

    @Override
    public State stateEnum() {
        return State.BOW;
    }

    @Override
    public void update(PlayerImpl player, int delta) {
        elapse(delta);
        if (elapsedMillis() >= arrowFlyingMillis) {
            getTarget().attackedBy(player);
            arrowFlyingMillis = Integer.MAX_VALUE;
        }
        player.takeClientEvent().ifPresent(e -> e.accept(player, this));
        if (elapsedMillis() < getTotalMillis()) {
            return;
        }
        if (counter <= 0) {
            log.debug("Bow ended.");
            player.changeState(PlayerStillState.chillOut(player));
        } else {
            attack(player, getTarget());
        }
    }

    @Override
    public Logger logger() {
        return log;
    }

    @Override
    public PlayerState stateForStopMoving(PlayerImpl player) {
        return PlayerBowCooldownState.cooldown(player, getTarget(), counter - 1);
    }

    @Override
    public PlayerState stateForMove(PlayerImpl player, Direction direction) {
        return PlayerBowEnfightWalkState.walk(player, direction, getTarget(), counter - 1);
    }

    @Override
    public PlayerState remoteCooldownState(PlayerImpl player, Entity target) {
        return PlayerBowCooldownState.cooldown(player, getTarget(), counter - 1);
    }

    public static PlayerBowAttackState bow(PlayerImpl player, Entity target, int counter) {
        return new PlayerBowAttackState(player.getStateMillis(State.BOW), target, State.BOW, arrowFlyingMillis(player, target), counter);
    }

    private static int arrowFlyingMillis(PlayerImpl player, Entity target) {
        int dist = player.coordinate().directDistance(target.coordinate());
        return dist * 50;
    }

    public static PlayerBowAttackState bow(PlayerImpl player, Entity target) {
        return new PlayerBowAttackState(player.getStateMillis(State.BOW), target, State.BOW, arrowFlyingMillis(player, target), 5);
    }
}
