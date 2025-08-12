package org.y1000.entities.creatures.npc.event;

import org.y1000.entities.ActiveEntity;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.network.I2ClientMessage;
import org.y1000.network.gen.NpcMovePacket;
import org.y1000.network.gen.Packet;
import org.y1000.realm.NpcEventHandler;

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

    public static NpcMovePacket forPlayer(ActiveEntity entity, Direction direction) {
        return movePacket(entity, direction, 0);
    }

    private static NpcMovePacket movePacket(ActiveEntity entity, Direction direction, int millis) {
        return  NpcMovePacket.newBuilder()
                .setId(entity.id())
                .setX(entity.coordinate().x())
                .setY(entity.coordinate().y())
                .setSpeedMillis(millis)
                .setDirection(direction.value())
                .build();
    }

    public static NpcMoveEvent of(Npc npc, int millis) {
        NpcMovePacket movePacket = movePacket(npc, npc.direction(), millis);
        Packet packet = Packet.newBuilder().setNpcMove(movePacket).build();
        return new NpcMoveEvent(npc, packet);
    }

    @Override
    public void accept(NpcEventHandler handler) {
        handler.sendToVisiblePlayers(source(), this);
    }
}
