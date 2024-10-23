package org.y1000.item;


public interface Equipment extends Item {

    EquipmentType equipmentType();

    default Long id() {
        return null;
    }

    default void setId(long id) { }
}
