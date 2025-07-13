package org.y1000.entities.creatures.npc;

import org.y1000.entities.Direction;

public final class NpcTurnAbility extends AbstractNpcNonMoveAbility {
    public NpcTurnAbility(NpcAnimation animation) {
        super(animation);
    }

    public void turn(Npc npc, Direction direction) {
        npc.changeDirection(direction);
        sendActionAndStartAnimation(npc);
    }

    @Override
    public boolean update(int delta) {
        return updateAnimation(delta);
    }
}
