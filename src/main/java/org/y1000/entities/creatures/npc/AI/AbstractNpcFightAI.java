package org.y1000.entities.creatures.npc.AI;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.AttackableEntity;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.entities.creatures.monster.Monster;
import org.y1000.entities.creatures.monster.NpcAnimationEnum;
import org.y1000.entities.creatures.npc.INpc;
import org.y1000.entities.creatures.npc.NpcHurtState;
import org.y1000.entities.creatures.npc.ViolentNpc;
import org.y1000.entities.creatures.npc.spell.CloneSpell;
import org.y1000.event.EntityEvent;
import org.y1000.event.EntityEventListener;
import org.y1000.message.SetPositionEvent;
import org.y1000.util.Coordinate;

@Slf4j
@Deprecated
public abstract class AbstractNpcFightAI implements INpcAI, EntityEventListener {
    private AttackableEntity enemy;

    protected final ViolentNpc npc;

    private Coordinate previous;

    private final int speedRate;

    public AbstractNpcFightAI(AttackableEntity enemy,
                              ViolentNpc npc, int speedRate) {
        Validate.isTrue(speedRate > 0);
        Validate.notNull(enemy);
        Validate.notNull(npc);
        this.enemy = enemy;
        this.npc = npc;
        this.previous = Coordinate.Empty;
        this.speedRate = speedRate;
        enemy.registerEventListener(this);
    }

    protected void turnIfNotFaced() {
        Direction towards = npc.coordinate().computeDirection(enemy.coordinate());
        if (towards != npc.direction()) {
            npc.changeDirection(towards);
            npc.emitEvent(SetPositionEvent.of(npc));
        }
    }

    protected abstract void fightProcess();

    protected Coordinate getPrevious() {
        return previous;
    }

    protected AttackableEntity getEnemy() {
        return enemy;
    }

    protected abstract boolean shouldChangeEnemy(AttackableEntity newEnemy);


    int computeWalkMillis() {
        int walkSpeed = npc.walkSpeed() / speedRate;
        var stateMillis = npc.getStateMillis(NpcAnimationEnum.Move);
        if (walkSpeed > stateMillis) {
            return stateMillis;
        }
        return Math.max(walkSpeed, 100);
    }

    int computeStayMillis() {
        int speed = npc.walkSpeed() / speedRate;
        int walk = computeWalkMillis();
        return Math.max(speed - walk, 100);
    }


    private void tryChangeEnemy() {
        if (npc.npcState() instanceof NpcHurtState hurtState) {
//            if (!hurtState.attacker().equals(enemy) &&
//                    shouldChangeEnemy(hurtState.attacker())) {
//                this.enemy = hurtState.attacker();
//            }
        }
    }

    protected abstract void onFightDone(INpc npc);

    private void wanderOrFight() {
        if (npc.canChaseOrAttack(enemy)) {
            fightProcess();
        } else {
            onFightDone(npc);
        }
    }


    @Override
    public void onActionDone(INpc npc) {
        if (npc.isDead()) {
            return;
        }
        if (npc.isMoving()) {
            previous = npc.coordinate().moveBy(npc.direction().opposite());
            npc.stay(computeStayMillis());
            return;
        } else if (npc.npcStateEnum() == NpcAnimationEnum.Hurt) {
            npc.findSpell(CloneSpell.class).ifPresent(s -> s.castIfAvailable(npc, getEnemy()));
            tryChangeEnemy();
            if (npc instanceof Monster monster && getEnemy() instanceof ViolentCreature violentCreature) {
                if (monster.escapeLife() > monster.currentLife()) {
                    monster.changeAndStartAI(new EscapeAI(violentCreature));
                    return;
                }
            }
        }
        wanderOrFight();
    }

    @Override
    public void onMoveFailed(INpc npc) {
        wanderOrFight();
    }

    @Override
    public void start(INpc npc) {
        npc.findSpell(CloneSpell.class).ifPresent(s -> s.castIfAvailable(npc, getEnemy()));
        wanderOrFight();
    }

    @Override
    public void onEvent(EntityEvent entityEvent) {
        if (entityEvent != null && enemy.equals(entityEvent.source()) && !enemy.canBeAttackedNow()) {
            enemy.deregisterEventListener(this);
            onFightDone(npc);
        }
    }
}
