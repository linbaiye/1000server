package org.y1000.entities.creatures.npc;

import org.apache.commons.lang3.StringUtils;
import org.y1000.entities.Direction;
import org.y1000.entities.attribute.AttributeProvider;
import org.y1000.entities.attribute.Damage;
import org.y1000.entities.creatures.*;
import org.y1000.message.AbstractCreatureInterpolation;
import org.y1000.message.CreatureInterpolation;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;
import org.y1000.util.Rectangle;

import java.util.Map;
import java.util.Optional;

public abstract class AbstractNpc<C extends Creature, S extends CreatureState<C>>
        extends AbstractCreature<C, S> implements Npc {

    private final AttributeProvider attributeProvider;

    private final RealmMap realmMap;

    private int currentLife;

    private Coordinate spwanCoordinate;

    private Rectangle wanderingArea;

    public AbstractNpc(long id,
                       Coordinate coordinate,
                       Direction direction,
                       String name,
                       Map<State, Integer> stateMillis,
                       AttributeProvider attributeProvider,
                       RealmMap realmMap) {
        super(id, coordinate, direction, name, stateMillis);
        this.attributeProvider = attributeProvider;
        this.realmMap = realmMap;
        doRevive(coordinate);
    }

    @Override
    public AbstractCreatureInterpolation captureInterpolation() {
        return new CreatureInterpolation(id(), coordinate(), state().stateEnum(), direction(), state().elapsedMillis(), name());
    }


    protected void doRevive(Coordinate coordinate) {
        int range = attributeProvider.wanderingRange();
        spwanCoordinate = coordinate;
        changeCoordinate(spwanCoordinate);
        wanderingArea = new Rectangle(coordinate.move(-range, -range), coordinate.move(range, range));
        currentLife = attributeProvider.life();
    }

    protected AttributeProvider attributeProvider() {
        return attributeProvider;
    }

    @Override
    public Rectangle wanderingArea() {
        return wanderingArea;
    }

    @Override
    public Coordinate spawnCoordinate() {
        return spwanCoordinate;
    }

    @Override
    public int avoidance() {
        return attributeProvider.avoidance();
    }

    @Override
    public int maxLife() {
        return attributeProvider.life();
    }

    @Override
    public int currentLife() {
        return currentLife;
    }

    @Override
    public int bodyArmor() {
        return attributeProvider.armor();
    }


    protected void consumeLife(int nr) {
        currentLife = Math.max(currentLife - nr, 0);
    }


    protected void takeDamage(Damage damage) {
        int bodyDamage = damage.bodyDamage() - bodyArmor();
        bodyDamage = bodyDamage > 0 ? bodyDamage : 1;
        currentLife = currentLife > bodyDamage ? currentLife - bodyDamage : 0;
    }

    @Override
    public RealmMap realmMap() {
        return realmMap;
    }

    @Override
    public Optional<String> hurtSound() {
        return StringUtils.isEmpty(attributeProvider.hurtSound()) ? Optional.empty() :
                Optional.of(attributeProvider.hurtSound());
    }

    public Optional<String> normalSound() {
        return attributeProvider.normalSound();
    }

    @Override
    public Optional<String> dieSound() {
        return attributeProvider.dieSound();
    }
}
