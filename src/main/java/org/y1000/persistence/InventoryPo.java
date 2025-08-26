package org.y1000.persistence;

import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.inventory.Inventory;



@Setter
@Getter
@Entity
@Table(name = "inventory")
@NoArgsConstructor
public class InventoryPo extends AbstractInventoryPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long playerId;

    public InventoryPo(Long id, long playerId) {
        this.id = id;
        this.playerId = playerId;
    }

    public static InventoryPo convert(long playerId, Inventory inventory) {
        Validate.notNull(inventory);
        var ret = new InventoryPo(null, playerId);
        ret.merge(inventory);
        return ret;
    }
}
