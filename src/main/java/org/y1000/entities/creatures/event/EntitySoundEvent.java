package org.y1000.entities.creatures.event;

import org.y1000.entities.ActiveEntity;
import org.y1000.message.serverevent.Abstract2ClientEntityEvent;
import org.y1000.event.EntityEventVisitor;
import org.y1000.network.gen.CreatureSoundEventPacket;
import org.y1000.network.gen.Packet;

public final class EntitySoundEvent extends Abstract2ClientEntityEvent {

    private final String sound;
    public EntitySoundEvent(ActiveEntity source, String sound) {
        super(source);
        this.sound = sound;
    }

    @Override
    protected Packet buildPacket() {
        return Packet.newBuilder()
                .setSound(CreatureSoundEventPacket.newBuilder()
                        .setId(source().id())
                        .setSound(sound)
                        .build()).build();
    }

    @Override
    public void accept(EntityEventVisitor visitor) {
        visitor.visit(this);
    }
}
