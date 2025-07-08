package org.y1000.entities.players;

import org.y1000.entities.creatures.OldPlayerStateEnum;
import org.y1000.entities.creatures.monster.NpcAnimationEnum;

final class PlayerFrozenState implements IPlayerState {
    public static final PlayerFrozenState Instance = new PlayerFrozenState();

    private PlayerFrozenState() {}
    @Override
    public OldPlayerStateEnum stateEnum() {
        return OldPlayerStateEnum.IDLE;
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
    public NpcAnimationEnum state() {
        return null;
    }

    @Override
    public void update(PlayerImpl player, int delta) {

    }

    @Override
    public OldPlayerStateEnum decideAfterHurtState() {
        return OldPlayerStateEnum.Turn;
    }
}
