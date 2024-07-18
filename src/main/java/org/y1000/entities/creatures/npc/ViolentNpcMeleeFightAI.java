package org.y1000.entities.creatures.npc;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.AttackableEntity;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.AiPathUtil;
import org.y1000.entities.creatures.State;
import org.y1000.event.EntityEvent;
import org.y1000.event.EntityEventListener;
import org.y1000.message.SetPositionEvent;
import org.y1000.util.Coordinate;

@Slf4j
public final class ViolentNpcMeleeFightAI implements NpcAI, EntityEventListener {
    private final AttackableEntity enemy;

    private final ViolentNpc npc;

    private Coordinate previous;

    public ViolentNpcMeleeFightAI(AttackableEntity enemy,
                                  ViolentNpc npc) {
        Validate.notNull(enemy);
        Validate.notNull(npc);
        this.enemy = enemy;
        this.npc = npc;
        enemy.registerEventListener(this);
        this.previous = Coordinate.Empty;
        log.debug("{} now fighting entity {}.", npc.id(), enemy.id());
    }


    private void meleeAttackProcess() {
        if (!npc.canChaseOrAttack(enemy)) {
            npc.changeAI(new ViolentNpcWanderingAI());
            return;
        }
        if (npc.coordinate().directDistance(enemy.coordinate()) > 1) {
            AiPathUtil.moveProcess(npc, enemy.coordinate(), previous, () -> npc.startAction(State.IDLE), npc.getStateMillis(State.WALK));
            return;
        }
        Direction towards = npc.coordinate().computeDirection(enemy.coordinate());
        if (towards != npc.direction()) {
            npc.changeDirection(towards);
            npc.emitEvent(SetPositionEvent.of(npc));
        }
        if (npc.cooldown() > 0) {
            npc.startAction(State.COOLDOWN);
        } else {
            npc.startAction(State.ATTACK);
            enemy.attackedBy(npc);
        }
    }


    @Override
    public void onActionDone(Npc npc) {
        if (npc.stateEnum() == State.WALK) {
            previous = npc.coordinate().moveBy(npc.direction().opposite());
        }
        meleeAttackProcess();
    }

    @Override
    public void onMoveFailed(Npc npc) {
        meleeAttackProcess();
    }

    @Override
    public void start(Npc npc) {
        meleeAttackProcess();
    }

    @Override
    public void onEvent(EntityEvent entityEvent) {
        if (npc.stateEnum() == State.DIE || !entityEvent.source().equals(enemy)) {
            return;
        }
        if (!npc.canChaseOrAttack(entityEvent.source())) {
            log.debug("Entity {} not attackable.", entityEvent.source().id());
            npc.changeAI(new ViolentNpcWanderingAI());
        }
    }
}
