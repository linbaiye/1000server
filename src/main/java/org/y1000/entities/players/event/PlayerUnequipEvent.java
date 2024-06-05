package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.item.EquipmentType;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PlayerUnequipPacket;

public final class PlayerUnequipEvent extends AbstractPlayerEvent {

    private final EquipmentType uneqipped;


    public PlayerUnequipEvent(Player source,
                              EquipmentType uneqipped) {
        super(source);
        this.uneqipped = uneqipped;
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {
        playerEventHandler.visit(this);
    }

    @Override
    protected Packet buildPacket() {
        PlayerUnequipPacket.Builder builder = PlayerUnequipPacket.newBuilder()
                .setId(source().id())
                .setEquipmentType(uneqipped.value());
        return Packet.newBuilder()
                .setUnequip(builder)
                .build();
    }
}
