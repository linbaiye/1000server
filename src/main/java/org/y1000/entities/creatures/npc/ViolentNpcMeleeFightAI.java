package org.y1000.entities.creatures.npc;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.AttackableEntity;
import org.y1000.entities.creatures.AiPathUtil;
import org.y1000.entities.creatures.State;

@Slf4j
public final class ViolentNpcMeleeFightAI extends AbstractNpcFightAI {

    public ViolentNpcMeleeFightAI(AttackableEntity enemy,
                                  ViolentNpc npc) {
        super(enemy, npc);
    }

    protected void fightProcess() {
        var enemy = getEnemy();
        if (npc.skill().isPresent() && npc.skill().get().isAvailable()) {
            npc.changeAI(new ViolentNpcRangedFightAI(enemy, npc));
            return;
        }
        if (npc.coordinate().directDistance(enemy.coordinate()) > 1) {
            AiPathUtil.moveProcess(npc, enemy.coordinate(), getPrevious(), () -> npc.startAction(State.IDLE), npc.walkSpeedInFight());
            return;
        }
        turnIfNotFaced();
        if (npc.cooldown() > 0) {
            npc.startAction(State.COOLDOWN);
        } else {
            npc.startAction(State.ATTACK);
            enemy.attackedBy(npc);
        }
    }

    @Override
    protected boolean shouldChangeEnemy(AttackableEntity newEnemy) {
        return getEnemy().coordinate().directDistance(npc.coordinate()) > 1;
    }

}
