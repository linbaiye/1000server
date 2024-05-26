package org.y1000.message;

import org.y1000.entities.GroundedItem;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.AbstractPlayerEvent;
import org.y1000.message.clientevent.ClientDropItemEvent;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.DropItemConfirmPacket;
import org.y1000.network.gen.Packet;
import org.y1000.util.Coordinate;

public final class PlayerDropItemEvent extends AbstractPlayerEvent {

    private final ClientDropItemEvent clientDropItemEvent;

    private final String droppedItemName;

    private final Integer numberOnGround;

    private final int numberLeft;

    public PlayerDropItemEvent(Player source, ClientDropItemEvent clientDropItemEvent,
                               String droppedItemName,
                               Integer ground, int numberLeft) {
        super(source);
        this.clientDropItemEvent = clientDropItemEvent;
        this.droppedItemName = droppedItemName;
        numberOnGround = ground;
        this.numberLeft = numberLeft;
    }


    public GroundedItem createGroundedItem(long id) {
        Coordinate coordinate = clientDropItemEvent.coordinate();
        GroundedItem.GroundedItemBuilder builder = GroundedItem.builder()
                .y(clientDropItemEvent.y())
                .x(clientDropItemEvent.x())
                .coordinate(coordinate)
                .name(droppedItemName)
                .id(id)
                .number(numberOnGround)
                ;
        return builder.build();
    }

    @Override
    protected void accept(PlayerEventVisitor playerEventHandler) {
        playerEventHandler.visit(this);
    }

    @Override
    protected Packet buildPacket() {
        return Packet.newBuilder()
                .setDropItem(DropItemConfirmPacket.newBuilder()
                        .setSlot(clientDropItemEvent.sourceSlot())
                        .setNumberLeft(numberLeft)
                        .build())
                .build();
    }

}
