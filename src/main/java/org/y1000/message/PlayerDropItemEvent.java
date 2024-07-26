package org.y1000.message;

import org.y1000.entities.ActiveEntity;
import org.y1000.entities.GroundedItem;
import org.y1000.entities.players.Player;
import org.y1000.event.item.ItemEvent;
import org.y1000.event.item.ItemEventVisitor;
import org.y1000.util.Coordinate;

public final class PlayerDropItemEvent implements ItemEvent {

    private final String droppedItemName;

    private final Integer numberOnGround;

    private final Coordinate coordinate;

    private final Player source;

    public PlayerDropItemEvent(Player source,
                               String droppedItemName,
                               Integer groundNumber, Coordinate coordinate) {
        this.droppedItemName = droppedItemName;
        this.numberOnGround = groundNumber;
        this.coordinate = coordinate;
        this.source = source;
    }


    public GroundedItem createGroundedItem(long id) {
        GroundedItem.GroundedItemBuilder builder = GroundedItem.builder()
                .coordinate(coordinate)
                .name(droppedItemName)
                .id(id)
                .number(numberOnGround)
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
