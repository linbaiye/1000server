package org.y1000.persistence;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.y1000.kungfu.KungFu;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KungFuSlotPo {
    private int exp;
    private int slot;
    private String name;

    public static KungFuSlotPo convert(int slot, KungFu kungFu) {
        return new KungFuSlotPo(kungFu.exp(), slot, kungFu.name());
    }
}
