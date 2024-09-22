package org.y1000.entities.creatures.npc;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.AttackableActiveEntity;
import org.y1000.entities.creatures.AiPathUtil;
import org.y1000.entities.creatures.State;

@Slf4j
public final class ViolentNpcMeleeFightAI extends AbstractNpcFightAI {

    public ViolentNpcMeleeFightAI(AttackableActiveEntity enemy,
                                  ViolentNpc npc) {
        this(enemy, npc, 1);
    }

    public ViolentNpcMeleeFightAI(AttackableActiveEntity enemy,
                                  ViolentNpc npc, int speedRate) {
        super(enemy, npc, speedRate);
    }

    protected void fightProcess() {
        var enemy = getEnemy();
        if (npc.skill().isPresent() && npc.skill().get().isAvailable()) {
            npc.changeAndStartAI(new ViolentNpcRangedFightAI(enemy, npc));
            return;
        }
        if (npc.coordinate().directDistance(enemy.coordinate()) > 1) {
            //log.debug("Walk on unit in {} millis, stay millis {}.", computeWalkMillis(), computeStayMillis());
            AiPathUtil.moveProcess(npc, enemy.coordinate(), getPrevious(), () -> npc.stay(computeStayMillis()), computeWalkMillis(), computeStayMillis());
            return;
        }
        turnIfNotFaced();
        if (npc.cooldown() > 0) {
            npc.startAction(State.COOLDOWN);
        } else {
            //log.debug("Creature attack at {}, direction {}.", npc.coordinate(), npc.direction());
            npc.startAction(State.ATTACK);
            enemy.attackedBy(npc);
        }
    }

    @Override
    protected boolean shouldChangeEnemy(AttackableActiveEntity newEnemy) {
        return getEnemy().coordinate().directDistance(npc.coordinate()) > 1;
    }

}
