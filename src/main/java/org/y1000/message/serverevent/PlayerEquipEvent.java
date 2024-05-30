package org.y1000.message.serverevent;

import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.AbstractPlayerEvent;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PlayerEquipPacket;

public final class PlayerEquipEvent extends AbstractPlayerEvent {
    private final String equipmentName;
    public PlayerEquipEvent(Player source,
                            String equipmentName) {
        super(source);
        this.equipmentName = equipmentName;
    }


    @Override
    protected Packet buildPacket() {
        return Packet.newBuilder()
                .setEquip(PlayerEquipPacket.newBuilder()
                        .setId(source().id())
                        .setEquipmentName(equipmentName))
                .build();
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {
        playerEventHandler.visit(this);
    }
}
