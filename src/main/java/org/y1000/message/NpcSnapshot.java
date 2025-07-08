package org.y1000.message;

import org.y1000.entities.creatures.monster.NpcAnimationEnum;
import org.y1000.entities.creatures.npc.INpc;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.network.gen.CreatureBaseInfoPacket;
import org.y1000.network.gen.NpcSnapshotPacket;
import org.y1000.network.gen.Packet;

public record NpcSnapshot(Packet packet) implements I2ClientMessage {
    @Override
    public Packet toPacket() {
        return packet;
    }


    public static NpcSnapshot of(Npc npc, int elapsed, NpcAnimationEnum type) {
        var coordinate = npc.coordinate();
        CreatureBaseInfoPacket baseInfoSnapshot = CreatureBaseInfoPacket.newBuilder()
                .setY(coordinate.y())
                .setX(coordinate.x())
                .setElapsedMillis(elapsed)
                .setId(npc.id())
                .setViewName(npc.getViewName())
                .setDirection(npc.direction().value())
                .build();
        NpcSnapshotPacket.Builder builder = NpcSnapshotPacket.newBuilder()
                .setBaseInfo(baseInfoSnapshot)
                .setAnimate(npc.getAnimate())
                .setState(type.value())
                .setShape(npc.getShape());
        return new NpcSnapshot(Packet.newBuilder().setNpcSnapshot(builder).build());
    }



    public static NpcSnapshot ofNpc(INpc npc, int elapsed) {
        var coordinate = npc.coordinate();
        CreatureBaseInfoPacket baseInfoSnapshot = CreatureBaseInfoPacket.newBuilder()
                .setY(coordinate.y())
                .setX(coordinate.x())
                .setElapsedMillis(elapsed)
                .setId(npc.id())
                .setViewName(npc.viewName())
                .setDirection(npc.direction().value())
                .build();
        NpcSnapshotPacket.Builder builder = NpcSnapshotPacket.newBuilder()
                .setBaseInfo(baseInfoSnapshot)
                .setAnimate(npc.animation())
                .setState(npc.npcStateEnum().value())
                .setShape(npc.shape());
        return new NpcSnapshot(Packet.newBuilder().setNpcSnapshot(builder).build());
    }
}
