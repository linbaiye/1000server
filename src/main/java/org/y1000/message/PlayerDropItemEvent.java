package org.y1000.message;

import org.y1000.entities.GroundedItem;
import org.y1000.entities.PhysicalEntity;
import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.EntityEvent;
import org.y1000.message.serverevent.EntityEventVisitor;
import org.y1000.util.Coordinate;

public final class PlayerDropItemEvent implements EntityEvent {

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
    public PhysicalEntity source() {
        return source;
    }

    @Override
    public void accept(EntityEventVisitor visitor) {
        visitor.visit(this);
    }
}
