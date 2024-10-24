package org.y1000.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.y1000.item.Equipment;
import org.y1000.item.Upgradable;

@Data
@Entity
@Table(name = "equipment")
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int level;

    private int color;

    public static EquipmentPo convert(Equipment equipment) {
        int level = equipment.findAbility(Upgradable.class).map(Upgradable::level).orElse(0);
        return new EquipmentPo(equipment.id(), equipment.name(), level, equipment.color());
    }
}
