package org.y1000.entities.creatures.npc;

import org.y1000.entities.AttackableEntity;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.AiPathUtil;
import org.y1000.entities.creatures.State;
import org.y1000.util.Coordinate;

public final class ViolentNpcRangedFightAI extends AbstractNpcFightAI {

    public ViolentNpcRangedFightAI(AttackableEntity enemy, ViolentNpc npc) {
        super(enemy, npc);
    }

    private Coordinate computeEscapePoint() {
        Direction direction = getEnemy().coordinate().computeDirection(npc.coordinate());
        var next = npc.coordinate().moveBy(direction);
        if (npc.realmMap().movable(next)) {
            return next;
        }
        return next.neighbours().stream().filter(p -> npc.realmMap().movable(p))
                .findFirst().orElse(Coordinate.Empty);
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

    private void rangedFightProcess(NpcRangedSkill rangedSkill) {
        var enemy = getEnemy();
        if (!rangedSkill.isAvailable()) {
            npc.changeAI(new ViolentNpcMeleeFightAI(enemy, npc));
            return;
        }
        var dis = npc.coordinate().directDistance(enemy.coordinate());
        if (rangedSkill.isInRange(dis)) {
            cooldownOrShoot(rangedSkill);
        } else if (rangedSkill.isLtLower(dis)) {
            var next = computeEscapePoint();
            if (next == Coordinate.Empty) {
                cooldownOrShoot(rangedSkill);
            } else {
                AiPathUtil.moveProcess(npc, next, getPrevious(), () -> cooldownOrShoot(rangedSkill), npc.walkSpeedInFight());
            }
        } else {
            AiPathUtil.moveProcess(npc, enemy.coordinate(), getPrevious(), () -> cooldownOrShoot(rangedSkill), npc.walkSpeedInFight());
        }
    }

    @Override
    protected void fightProcess() {
        npc.skill().ifPresentOrElse(this::rangedFightProcess,
                () -> npc.changeAI(new ViolentNpcMeleeFightAI(getEnemy(), npc)));
    }

    @Override
    protected boolean shouldChangeEnemy(AttackableEntity newEnemy) {
        return true;
    }
}
