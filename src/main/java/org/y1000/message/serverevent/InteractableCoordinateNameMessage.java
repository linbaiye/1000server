package org.y1000.message.serverevent;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.teleport.StaticTeleport;
import org.y1000.message.I2ClientMessage;
import org.y1000.network.gen.NpcPositionPacket;
import org.y1000.network.gen.Packet;

import java.util.Collection;

public final class InteractableCoordinateNameMessage implements I2ClientMessage  {

    private final Collection<Npc> merchants;

    private final Collection<StaticTeleport> teleports;

    public InteractableCoordinateNameMessage(Collection<Npc> merchants,
                                             Collection<StaticTeleport> teleports) {
        Validate.notNull(merchants);
        Validate.notNull(teleports);
        this.merchants = merchants;
        this.teleports = teleports;
    }

    private Packet buildPacket() {
        NpcPositionPacket.Builder builder = NpcPositionPacket.newBuilder();
        for (var merchant : merchants) {
            builder.addIdList(merchant.id());
            builder.addNameList(merchant.viewName());
            builder.addXList(merchant.coordinate().x());
            builder.addYList(merchant.coordinate().y());
        }
        for (StaticTeleport teleport : teleports) {
            builder.addIdList(teleport.id());
            builder.addNameList(teleport.viewName());
            builder.addXList(teleport.coordinate().x());
            builder.addYList(teleport.coordinate().y());
        }
        return Packet.newBuilder().setNpcPosition(builder).build();
    }

    @Override
    public Packet toPacket() {
        return buildPacket();
    }
}