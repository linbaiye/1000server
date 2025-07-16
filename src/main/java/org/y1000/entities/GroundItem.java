package org.y1000.entities;

import org.y1000.item.Item;
import org.y1000.item.StackItem;
import org.y1000.message.GroundItemSnapshot;
import org.y1000.message.I2ClientMessage;
import org.y1000.realm.GroundItemEventListener;
import org.y1000.util.Coordinate;

import java.util.Objects;
import java.util.Optional;

public class GroundItem extends AbstractActiveEntity {

    private final Item item;

    private final Coordinate coordinate;

    private final GroundItemEventListener eventListener;

    private long ttl = 3 * 60 * 1000;

    public GroundItem(long id,
                      Item item,
                      Coordinate coordinate,
                      GroundItemEventListener eventListener) {
        super(id);
        this.item = item;
        this.coordinate = coordinate;
        this.eventListener = eventListener;
    }

    @Override
    public void update(int delta) {
        if (ttl <= 0) {
            return;
        }
        ttl -= delta;
        if (ttl <= 0) {
//            eventListener.sendEvent();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroundItem item = (GroundItem) o;
        return item.id() == id();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id());
    }

    @Override
    public <AB> Optional<AB> findAbility(Class<AB> type) {
        return Optional.empty();
    }

    @Override
    public Coordinate coordinate() {
        return coordinate;
    }

    @Override
    public I2ClientMessage captureSnapshot() {
        return GroundItemSnapshot.builder()
                .name(item.name())
                .number((item instanceof StackItem stackItem) ? (int)stackItem.number() : 1)
                .id(id())
                .coordinate(coordinate)
                .color(item.color())
                .icon(item.icon())
                .build();
    }
}
