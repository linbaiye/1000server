package org.y1000.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "player_inventory_slot")
@NoArgsConstructor
@AllArgsConstructor
public class PlayerInventorySlot {

    @Id
    private SlotKey slotKey;

    private long itemId;
}
