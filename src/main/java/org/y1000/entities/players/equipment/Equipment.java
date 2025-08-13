package org.y1000.entities.players.equipment;


import org.y1000.item.Item;

import java.util.Optional;

public interface Equipment extends Item {

    EquipmentType equipmentType();

    default Long id() {
        return null;
    }

    default void setId(long id) { }

    default <T> Optional<T> findAbility(Class<T> type) {
        return Optional.empty();
    }

    String wearShape();
}
