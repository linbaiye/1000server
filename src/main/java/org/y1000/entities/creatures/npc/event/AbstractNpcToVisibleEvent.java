package org.y1000.entities.creatures.npc.event;

import org.y1000.entities.creatures.npc.Npc;
import org.y1000.network.I2ClientMessage;
import org.y1000.network.gen.Packet;
import org.y1000.realm.NpcEventHandler;

public abstract class AbstractNpcToVisibleEvent extends AbstractNpcEvent implements I2ClientMessage {

    private final Packet packet;

    protected AbstractNpcToVisibleEvent(Npc npc, Packet packet) {
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
}
