package org.y1000.entities.creatures.npc.AI;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.AttackableEntity;
import org.y1000.entities.creatures.monster.NpcActionEnum;
import org.y1000.entities.creatures.npc.INpc;
import org.y1000.entities.creatures.npc.ViolentNpc;

@Slf4j
public final class ViolentNpcMeleeFightAI extends AbstractNpcFightAI {

    private final Chatter chatter;

    public ViolentNpcMeleeFightAI(AttackableEntity enemy,
                                  ViolentNpc npc) {
        this(enemy, npc, npc.skill().isPresent() ? 2 : 1);
    }

    public ViolentNpcMeleeFightAI(AttackableEntity enemy,
                                  ViolentNpc npc, int speedRate) {
        this(enemy, npc, speedRate, null);
    }

    public ViolentNpcMeleeFightAI(AttackableEntity enemy,
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
        if (npc.maxCooldown() > 0) {
            npc.startAction(NpcActionEnum.Turn);
        } else {
            //log.debug("Creature attack at {}, direction {}.", npc.coordinate(), npc.direction());
            npc.startAction(NpcActionEnum.Attack);
            enemy.attackedBy(npc);
        }
    }

    @Override
    protected boolean shouldChangeEnemy(AttackableEntity newEnemy) {
        return getEnemy().coordinate().directDistance(npc.coordinate()) > 1;
    }

    @Override
    protected void onFightDone(INpc npc) {
        if (chatter != null)
            npc.changeAI(new GuardWanderingAI(npc.coordinate(), chatter));
        else
            npc.startIdleAI();
    }

}
