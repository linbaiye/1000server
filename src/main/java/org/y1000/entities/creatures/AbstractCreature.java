package org.y1000.entities.creatures;

import org.y1000.entities.AbstractActiveEntity;
import org.y1000.entities.Direction;
import org.y1000.entities.players.Damage;
import org.y1000.exp.ExperienceUtil;
import org.y1000.util.Coordinate;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public abstract class AbstractCreature extends AbstractActiveEntity implements Creature {

    private Coordinate coordinate;

    private Direction direction;

    private final String name;

    public AbstractCreature(long id,
                             Coordinate coordinate,
                             Direction direction,
                             String name) {
        super(id);
        this.name = name;
        this.direction = direction;
        this.coordinate = coordinate;
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
    public String viewName() {
        return name;
    }

    @Override
    public void changeDirection(Direction newdir) {
        direction = newdir;
    }

    @Override
    public void changeCoordinate(Coordinate newCoor) {
        coordinate = newCoor;
        realmMap().occupy(this);
    }
}
