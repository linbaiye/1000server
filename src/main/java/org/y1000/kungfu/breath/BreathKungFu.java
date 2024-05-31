package org.y1000.kungfu.breath;

import lombok.experimental.SuperBuilder;
import org.y1000.kungfu.AbstractKungFu;

@SuperBuilder
public class BreathKungFu extends AbstractKungFu {


    public static BreathKungFu unnamed() {
        return BreathKungFu.builder()
                .level(100)
                .name("无名心法")
                .build();
    }
}
