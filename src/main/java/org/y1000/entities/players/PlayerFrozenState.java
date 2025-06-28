package org.y1000.entities.players;

import org.y1000.entities.creatures.PlayerStateEnum;
import org.y1000.entities.creatures.monster.NpcStateEnum;

final class PlayerFrozenState implements IPlayerState {
    public static final PlayerFrozenState Instance = new PlayerFrozenState();

    private PlayerFrozenState() {}
    @Override
    public PlayerStateEnum stateEnum() {
        return PlayerStateEnum.IDLE;
    }

    @Override
    public int elapsedMillis() {
        return 0;
    }

    @Override
    public int totalMillis() {
        return 0;
    }

    @Override
    public boolean attackable() {
        return false;
    }

    @Override
    public NpcStateEnum state() {
        return null;
    }

    @Override
    public void update(PlayerImpl player, int delta) {

    }

    @Override
    public PlayerStateEnum decideAfterHurtState() {
        return PlayerStateEnum.Turn;
    }
}
