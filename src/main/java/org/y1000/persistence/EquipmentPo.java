package org.y1000.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.y1000.item.Equipment;

@Data
@Entity
@Table(name = "equipment")
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private long playerId;

    private String name;

    private int color;

    public static EquipmentPo convert(long id, Equipment equipment) {
        return new EquipmentPo(null, id, equipment.name(), equipment.color());
    }
}
