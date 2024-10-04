package org.y1000.entities.creatures.npc.AI;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.AttackableActiveEntity;
import org.y1000.entities.Direction;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.creatures.npc.NpcRangedSkill;
import org.y1000.entities.creatures.npc.ViolentNpc;
import org.y1000.util.Coordinate;

@Slf4j
public final class ViolentNpcRangedFightAI extends AbstractNpcFightAI {

    private static final int SPEEDRATE = 2;

    public ViolentNpcRangedFightAI(AttackableActiveEntity enemy, ViolentNpc npc) {
        super(enemy, npc, SPEEDRATE);
    }

    private void cooldownOrShoot(NpcRangedSkill rangedSkill) {
        if (npc.cooldown() > 0) {
            npc.startAction(State.COOLDOWN);
            return;
        }
        turnIfNotFaced();
        npc.startRangedAttack(getEnemy());
        rangedSkill.consumeProjectile();
    }


    private Coordinate computeEscapePoint(Npc escaping, Entity from) {
        Validate.notNull(escaping);
        Validate.notNull(from);
        Direction direction = from.coordinate().computeDirection(escaping.coordinate());
        var next = escaping.coordinate().moveBy(direction);
        if (escaping.realmMap().movable(next)) {
            return next;
        }
        return next.neighbours().stream().filter(p -> escaping.realmMap().movable(p))
                .findFirst().orElse(Coordinate.Empty);
    }

    private void rangedFightProcess(NpcRangedSkill rangedSkill) {
        var enemy = getEnemy();
        if (!rangedSkill.isAvailable()) {
            npc.changeAndStartAI(new ViolentNpcMeleeFightAI(enemy, npc, SPEEDRATE));
            return;
        }
        var dis = npc.coordinate().directDistance(enemy.coordinate());
        if (rangedSkill.isInRange(dis)) {
            cooldownOrShoot(rangedSkill);
        } else if (rangedSkill.isLtLower(dis)) {
            var next = computeEscapePoint(npc, enemy);
            if (next == Coordinate.Empty) {
                cooldownOrShoot(rangedSkill);
            } else {
                AiPathUtil.moveProcess(npc, next, getPrevious(), () -> cooldownOrShoot(rangedSkill), computeWalkMillis(), computeStayMillis());
            }
        } else {
            AiPathUtil.moveProcess(npc, enemy.coordinate(), getPrevious(), () -> cooldownOrShoot(rangedSkill), computeWalkMillis(), computeStayMillis());
        }
    }

    @Override
    protected void fightProcess() {
        npc.skill().ifPresentOrElse(this::rangedFightProcess,
                () -> npc.changeAndStartAI(new ViolentNpcMeleeFightAI(getEnemy(), npc)));
    }

    @Override
    protected boolean shouldChangeEnemy(AttackableActiveEntity newEnemy) {
        return true;
    }

}
