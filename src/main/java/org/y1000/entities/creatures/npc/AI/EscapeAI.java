package org.y1000.entities.creatures.npc.AI;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.util.Coordinate;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public final class EscapeAI extends AbstractAI<Npc> {

    private final ViolentCreature enemy;

    private Mover<Npc> mover;

    public EscapeAI(ViolentCreature enemy) {
        this.enemy = enemy;
    }

    private Coordinate computeEscapePoint(Npc npc) {
        int len = 0;
        Coordinate coordinate = npc.coordinate();
        Coordinate target = Coordinate.Empty;
        for (int i = 0; i < 10; i++) {
            int x = coordinate.x() - 10 + ThreadLocalRandom.current().nextInt(0, 20);
            int y = coordinate.y() - 10 + ThreadLocalRandom.current().nextInt(0, 20);
            var dist = Math.max(Math.abs(enemy.coordinate().x() - x), Math.abs(enemy.coordinate().y() - y));
            if (len < dist)  {
                Coordinate xy = Coordinate.xy(x, y);
                if (npc.realmMap().movable(xy)) {
                    len = dist;
                    target = xy;
                }
            }
        }
        if (target.equals(coordinate)) {
            target = Coordinate.Empty;
        }
        return target;
    }

    @Override
    protected void onStartNotDead(Npc violentNpc) {
        if (mover == null)
            mover = Mover.run(violentNpc, computeEscapePoint(violentNpc));
        mover.nextMove(this::onNoPath);
    }

    @Override
    protected Class<Npc> npcType() {
        return Npc.class;
    }

    @Override
    protected void onActionDoneNotDead(Npc violentNpc) {
        if (mover.isArrived()) {
            violentNpc.changeAndStartAI(new VigilantWanderingAI());
        } else {
            if (mover.usedMillis() > 10000) {
                violentNpc.changeAndStartAI(new VigilantWanderingAI());
            } else {
                mover.nextMove(this::onNoPath);
            }
        }
    }

    @Override
    protected void onMoveFailedNotDead(Npc violentNpc) {
        mover.nextMove(this::onNoPath);
    }

    private void onNoPath(Npc npc) {
        npc.changeAndStartAI(new VigilantWanderingAI());
    }
}
