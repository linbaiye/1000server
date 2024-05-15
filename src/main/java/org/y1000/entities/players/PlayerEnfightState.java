package org.y1000.entities.players;

import org.y1000.entities.Direction;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.AbstractCreateState;
import org.y1000.entities.creatures.State;
import org.y1000.message.clientevent.ClientEventVisitor;

public class PlayerEnfightState extends AbstractCreateState<PlayerImpl> implements
        ClientEventVisitor, AttackableState, MovableState, PlayerState {
    private final Entity target;
    public PlayerEnfightState(int totalMillis, Entity target) {
        super(totalMillis);
        this.target = target;
    }
    public PlayerEnfightState(int totalMillis) {
        this(totalMillis, null);
    }

    @Override
    public State stateEnum() {
        return State.COOLDOWN;
    }

    @Override
    public void update(PlayerImpl player, int delta) {
        if (target != null && target.coordinate().distance(player.coordinate()) <= 1) {
            player.attack(target);
        } else if (elapse(delta)) {
            reset();
        }
    }

    @Override
    public PlayerState stateForStopMoving(PlayerImpl player) {
        return null;
    }

    @Override
    public PlayerState stateForMove(PlayerImpl player, Direction direction) {
        return null;
    }

    @Override
    public void afterAttacked(PlayerImpl player) {
        player.changeState(this);
    }
}
