package org.y1000.persistence;

import jakarta.persistence.*;
import lombok.*;
import org.y1000.exp.Experience;
import org.y1000.kungfu.KungFu;

import java.io.Serializable;

@Entity
@Table(name = "kung_fu")
@Builder
@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KungFuPo {


    @EmbeddedId
    private KungFuKey key;

    private int exp;

    private int slot;

    @Data
    @Builder
    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class KungFuKey implements Serializable {

        private long playerId;

        private String name;
    }

    public static KungFuPo create(int slot, long playerId, KungFu kungFu) {
        return KungFuPo.builder()
                .slot(slot)
                .exp(kungFu.exp())
                .key(KungFuKey.builder()
                        .playerId(playerId)
                        .name(kungFu.name()).build())
                .build();
    }
}
