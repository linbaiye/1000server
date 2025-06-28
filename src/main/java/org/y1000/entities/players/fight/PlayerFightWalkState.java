package org.y1000.entities.players.fight;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.PlayerStateEnum;
import org.y1000.entities.players.AbstractPlayerMoveState;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.IPlayerState;
import org.y1000.util.Coordinate;

public final class PlayerFightWalkState extends AbstractPlayerMoveState {

    public PlayerFightWalkState(Coordinate start, Direction towards, int millisPerUnit) {
        super(PlayerStateEnum.ENFIGHT_WALK, start, towards, millisPerUnit);
    }

    @Override
    protected IPlayerState rewindState(PlayerImpl player) {
        return new PlayerCooldownState(player.getStateMillis(PlayerStateEnum.FightStand));
    }

    @Override
    protected void onMoved(PlayerImpl player) {
        player.attackKungFu().attackAgain(player);
    }

    public static PlayerFightWalkState walk(PlayerImpl player, Direction towards) {
        return new PlayerFightWalkState(player.coordinate(), towards, player.getStateMillis(PlayerStateEnum.ENFIGHT_WALK));
    }

    @Override
    public PlayerStateEnum decideAfterHurtState() {
        return PlayerStateEnum.FightStand;
    }

}
