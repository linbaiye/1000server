package org.y1000.entities.creatures;


import org.y1000.entities.creatures.monster.NpcStateEnum;

public interface ICreatureState<C extends Creature> {
    OldPlayerStateEnum stateEnum();

    int elapsedMillis();

    int totalMillis();

    void update(C c, int delta);

    default boolean attackable() {
        return true;
    }

    default void moveToHurtCoordinate(C creature) {

    }

    NpcStateEnum state();

    default OldPlayerStateEnum decideAfterHurtState() {
        return OldPlayerStateEnum.IDLE;
    }
}
