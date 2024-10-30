package org.y1000.persistence;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.io.Serializable;

@Data
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SlotKey implements Serializable {

    private long playerId;

    private int slot;

    @Enumerated(EnumType.STRING)
    private Type type;

    public enum Type {
        BANK,
        INVENTORY,
    }

    public static SlotKey inventory(long playerId, int slot) {
        return new SlotKey(playerId, slot, Type.INVENTORY);
    }

    public static SlotKey bank(long playerId, int slot) {
        return new SlotKey(playerId, slot, Type.BANK);
    }
}
