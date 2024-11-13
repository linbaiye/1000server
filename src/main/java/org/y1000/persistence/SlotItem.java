package org.y1000.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.Validate;
import org.y1000.item.Equipment;
import org.y1000.item.Item;
import org.y1000.item.StackItem;

import java.io.Serializable;

@Data
public class SlotItem implements Serializable {
    private static final String ITEM_TYPE = "I";
    private static final String EQUIP_TYPE = "E";
    private int slot;
    // 'I' for item, name, number, and color are then used.
    // 'E' for equipment, equipmentId is used.
    private String type;
    private String name;
    private int color;
    private Long number;
    private Long equipmentId;

    private static SlotItem ofEquipment(int slot, Equipment equipment) {
        Validate.notNull(equipment.id());
        var slotItem = new SlotItem();
        slotItem.setSlot(slot);
        slotItem.setType(EQUIP_TYPE);
        slotItem.setEquipmentId(equipment.id());
        return slotItem;
    }

    private static SlotItem ofItem(int slot, Item item) {
        var slotItem = new SlotItem();
        slotItem.setSlot(slot);
        slotItem.setType(ITEM_TYPE);
        slotItem.setName(item.name());
        long number = 1;
        if (item instanceof StackItem stackItem)
            number = stackItem.number();
        slotItem.setNumber(number);
        slotItem.setColor(item.color());
        return slotItem;
    }

    @JsonIgnore
    public boolean isEquipment() {
        return EQUIP_TYPE.equals(type);
    }

    public static SlotItem of(int slot, Item item) {
        Validate.notNull(item);
        if (item instanceof Equipment equipment) {
            return ofEquipment(slot, equipment);
        } else {
            return ofItem(slot, item);
        }
    }
}
