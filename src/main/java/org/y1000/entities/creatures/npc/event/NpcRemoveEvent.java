package org.y1000.entities.creatures.npc.event;

import org.y1000.entities.creatures.npc.Npc;
import org.y1000.message.I2ClientMessage;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.RemoveEntityPacket;
import org.y1000.realm.NpcEventHandler;

public class NpcRemoveEvent extends AbstractNpcEvent implements I2ClientMessage  {

    private final Packet packet;
    protected NpcRemoveEvent(Npc npc, Packet packet) {
        super(npc);
        this.packet = packet;
    }
    public static NpcRemoveEvent of(Npc npc) {
        return new NpcRemoveEvent(npc, Packet.newBuilder().setRemoveEntity(RemoveEntityPacket.newBuilder().setId(npc.id())).build());
    }

    @Override
    public void accept(NpcEventHandler handler) {
        handler.onRemove(source(), this);
    }

    @Override
    public Packet toPacket() {
        return packet;
    }
}
