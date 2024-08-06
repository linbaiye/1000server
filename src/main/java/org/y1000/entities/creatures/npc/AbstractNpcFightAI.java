package org.y1000.entities.creatures.npc;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.AttackableActiveEntity;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.npc.spell.CloneSpell;
import org.y1000.message.SetPositionEvent;
import org.y1000.util.Coordinate;

@Slf4j
public abstract class AbstractNpcFightAI implements NpcAI {
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
    }

    protected void turnIfNotFaced() {
        Direction towards = npc.coordinate().computeDirection(enemy.coordinate());
        if (towards != npc.direction()) {
            npc.changeDirection(towards);
            npc.emitEvent(SetPositionEvent.of(npc));
        }
    }

    protected abstract void fightProcess();

    protected Coordinate getPrevious(){
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
        int walk = computeWalkMillis();
        var stateMillis = npc.getStateMillis(State.WALK);
        return Math.max(walk - stateMillis, 100);
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
            npc.changeAI(new ViolentNpcWanderingAI());
        }
    }


    @Override
    public void onActionDone(Npc npc) {
        if (npc.stateEnum() == State.WALK) {
            previous = npc.coordinate().moveBy(npc.direction().opposite());
            npc.stay(computeStayMillis());
            return;
        } else if (npc.stateEnum() == State.HURT) {
            npc.findSpell(CloneSpell.class).ifPresent(s -> s.castIfAvailable(npc, getEnemy()));
            tryChangeEnemy();
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
}
