package org.y1000.message;

import org.y1000.entities.item.Item;
import org.y1000.entities.item.StackItem;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.AbstractPlayerEvent;
import org.y1000.message.clientevent.ClientDropItemEvent;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.DropItemConfirmPacket;
import org.y1000.network.gen.Packet;

public class PlayerDropItemEvent extends AbstractPlayerEvent {

    private final ClientDropItemEvent clientDropItemEvent;

    private final Item currentSlot;

    public PlayerDropItemEvent(Player source, ClientDropItemEvent clientDropItemEvent, Item currentSlot) {
        super(source);
        this.clientDropItemEvent = clientDropItemEvent;
        this.currentSlot = currentSlot;
    }

    @Override
    protected void accept(PlayerEventVisitor playerEventHandler) {
        playerEventHandler.visit(this);
    }

    @Override
    protected Packet buildPacket() {
        var number = currentSlot instanceof StackItem stackItem ? stackItem.number() : 0;
        return Packet.newBuilder()
                .setDropItem(DropItemConfirmPacket.newBuilder()
                        .setSlot(clientDropItemEvent.sourceSlot())
                        .setNumberLeft(number)
                        .build())
                .build();
    }

}
