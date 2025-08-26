package org.y1000.item;

import lombok.Builder;
import org.y1000.kungfu.KungFu;

public final class KungFuItem extends AbstractItem {
    private final KungFu kungFu;
    @Builder
    public KungFuItem(String name,
                      String dropSound,
                      String eventSound,
                      String desc,
                      KungFu kungFu,
                      int icon) {
        super(name, ItemType.KUNGFU, dropSound, eventSound, desc, icon);
        this.kungFu = kungFu;
    }

    public KungFu kungFu() {
        return kungFu;
    }
}
