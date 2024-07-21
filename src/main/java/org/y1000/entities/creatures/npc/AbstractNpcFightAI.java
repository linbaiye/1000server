package org.y1000.entities.creatures.npc;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.AttackableEntity;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.message.SetPositionEvent;
import org.y1000.util.Coordinate;

public abstract class AbstractNpcFightAI implements NpcAI{
    private AttackableEntity enemy;

    protected final ViolentNpc npc;

    private Coordinate previous;

    public AbstractNpcFightAI(AttackableEntity enemy,
                              ViolentNpc npc) {
        Validate.notNull(enemy);
        Validate.notNull(npc);
        this.enemy = enemy;
        this.npc = npc;
        this.previous = Coordinate.Empty;
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

    protected AttackableEntity getEnemy() {
        return enemy;
    }

    protected abstract boolean shouldChangeEnemy(AttackableEntity newEnemy);


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
        } else if (npc.stateEnum() == State.HURT) {
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
        wanderOrFight();
    }
}
