package org.y1000.item;

import org.junit.jupiter.api.Test;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.entities.GroundedItem;
import org.y1000.util.Coordinate;

import static org.junit.jupiter.api.Assertions.*;

class ItemFactoryTest extends AbstractUnitTestFixture {
    private final ItemFactory itemFactory = createItemFactory();

    @Test
    void createBoot() {
        var equipment = itemFactory.createBoot("女子皮鞋");
        assertEquals(EquipmentType.BOOT, equipment.equipmentType());
        assertFalse(equipment.isMale());
        equipment = itemFactory.createBoot("男子木屐");
        assertEquals(EquipmentType.BOOT, equipment.equipmentType());
        assertTrue(equipment.isMale());
    }

    @Test
    void createHair() {
        var equipment = itemFactory.createHair("女子长发");
        assertEquals(EquipmentType.HAIR, equipment.equipmentType());
    }

    @Test
    void createChest() {
        var equipment = itemFactory.createChest("女子黄龙弓服");
        assertEquals(EquipmentType.CHEST, equipment.equipmentType());
        assertNotEquals(0, equipment.color());
    }

    @Test
    void createWrist() {
        var equipment = itemFactory.createWrist("女子黄龙手套");
        assertEquals(EquipmentType.WRIST, equipment.equipmentType());
        assertNotEquals(0, equipment.color());
    }

    @Test
    void createFromGroundItem() {
        var equip = itemFactory.createEquipment("女子长发");
        equip.findAbility(Dyable.class).ifPresent(d -> d.dye(100));
        GroundedItem groundedItem = new GroundedItem(1L, equip.name(), Coordinate.xy(1,1), 1, "", equip.color());
        Item item = itemFactory.createItem(groundedItem);
        assertEquals(100, item.color());
    }
}
