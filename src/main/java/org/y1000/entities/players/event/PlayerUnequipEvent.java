package org.y1000.entities.players.event;

import org.y1000.entities.creatures.State;
import org.y1000.entities.players.Player;
import org.y1000.item.EquipmentType;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PlayerUnequipPacket;

public final class PlayerUnequipEvent extends AbstractPlayerEvent {

    private final State changedTo;

    private final EquipmentType uneqipped;

    private final int basicQuanfaLevel;

    public PlayerUnequipEvent(Player source,
                              EquipmentType uneqipped,
                              State st, int basicQuanfaLevel) {
        super(source);
        this.uneqipped = uneqipped;
        this.changedTo = st;
        this.basicQuanfaLevel = basicQuanfaLevel;
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
        if (changedTo != null) {
            builder.setChangedToState(changedTo.value());
            builder.setBasicQuanfaLevel(basicQuanfaLevel);
        }
        return Packet.newBuilder()
                .setUnequip(builder)
                .build();
    }
}
