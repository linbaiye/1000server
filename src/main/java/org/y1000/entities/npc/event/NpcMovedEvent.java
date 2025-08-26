package org.y1000.entities.npc.event;

import org.y1000.entities.npc.Npc;
import org.y1000.network.I2ClientMessage;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PositionPacket;
import org.y1000.realm.NpcEventHandler;

public class NpcMovedEvent extends AbstractNpcEvent implements I2ClientMessage  {

    private final Packet packet;

    protected NpcMovedEvent(Npc npc,
                            Packet packet) {
        super(npc);
        this.packet = packet;
    }

    @Override
    public void accept(NpcEventHandler handler) {
        handler.onMoved(source(), this);
    }

    public static NpcMovedEvent of(Npc npc) {
        PositionPacket positionPacket = PositionPacket.newBuilder()
                .setDirection(npc.direction().value())
                .setX(npc.coordinate().x())
                .setY(npc.coordinate().y())
                .setId(npc.id()).build();
        return new NpcMovedEvent(npc, Packet.newBuilder().setPositionPacket(positionPacket).build());
    }

    @Override
    public Packet toPacket() {
        return packet;
    }
}
