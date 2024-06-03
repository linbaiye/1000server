package org.y1000.entities.players.fight;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.AbstractPlayerMoveState;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.PlayerState;
import org.y1000.util.Coordinate;

public final class PlayerFightWalkState extends AbstractPlayerMoveState {

    public PlayerFightWalkState(Coordinate start, Direction towards, int millisPerUnit) {
        super(State.ENFIGHT_WALK, start, towards, millisPerUnit);
    }

    @Override
    protected PlayerState rewindState(PlayerImpl player) {
        return new PlayerCooldownState(player.cooldown());
    }

    @Override
    protected void onMoved(PlayerImpl player) {
        if (player.cooldown() > 0) {
            player.changeState(new PlayerCooldownState(player.cooldown()));
        } else {
            player.changeState(PlayerAttackState.of(player));
        }
    }

    public static PlayerFightWalkState walk(PlayerImpl player, Direction towards) {
        return new PlayerFightWalkState(player.coordinate(), towards, player.getStateMillis(State.ENFIGHT_WALK));
    }
}
