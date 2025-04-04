package org.y1000.entities.creatures.npc.AI;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.AttackableActiveEntity;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.creatures.npc.ViolentNpc;

@Slf4j
public final class ViolentNpcMeleeFightAI extends AbstractNpcFightAI {

    private final Chatter chatter;

    public ViolentNpcMeleeFightAI(AttackableActiveEntity enemy,
                                  ViolentNpc npc) {
        this(enemy, npc, npc.skill().isPresent() ? 2 : 1);
    }

    public ViolentNpcMeleeFightAI(AttackableActiveEntity enemy,
                                  ViolentNpc npc, int speedRate) {
        this(enemy, npc, speedRate, null);
    }

    public ViolentNpcMeleeFightAI(AttackableActiveEntity enemy,
                                  ViolentNpc npc, int speedRate,
                                  Chatter chatter) {
        super(enemy, npc, speedRate);
        this.chatter = chatter;
    }


    protected void fightProcess() {
        var enemy = getEnemy();
        if (npc.skill().isPresent() && npc.skill().get().isAvailable()) {
            npc.changeAI(new ViolentNpcRangedFightAI(enemy, npc));
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

    @Override
    protected void onFightDone(Npc npc) {
        if (chatter != null)
            npc.changeAI(new GuardWanderingAI(npc.coordinate(), chatter));
        else
            npc.startIdleAI();
    }

}
