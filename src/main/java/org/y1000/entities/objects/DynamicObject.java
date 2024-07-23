package org.y1000.entities.objects;

import org.y1000.entities.AttackableEntity;
import org.y1000.util.Coordinate;

import java.util.Optional;
import java.util.Set;

public interface DynamicObject extends AttackableEntity {
    DynamicObjectType type();

    String shape();

    String idName();

    Optional<String> viewName();
    
    Set<Coordinate> occupyingCoordinates();

    Animation currentAnimation();

}
