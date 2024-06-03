package org.y1000.entities.players.fight;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.Projectile;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.CreatureShootEvent;
import org.y1000.entities.players.PlayerImpl;

@Slf4j
public final class PlayerAttackState extends AbstractFightingState {

    private final State attackingState;

    public PlayerAttackState(int totalMillis, State attackingState) {
        super(totalMillis);
        this.attackingState = attackingState;
    }

    @Override
    public State stateEnum() {
        return attackingState;
    }

    @Override
    public void update(PlayerImpl player, int delta) {
        if (!elapse(delta)) {
            return;
        }
        if (player.attackKungFu().isRanged() && player.isFighting()) {
            int dist = player.coordinate().directDistance(player.getFightingEntity().coordinate());
            player.emitEvent(new CreatureShootEvent(new Projectile(player, player.getFightingEntity(), dist * 30)));
        }
        player.attackKungFu().attackAgain(player);
    }

    @Override
    public Logger logger() {
        return log;
    }

    public static PlayerAttackState of(PlayerImpl player) {
        State state = player.attackKungFu().randomAttackState();
        return new PlayerAttackState(player.getStateMillis(state), state);
    }
}
