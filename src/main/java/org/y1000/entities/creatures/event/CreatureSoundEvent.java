package org.y1000.entities.creatures.event;

import org.y1000.entities.creatures.Creature;
import org.y1000.message.serverevent.EntityEventVisitor;
import org.y1000.network.gen.CreatureSoundEventPacket;
import org.y1000.network.gen.Packet;

public final class CreatureSoundEvent extends AbstractCreatureEvent {

    private final String sound;
    public CreatureSoundEvent(Creature source, String sound) {
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
