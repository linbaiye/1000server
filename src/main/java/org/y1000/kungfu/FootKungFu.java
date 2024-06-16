package org.y1000.kungfu;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public final class FootKungFu extends AbstractKungFu {

    public boolean canFly() {
        return level() >= 8501;
    }

    @Override
    public KungFuType kungFuType() {
        return KungFuType.FOOT;
    }

}
