package org.y1000.kungfu.attack;

import lombok.experimental.SuperBuilder;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.PlayerStillState;
import org.y1000.entities.players.fight.PlayerWaitDistanceState;
import org.y1000.entities.players.fight.PlayerWaitResourceState;

@SuperBuilder
public abstract class AbstractMeleeKungFu extends AbstractAttackKungFu {

    @Override
    public void attackAgain(PlayerImpl player) {
        if (player.isFighting()) {
            player.changeState(PlayerStillState.chillOut(player));
            return;
        }
        if (player.getFightingEntity().coordinate().directDistance(player.coordinate()) > 1) {
            player.changeState(new PlayerWaitDistanceState(player.getStateMillis(State.COOLDOWN)));
            return;
        }
        if (hasEnoughResources(player)) {
            player.changeState(new PlayerWaitResourceState(player.getStateMillis(State.COOLDOWN)));
            return;
        }
    }
}
