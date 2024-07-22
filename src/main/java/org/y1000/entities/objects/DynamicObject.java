package org.y1000.entities.objects;

import org.y1000.entities.AttackableEntity;

import java.util.Optional;

public interface DynamicObject extends AttackableEntity {
    DynamicObjectType type();

    String shape();

    Optional<String> name();
}
