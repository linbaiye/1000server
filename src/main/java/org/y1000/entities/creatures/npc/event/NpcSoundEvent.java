package org.y1000.entities.creatures.npc.event;

import org.y1000.entities.creatures.npc.Npc;
import org.y1000.network.gen.EntitySoundPacket;
import org.y1000.network.gen.Packet;

public class NpcSoundEvent extends AbstractNpcToVisibleEvent {
    protected NpcSoundEvent(Npc npc, Packet packet) {
        super(npc, packet);
    }

    public static NpcSoundEvent of(Npc npc, String sound) {
        return new NpcSoundEvent(npc, Packet.newBuilder().setEntitySound(EntitySoundPacket.newBuilder()
                        .setSound(sound)
                        .setEntityName(npc.viewName())
                ).build());
    }
}
