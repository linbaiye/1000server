package org.y1000.entities.players.kungfu.protect;

import lombok.experimental.SuperBuilder;
import org.y1000.entities.players.kungfu.AbstractKungFu;

@SuperBuilder
public class ProtectKungFu extends AbstractKungFu {

    public static ProtectKungFu unnamed() {
        return ProtectKungFu.builder()
                .name("无名强身")
                .level(100)
                .build();
    }
}
