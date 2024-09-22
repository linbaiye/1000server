package org.y1000.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.y1000.item.Equipment;

@Data
@Entity
@Table(name = "player_equipment")
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentPo {

    @EmbeddedId
    private PlayerIdNameKey key;

    private int color;

    public String getName() {
        return key.getName();
    }

    public static EquipmentPo convert(long id, Equipment equipment) {
        return new EquipmentPo(new PlayerIdNameKey(id, equipment.name()), equipment.color());
    }
}
