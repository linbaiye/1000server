package org.y1000.entities.players.fight;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.Direction;
import org.y1000.entities.PhysicalEntity;
import org.y1000.entities.Projectile;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.CreatureShootEvent;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.PlayerState;
import org.y1000.entities.players.PlayerStillState;

@Slf4j
public final class PlayerBowAttackState extends AbstractPlayerAttackState {

    private final int counter;

    private PlayerBowAttackState(int totalMillis, PhysicalEntity target, State state, int counter) {
        super(totalMillis, target, state);
        this.counter = counter;
    }

    @Override
    public State stateEnum() {
        return State.BOW;
    }

    @Override
    public void update(PlayerImpl player, int delta) {
        if (elapsedMillis() == 0) {
            player.cooldownAttack();
        }
        elapse(delta);
        player.takeClientEvent().ifPresent(e -> e.accept(player, this));
        if (elapsedMillis() < getTotalMillis()) {
            return;
        }
        player.emitEvent(new CreatureShootEvent(new Projectile(player, getTarget(), arrowFlyingMillis(player, getTarget()))));
        if (counter <= 0) {
            player.changeState(PlayerStillState.chillOut(player));
        } else {
            player.changeState(PlayerBowCooldownState.cooldown(player, getTarget(), counter - 1));
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
    public PlayerState rangedCooldownState(PlayerImpl player, PhysicalEntity target) {
        return PlayerBowCooldownState.cooldown(player, getTarget(), counter - 1);
    }

    public static PlayerBowAttackState bow(PlayerImpl player, PhysicalEntity target, int counter) {
        return new PlayerBowAttackState(player.getStateMillis(State.BOW), target, State.BOW,  counter);
    }

    private static int arrowFlyingMillis(PlayerImpl player, PhysicalEntity target) {
        int dist = player.coordinate().directDistance(target.coordinate());
        return dist * 30;
    }

    public static PlayerBowAttackState bow(PlayerImpl player, PhysicalEntity target) {
        return new PlayerBowAttackState(player.getStateMillis(State.BOW), target, State.BOW, 5);
    }
}
