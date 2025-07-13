package org.y1000.entities.creatures.npc;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.Direction;
import org.y1000.util.Coordinate;

@Slf4j
public final class EscapeAI extends AbstractMovableNpcAI {
    private ActiveEntity enemy;

    private Coordinate destination;

    public EscapeAI(Npc npc, ActiveEntity entity, NpcAbility ability) {
        super(npc);
        changeAbility(ability);
        npc.findAbility(NpcHurtAbility.class).ifPresent(hurtAbility -> hurtAbility.setHurtTrigger(this::onAttacked));
        this.enemy = entity;
        computeEscapePoint();
    }

    private void onAttacked(ActiveEntity entity, NpcHurtAbility hurtAbility) {
        applyHurtAbility(hurtAbility);
        enemy = entity;
        computeEscapePoint();
    }

    private Coordinate computeByDirection(Direction direction) {
        Coordinate coordinate = npc().coordinate();
        Coordinate dest = coordinate.move(direction.xVector() * npc().viewRange(), direction.yVector() * npc().viewRange());
        return npc().getRealmMap().movable(dest) ? dest : null;
    }

    private void computeEscapePoint() {
        Direction bestDirection = enemy.coordinate().computeDirection(npc().coordinate());
        Coordinate target = computeByDirection(bestDirection);
        if (target != null) {
            destination = target;
            return;
        }
        for (Direction direction: Direction.values()) {
            var tmp = computeByDirection(direction);
            if (tmp != null) {
                destination = tmp;
                return;
            }
        }
    }

    @Override
    void onMoveFailed() {
        computeEscapePoint();
        changeAbilityOrThrow(NpcIdleAbility.class).apply(npc());
    }

    @Override
    void onNonDieAbilityDone(NpcAbility ability) {
        computePrevious();
        if (destination == null) {
            computeEscapePoint();
            log.debug("No point.");
            changeAbilityOrThrow(NpcIdleAbility.class).apply(npc());
            return;
        }
        if (npc().coordinate().directDistance(enemy.coordinate()) >= npc().viewRange()) {
            WanderingAI newAi = new WanderingAI(npc());
            npc().startAI(newAi);
            log.debug("Wandering again.");
            return;
        }
        if (ability instanceof NpcMoveAbility moveAbility && moveAbility.idleTime() > 0) {
            changeAbilityOrThrow(NpcIdleAbility.class).apply(npc(), moveAbility.idleTime());
        } else {
            moveCloser(destination);
            log.debug("keep moving");
        }
    }

    @Override
    public void update(int delta) {
        updateAbility(delta);
    }


    @Override
    public void start() {
        npc().findAbility(NpcMoveAbility.class).ifPresent(NpcMoveAbility::enableFastMove);
    }
}
