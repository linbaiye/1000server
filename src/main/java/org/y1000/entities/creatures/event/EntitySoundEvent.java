package org.y1000.entities.creatures.event;

import org.y1000.entities.ActiveEntity;
import org.y1000.message.serverevent.Abstract2ClientEntityEvent;
import org.y1000.event.EntityEventVisitor;
import org.y1000.network.gen.CreatureSoundEventPacket;
import org.y1000.network.gen.Packet;

public final class EntitySoundEvent extends Abstract2ClientEntityEvent {

    private final String sound;
    private final long id;
    public EntitySoundEvent(ActiveEntity source, String sound) {
        this(source, sound, source.id());
    }

    public EntitySoundEvent(ActiveEntity source, String sound, long id) {
        super(source);
        this.sound = sound;
        this.id = id;
    }

    public static EntitySoundEvent broadcast(ActiveEntity source, String sound) {
        return new EntitySoundEvent(source, sound, 0);
    }

    @Override
    protected Packet buildPacket() {
        return Packet.newBuilder()
                .setSound(CreatureSoundEventPacket.newBuilder()
                        .setId(id)
                        .setSound(sound)
                        .build())
                .build();
    }

    @Override
    public void accept(EntityEventVisitor visitor) {
        visitor.visit(this);
    }
}
