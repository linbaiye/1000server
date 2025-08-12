package org.y1000.entities.creatures.npc.event;

import org.y1000.entities.creatures.npc.NpcAction;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.network.I2ClientMessage;
import org.y1000.network.gen.ChangeStatePacket;
import org.y1000.network.gen.Packet;
import org.y1000.realm.NpcEventHandler;

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

    public static Packet changeStatePacket(Npc npc, NpcAction actionEnum) {
        return Packet.newBuilder()
                .setChangeStatePacket(ChangeStatePacket.newBuilder()
                        .setState(actionEnum.value())
                        .setDirection(npc.direction().value())
                        .setX(npc.coordinate().x())
                        .setY(npc.coordinate().y())
                        .setId(npc.id()))
                .build();
    }

    public static NpcStartActionEvent of(Npc npc, NpcAction actionEnum) {
        return new NpcStartActionEvent(npc, changeStatePacket(npc, actionEnum));
    }
}
