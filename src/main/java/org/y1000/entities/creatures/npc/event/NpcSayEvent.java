package org.y1000.entities.creatures.npc.event;

import org.y1000.entities.creatures.npc.Npc;
import org.y1000.network.gen.CreatureSayPacket;
import org.y1000.network.gen.Packet;

public class NpcSayEvent extends AbstractNpcToVisibleEvent {
    public NpcSayEvent(Npc npc, Packet packet) {
        super(npc, packet);
    }

    public static NpcSayEvent say(Npc npc, String text) {
        return new NpcSayEvent(npc, Packet.newBuilder().setSay(CreatureSayPacket.newBuilder()
                .setId(npc.id())
                .setText(text).build()).build());
    }
}
