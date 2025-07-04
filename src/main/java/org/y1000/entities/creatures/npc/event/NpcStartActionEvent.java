package org.y1000.entities.creatures.npc.event;

import org.y1000.entities.creatures.monster.NpcActionEnum;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.message.I2ClientMessage;
import org.y1000.network.gen.ChangeStatePacket;
import org.y1000.network.gen.Packet;

public class NpcStartActionEvent extends AbstractNpcEvent implements I2ClientMessage {

    private final Packet packet;

    protected NpcStartActionEvent(Npc npc, Packet packet) {
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

    public static NpcStartActionEvent of(Npc npc, NpcActionEnum actionEnum) {
        var packet = Packet.newBuilder()
                .setChangeStatePacket(ChangeStatePacket.newBuilder()
                        .setState(actionEnum.value())
                        .setDirection(npc.direction().value())
                        .setId(npc.id()))
                .build();
        return new NpcStartActionEvent(npc, packet);
    }
}
