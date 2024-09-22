package org.y1000.persistence;

import jakarta.persistence.*;
import lombok.*;
import org.y1000.kungfu.KungFu;

@Entity
@Table(name = "player_kung_fu")
@Builder
@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KungFuPo {


    @EmbeddedId
    private PlayerIdNameKey key;

    private int exp;

    private int slot;

    public static KungFuPo create(int slot, long playerId, KungFu kungFu) {
        return KungFuPo.builder()
                .slot(slot)
                .exp(kungFu.exp())
                .key(PlayerIdNameKey.builder()
                        .playerId(playerId)
                        .name(kungFu.name()).build())
                .build();
    }
}
