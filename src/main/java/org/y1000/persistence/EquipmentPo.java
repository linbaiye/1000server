package org.y1000.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.Validate;
import org.y1000.item.Equipment;
import org.y1000.item.Upgradable;

@Entity
@Table(name = "equipment")
@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
public class EquipmentPo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int color;

    private int level;

    public void merge(Equipment equipment) {
        Validate.notNull(equipment);
        level = equipment.findAbility(Upgradable.class)
                .map(Upgradable::level)
                .orElse(0);
        color = equipment.color();
    }

    public static EquipmentPo convert(Equipment equipment) {
        int level = equipment.findAbility(Upgradable.class).map(Upgradable::level).orElse(0);
        return new EquipmentPo(equipment.id(), equipment.name(), equipment.color(), level);
    }
}
