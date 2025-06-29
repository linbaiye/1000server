package org.y1000.item;

import lombok.Getter;

import java.util.Set;

@Getter
public abstract class AbstractSexualEquipment extends AbstractEquipment implements SexualEquipment {

    private final boolean male;

    public AbstractSexualEquipment(String name,
                                   ItemSdb itemSdb,
                                   Set<Object> abilities) {
        super(name, itemSdb, abilities);
        this.male = itemSdb.isMale(name);
    }


    public static void main(String[] args) {
        var tmp = (char)((int) 'a' + EquipmentType.BOOT.value());
        System.out.println(tmp + "1");
    }


}
