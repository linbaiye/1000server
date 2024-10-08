package org.y1000.message;

import lombok.Getter;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.GroundedItem;
import org.y1000.entities.players.Player;
import org.y1000.event.item.ItemEvent;
import org.y1000.event.item.ItemEventVisitor;
import org.y1000.util.Coordinate;

public final class PlayerDropItemEvent implements ItemEvent {

    @Getter
    private final String droppedItemName;

    @Getter
    private final Integer numberOnGround;

    @Getter
    private final Coordinate coordinate;

    private final Player source;

    private final int color;

    public PlayerDropItemEvent(Player source,
                               String droppedItemName,
                               Integer groundNumber,
                               Coordinate coordinate,
                               int color) {
        this.droppedItemName = droppedItemName;
        this.numberOnGround = groundNumber;
        this.coordinate = coordinate;
        this.source = source;
        this.color = color;
    }

    public GroundedItem createGroundedItem(long id) {
        GroundedItem.GroundedItemBuilder builder = GroundedItem.builder()
                .coordinate(coordinate)
                .name(droppedItemName)
                .id(id)
                .number(numberOnGround)
                .color(color)
                ;
        return builder.build();
    }

    @Override
    public ActiveEntity source() {
        return source;
    }

    @Override
    public void accept(ItemEventVisitor itemEventVisitor) {
        itemEventVisitor.visit(this);
    }
}
