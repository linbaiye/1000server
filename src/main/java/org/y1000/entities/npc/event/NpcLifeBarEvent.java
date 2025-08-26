package org.y1000.entities.npc.event;

import org.y1000.entities.npc.Npc;
import org.y1000.network.I2ClientMessage;
import org.y1000.network.gen.EntityDamagedPacket;
import org.y1000.network.gen.Packet;
import org.y1000.realm.NpcEventHandler;

public class NpcLifeBarEvent extends AbstractNpcEvent implements I2ClientMessage {

    private final Packet packet;

    protected NpcLifeBarEvent(Npc npc, Packet packet) {
        super(npc);
        this.packet = packet;
    }

    @Override
    public void accept(NpcEventHandler handler) {
        handler.sendToVisiblePlayers(source(), this);
    }

    @Override
    public Packet toPacket() {
        return packet;
    }

    public static Packet damagedPacket(long id, int cur, int max) {
        EntityDamagedPacket build = EntityDamagedPacket.newBuilder().setId(id).setPercent((int)(((float)cur / max) * 100)).build();
        return Packet.newBuilder().setEntityDamaged(build).build();
    }

    public static NpcLifeBarEvent die(Npc npc) {
        return new NpcLifeBarEvent(npc, damagedPacket(npc.id(), 0, 1));
    }

    public static NpcLifeBarEvent of(Npc npc, int cur, int max) {
        return new NpcLifeBarEvent(npc, damagedPacket(npc.id(), cur, max));
    }

}
