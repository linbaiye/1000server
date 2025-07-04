package org.y1000.entities.creatures.npc.event;

import org.y1000.entities.creatures.npc.Npc;
import org.y1000.message.I2ClientMessage;
import org.y1000.network.gen.MonsterMoveEventPacket;
import org.y1000.network.gen.Packet;

public class NpcMoveEvent extends AbstractNpcEvent implements I2ClientMessage  {
    private final Packet packet;

    public NpcMoveEvent(Npc npc, Packet packet) {
        super(npc);
        this.packet = packet;
    }

    @Override
    public Packet toPacket() {
        return packet;
    }

    public static NpcMoveEvent of(Npc npc) {
        Packet packet = Packet.newBuilder()
                .setMonsterMove(MonsterMoveEventPacket.newBuilder()
                        .setId(npc.id())
                        .setDirection(npc.direction().value())
                        .setX(npc.coordinate().x())
                        .setY(npc.coordinate().y())
                        .build())
                .build();
        return new NpcMoveEvent(npc, packet);
    }

    @Override
    public void accept(NpcEventHandler handler) {
        handler.sendToVisiblePlayers(source(), this);
    }
}
