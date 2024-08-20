package org.y1000.item;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemFactoryTest extends AbstractItemUnitTestFixture{

    @Test
    void createBoot() {
        var equipment = itemFactory.createBoot("女子皮鞋");
        assertEquals(EquipmentType.BOOT, equipment.equipmentType());
        assertInstanceOf(DyableArmorEquipment.class, equipment);
        assertFalse(equipment.isMale());
        equipment = itemFactory.createBoot("男子木屐");
        assertEquals(EquipmentType.BOOT, equipment.equipmentType());
        assertInstanceOf(ArmorEquipmentImpl.class, equipment);
        assertTrue(equipment.isMale());
    }

    @Test
    void createHair() {
        var equipment = itemFactory.createHair("女子长发");
        assertEquals(EquipmentType.HAIR, equipment.equipmentType());
        assertInstanceOf(DecorativeEquipment.class, equipment);
    }

    @Test
    void createChest() {
        var equipment = itemFactory.createChest("女子黄龙弓服");
        assertEquals(EquipmentType.CHEST, equipment.equipmentType());
        assertInstanceOf(ArmorEquipmentImpl.class, equipment);
        assertNotEquals(0, equipment.color());
    }

    @Test
    void createWrist() {
        var equipment = itemFactory.createWrist("女子黄龙手套");
        assertEquals(EquipmentType.WRIST, equipment.equipmentType());
        assertInstanceOf(ArmorEquipmentImpl.class, equipment);
        assertNotEquals(0, equipment.color());
    }

}
