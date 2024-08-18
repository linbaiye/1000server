package org.y1000.message.serverevent;

import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.AbstractPlayerEvent;
import org.y1000.item.Equipment;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PlayerEquipPacket;

public final class PlayerEquipEvent extends AbstractPlayerEvent {
    private final String equipmentName;
    private final int color;

    public PlayerEquipEvent(Player source,
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
                        .setColor(color)
                        .setEquipmentName(equipmentName))
                .build();
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {
        playerEventHandler.visit(this);
    }
}
