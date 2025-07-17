package org.y1000.entities;

import lombok.Getter;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.PlayerTextMessage;
import org.y1000.item.Item;
import org.y1000.item.StackItem;
import org.y1000.message.GroundItemSnapshot;
import org.y1000.message.I2ClientMessage;
import org.y1000.util.Coordinate;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class GroundItem extends AbstractActiveEntity {

    @Getter
    private final Item item;

    private final Coordinate coordinate;


    private final Consumer<? super GroundItem> remover;

    private long ttl = 3 * 60 * 1000;

    public GroundItem(long id,
                      Item item,
                      Coordinate coordinate,
                      Consumer<? super GroundItem> remover) {
        super(id);
        this.item = item;
        this.coordinate = coordinate;
        this.remover = remover;
    }

    @Override
    public void update(int delta) {
        if (ttl <= 0) {
            return;
        }
        ttl -= delta;
        if (ttl <= 0) {
            remover.accept(this);
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

    private boolean canPickAt(Coordinate from) {
        return coordinate().xDistance(from.x()) <= 2 && coordinate().yDistance(from.y()) <= 3;
    }

    public void pickedBy(Player player) {
        if (player == null)
            return;
        if (!canPickAt(player.coordinate())) {
            player.sendEvent(PlayerTextMessage.of(player, "距离过远。"));
            return;
        }
        if (player.pickItem(item))
            remover.accept(this);
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
