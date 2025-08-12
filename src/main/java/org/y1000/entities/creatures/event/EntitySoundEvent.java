package org.y1000.entities.creatures.event;

import org.y1000.network.I2ClientMessage;
import org.y1000.network.gen.EntitySoundPacket;
import org.y1000.network.gen.Packet;

public final class EntitySoundEvent implements I2ClientMessage {

    private final Packet packet;

    public EntitySoundEvent(String sound) {
        packet = buildPacket(sound);
    }


    private static Packet buildPacket(String sound) {
        return Packet.newBuilder()
                .setEntitySound(EntitySoundPacket.newBuilder()
                        .setSound(sound)
                        .build())
                .build();
    }

    @Override
    public Packet toPacket() {
        return packet;
    }
}
