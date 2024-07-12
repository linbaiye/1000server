package org.y1000.item;

import lombok.Builder;
import org.y1000.kungfu.KungFu;

public final class KungFuItem extends AbstractStackItem {
    private final KungFu kungFu;
    @Builder
    public KungFuItem(String name,
                      long number,
                      String dropSound,
                      String eventSound,
                      String desc,
                      KungFu kungFu) {
        super(name, number, ItemType.KUNGFU, dropSound, eventSound, desc);
        this.kungFu = kungFu;
    }

    public KungFu kungFu() {
        return kungFu;
    }
}
