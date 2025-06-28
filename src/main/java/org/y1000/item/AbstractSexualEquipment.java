package org.y1000.item;

import lombok.Getter;

import java.util.Set;

@Getter
public abstract class AbstractSexualEquipment extends AbstractEquipment implements SexualEquipment {

    private final boolean male;

    private final String sprite;
    private final String animation;

    public AbstractSexualEquipment(String name,
                                   ItemSdb itemSdb,
                                   Set<Object> abilities) {
        super(name, itemSdb, abilities);
        this.male = itemSdb.isMale(name);
        sprite = this.male ? computeMaleSprite(itemSdb) : computeFemaleSprite(itemSdb);
        this.animation = null;
    }

    private String computeFemaleSprite(ItemSdb itemSdb) {
        return (char)((int) 'a' + equipmentType().value()) + itemSdb.getWearShape(name());
    }

    private String computeMaleSprite(ItemSdb itemSdb) {
        return (char)((int) 'n' + equipmentType().value()) + itemSdb.getWearShape(name());
    }

    public static void main(String[] args) {
        var tmp = (char)((int) 'a' + EquipmentType.BOOT.value());
        System.out.println(tmp + "1");
    }


}
