package org.y1000.message.serverevent;

import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.AbstractPlayerEvent;
import org.y1000.item.Equipment;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PlayerEquipPacket;

@Deprecated
public final class OldPlayerEquipEvent extends AbstractPlayerEvent {
    private final String equipmentName;
    private final int color;

    public OldPlayerEquipEvent(Player source,
                               Equipment equipment) {
        super(source);
        this.equipmentName = equipment.name();
        this.color = equipment.color();
    }


    @Override
    protected Packet buildPacket() {
        return Packet.newBuilder()
                .setEquip(PlayerEquipPacket.newBuilder()
                        .setId(source().id())
                        .setColor(color).build())
                .build();
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {
        playerEventHandler.visit(this);
    }
}
