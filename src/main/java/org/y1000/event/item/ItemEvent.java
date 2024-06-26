package org.y1000.event.item;

import org.y1000.event.EntityEvent;
import org.y1000.event.EntityEventVisitor;

public interface ItemEvent extends EntityEvent {

    void accept(ItemEventVisitor itemEventVisitor);


    default void accept(EntityEventVisitor visitor) {
        if (visitor instanceof ItemEventVisitor itemEventVisitor) {
            accept(itemEventVisitor);
        }
    }
}
