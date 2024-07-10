package org.y1000.entities.creatures;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.AbstractEntity;
import org.y1000.entities.Direction;
import org.y1000.entities.players.Damage;
import org.y1000.exp.ExperienceUtil;
import org.y1000.util.Coordinate;
import org.y1000.util.UnaryAction;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public abstract class AbstractCreature<C extends Creature, S extends CreatureState<C>> extends AbstractEntity implements Creature {

    private Coordinate coordinate;

    private Direction direction;

    private final String name;

    private S state;

    private final Map<State, Integer> stateMillis;

    public AbstractCreature(long id,
                            Coordinate coordinate,
                            Direction direction,
                            String name,
                            Map<State, Integer> stateMillis) {
        super(id);
        Objects.requireNonNull(coordinate, "coordinate can't be null.");
        Objects.requireNonNull(direction, "direction can't be null.");
        Objects.requireNonNull(name, "name can't be null.");
        Objects.requireNonNull(stateMillis, "stateMillis can't be null.");
        this.coordinate = coordinate;
        this.direction = direction;
        this.name = name;
        this.stateMillis = stateMillis;
    }

    public void changeDirection(Direction newdir) {
        direction = newdir;
    }

    public void changeCoordinate(Coordinate newCoor) {
        coordinate = newCoor;
    }

    public int getStateMillis(State state) {
        return stateMillis.get(state);
    }

    public S state() {
        return state;
    }

    protected boolean randomAvoidance(int attackerHit) {
        var rand = ThreadLocalRandom.current().nextInt(attackerHit + 75 + avoidance());
        return rand < avoidance();
    }

    @Override
    public Coordinate coordinate() {
        return coordinate;
    }

    @Override
    public Direction direction() {
        return direction;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public State stateEnum() {
        return state().stateEnum();
    }

    public void changeState(S newState) {
        state = newState;
    }

    protected boolean doAttackedAndGiveExp(Damage damage, int hit, UnaryAction<Damage> damageAction, UnaryAction<Integer> gainExp) {
        if (!state().attackable() || randomAvoidance(hit)) {
            return false;
        }
        var before = currentLife();
        damageAction.invoke(damage);
        var damagedLife = before - currentLife();
        if (damagedLife > 0) {
            var exp = damagedLifeToExp(damagedLife);
            gainExp.invoke(exp);
        }
        return true;
    }


    @Override
    public boolean canBeAttackedNow() {
        return state().attackable();
    }

    protected int damagedLifeToExp(int damagedLife) {
        var n = maxLife() / damagedLife;
        return n > 15 ? ExperienceUtil.DEFAULT_EXP : ExperienceUtil.DEFAULT_EXP * n * n / (15 * 15);
    }
}
