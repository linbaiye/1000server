package org.y1000.entities.creatures;

import org.y1000.entities.attribute.ArmorAttribute;
import org.y1000.entities.attribute.HarhAttribute;
import org.y1000.entities.Direction;
import org.y1000.entities.Entity;
import org.y1000.entities.attribute.DamageAttribute;

public interface Creature extends Entity {

    Direction direction();

    State stateEnum();

    void changeDirection(Direction newDirection);

    String name();

    default HarhAttribute harhAttribute() {
        return HarhAttribute.DEFAULT;
    }

    default ArmorAttribute amorArribute() {
        return ArmorAttribute.DEFAULT;
    }

    default DamageAttribute damageAttribute() {
        return DamageAttribute.DEFAULT;
    }
}
