package org.y1000.entities.players.event;

import lombok.Builder;
import org.y1000.entities.players.Player;
import org.y1000.message.RightClickType;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.ItemAttributePacket;
import org.y1000.network.gen.Packet;

public class PlayerItemAttributeEvent extends AbstractPlayerEvent {
    private final int page;
    private final int slotId;
    private final String description;
    private final RightClickType type;
    public PlayerItemAttributeEvent(Player source, int page, int slotId, String description, RightClickType type) {
        super(source);
        this.page = page;
        this.slotId = slotId;
        this.description = description;
        this.type = type;
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {
        playerEventHandler.visit(this);
    }

    @Override
    protected Packet buildPacket() {
        return Packet.newBuilder()
                .setItemAttribute(ItemAttributePacket.newBuilder()
                        .setPage(page)
                        .setType(type.value())
                        .setText(description)
                        .setSlotId(slotId)
                ).build();
    }
}
