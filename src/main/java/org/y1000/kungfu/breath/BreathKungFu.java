package org.y1000.kungfu.breath;

import lombok.Builder;
import lombok.experimental.SuperBuilder;
import org.y1000.kungfu.AbstractKungFu;
import org.y1000.kungfu.KungFuType;

public class BreathKungFu extends AbstractKungFu {


    @Builder

    public BreathKungFu(String name, int exp) {
        super(name, exp);
    }

    @Override
    public KungFuType kungFuType() {
        return KungFuType.BREATHING;
    }
}
