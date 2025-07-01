package org.y1000.entities.creatures;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.Direction;
import org.y1000.entities.players.Damage;
import org.y1000.util.Coordinate;
import org.y1000.util.UnaryAction;

import java.util.Map;
import java.util.Objects;

@Slf4j
public abstract class IAbstractCreature<C extends Creature, S extends ICreatureState<C>> extends AbstractCreature {

    private S state;

    private final Map<OldPlayerStateEnum, Integer> stateMillis;

    public IAbstractCreature(long id,
                             Coordinate coordinate,
                             Direction direction,
                             String name,
                             Map<OldPlayerStateEnum, Integer> stateMillis) {
        super(id, coordinate, direction, name);
        Objects.requireNonNull(coordinate, "coordinate can't be null.");
        Objects.requireNonNull(direction, "direction can't be null.");
        Objects.requireNonNull(name, "viewName can't be null.");
        Objects.requireNonNull(stateMillis, "stateMillis can't be null.");
        this.stateMillis = stateMillis;
    }


    public int getStateMillis(OldPlayerStateEnum playerStateEnum) {
        return stateMillis.get(playerStateEnum);
    }

    public S creatureState() {
        return state;
    }

    protected int getHurtAndGiveExp(Damage damage, int hit, UnaryAction<Damage> damageAction, UnaryAction<Integer> gainExp) {
        if (!creatureState().attackable() || isDodged(hit)) {
            return 0;
        }
        var before = currentLife();
        damageAction.invoke(damage);
        var damagedLife = before - currentLife();
        if (damagedLife > 0) {
            var exp = damagedLifeToExp(damagedLife);
            gainExp.invoke(exp);
        }
        return damagedLife;
    }


    public void changeState(S newState) {
        state = newState;
    }


    @Override
    public boolean canBeAttackedNow() {
        return creatureState().attackable();
    }

    public OldPlayerStateEnum oldStateEnum() {
        return creatureState().stateEnum();
    }

}
