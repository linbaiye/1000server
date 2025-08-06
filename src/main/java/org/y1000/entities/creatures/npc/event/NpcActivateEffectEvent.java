package org.y1000.entities.creatures.npc.event;

import org.y1000.entities.creatures.npc.Npc;
import org.y1000.network.gen.ActivateEffectPacket;
import org.y1000.network.gen.Packet;

public class NpcActivateEffectEvent extends AbstractNpcToVisibleEvent {
    protected NpcActivateEffectEvent(Npc npc, Packet packet) {
        super(npc, packet);
    }

    public static NpcActivateEffectEvent of(Npc npc, String effect, int millis) {
        return new NpcActivateEffectEvent(npc, Packet.newBuilder().setActivateEffect(
                ActivateEffectPacket.newBuilder().setId(npc.id())
                        .setMillis(millis)
                        .setEffect(effect)
                        .build()).build());
    }
}
