package org.y1000.kungfu.protect;

import lombok.experimental.SuperBuilder;
import org.y1000.kungfu.AbstractKungFu;
import org.y1000.kungfu.KungFuType;

@SuperBuilder
public class ProtectKungFu extends AbstractKungFu {
    private int bodyArmor;
    private int headArmor;
    private int armArmor;
    private int legArmor;

    public static ProtectKungFu unnamed() {
        return ProtectKungFu.builder()
                .name("无名强身")
                .level(100)
                .build();
    }

    @Override
    public KungFuType kungFuType() {
        return KungFuType.PROTECTION;
    }
}
