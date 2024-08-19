package org.y1000.message.serverevent;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.creatures.npc.Merchant;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.AbstractPlayerEvent;
import org.y1000.entities.teleport.StaticTeleport;
import org.y1000.network.gen.NpcPositionPacket;
import org.y1000.network.gen.Packet;

import java.util.Collection;

public final class NpcPositionEvent extends AbstractPlayerEvent {

    private final Collection<Merchant> merchants;

    private final Collection<StaticTeleport> teleports;

    public NpcPositionEvent(Player source,
                            Collection<Merchant> merchants,
                            Collection<StaticTeleport> teleports) {
        super(source, true);
        Validate.notNull(merchants);
        Validate.notNull(teleports);
        Validate.isTrue(!merchants.isEmpty() || !teleports.isEmpty());
        this.merchants = merchants;
        this.teleports = teleports;
    }

    @Override
    protected Packet buildPacket() {
        NpcPositionPacket.Builder builder = NpcPositionPacket.newBuilder();
        for (Merchant merchant : merchants) {
            builder.addNameList(merchant.viewName());
            builder.addXList(merchant.coordinate().x());
            builder.addYList(merchant.coordinate().y());
        }
        for (StaticTeleport teleport : teleports) {
            builder.addNameList(teleport.viewName());
            builder.addXList(teleport.coordinate().x());
            builder.addYList(teleport.coordinate().y());
        }
        return Packet.newBuilder().setNpcPosition(builder).build();
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {
    }
}