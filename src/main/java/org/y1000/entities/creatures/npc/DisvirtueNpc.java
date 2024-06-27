package org.y1000.entities.creatures.npc;

import lombok.Builder;
import org.y1000.entities.Direction;
import org.y1000.entities.attribute.AttributeProvider;
import org.y1000.entities.attribute.Damage;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.CreatureDieEvent;
import org.y1000.entities.players.Player;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.Map;
import java.util.Set;

public final class DisvirtueNpc extends AbstractNpc<Npc, NpcState> implements Npc {

    private final WanderingNpcAI ai;

    @Builder
    public DisvirtueNpc(long id, Coordinate coordinate, Direction direction, String name, Map<State, Integer> stateMillis,
                        WanderingNpcAI ai, AttributeProvider attributeProvider,
                        RealmMap realmMap) {
        super(id, coordinate, direction, name, stateMillis, attributeProvider, realmMap);
        this.ai = ai;
    }

    @Override
    public void update(int delta) {
        state().update(this, delta);
    }

    @Override
    public boolean attackedBy(Player attacker) {
        var hit = doAttackedAndGiveExp(attacker.damage(), attacker.hit(), this::takeDamage, attacker::gainAttackExp);
        if (!hit) {
            return false;
        }
        if (currentLife() > 0) {
            state().moveToHurtCoordinate(this);
            changeState(new DisvirtueHurtState(getStateMillis(State.HURT)));
        } else {
            changeState(NpcDieState.die(getStateMillis(State.DIE)));
            emitEvent(new CreatureDieEvent(this));
        }
        return true;
    }

    @Override
    public Set<String> sellingItems() {
        return null;
    }

    @Override
    public Set<String> buyingItems() {
        return null;
    }

    @Override
    public void onActionDone() {
        switch (stateEnum()) {
            case IDLE -> ai.onIdleDone(this);
            case FROZEN -> ai.onFrozenDone(this);
            case HURT -> ai.onHurtDone(this);
        }
    }

    @Override
    public void revive(Coordinate coordinate) {
        doRevive(coordinate);
    }
}
