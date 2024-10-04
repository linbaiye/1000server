package org.y1000.entities.creatures.npc.AI;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.AttackableActiveEntity;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.entities.creatures.monster.Monster;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.creatures.npc.NpcHurtState;
import org.y1000.entities.creatures.npc.ViolentNpc;
import org.y1000.entities.creatures.npc.spell.CloneSpell;
import org.y1000.event.EntityEvent;
import org.y1000.event.EntityEventListener;
import org.y1000.message.SetPositionEvent;
import org.y1000.util.Coordinate;

@Slf4j
public abstract class AbstractNpcFightAI implements NpcAI, EntityEventListener {
    private AttackableActiveEntity enemy;

    protected final ViolentNpc npc;

    private Coordinate previous;

    private final int speedRate;

    public AbstractNpcFightAI(AttackableActiveEntity enemy,
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

    protected AttackableActiveEntity getEnemy() {
        return enemy;
    }

    protected abstract boolean shouldChangeEnemy(AttackableActiveEntity newEnemy);


    int computeWalkMillis() {
        int walkSpeed = npc.walkSpeed() / speedRate;
        var stateMillis = npc.getStateMillis(State.WALK);
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
        if (npc.state() instanceof NpcHurtState hurtState) {
            if (!hurtState.attacker().equals(enemy) &&
                    shouldChangeEnemy(hurtState.attacker())) {
                this.enemy = hurtState.attacker();
            }
        }
    }

    private void wanderOrFight() {
        if (npc.canChaseOrAttack(enemy)) {
            fightProcess();
        } else {
            npc.startIdleAI();
        }
    }


    @Override
    public void onActionDone(Npc npc) {
        if (npc.stateEnum() == State.DIE) {
            return;
        }
        if (npc.stateEnum() == State.WALK) {
            previous = npc.coordinate().moveBy(npc.direction().opposite());
            npc.stay(computeStayMillis());
            return;
        } else if (npc.stateEnum() == State.HURT) {
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
    public void onMoveFailed(Npc npc) {
        wanderOrFight();
    }

    @Override
    public void start(Npc npc) {
        npc.findSpell(CloneSpell.class).ifPresent(s -> s.castIfAvailable(npc, getEnemy()));
        wanderOrFight();
    }

    @Override
    public void onEvent(EntityEvent entityEvent) {
        if (entityEvent != null && enemy.equals(entityEvent.source()) && !enemy.canBeAttackedNow()) {
            enemy.deregisterEventListener(this);
            npc.changeToIdleAI();
        }
    }
}
