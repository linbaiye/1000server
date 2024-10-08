package org.y1000.entities;

import lombok.Getter;
import org.y1000.event.EntityEventVisitor;
import org.y1000.message.serverevent.Abstract2ClientEntityEvent;
import org.y1000.network.gen.LifeBarPacket;
import org.y1000.network.gen.Packet;

public final class EntityLifebarEvent extends Abstract2ClientEntityEvent {

    private final LifeBarPacket lifeBarPacket;

    @Getter
    private final int current;

    @Getter
    private final int max;

    public EntityLifebarEvent(Entity source, int current, int max) {
        super(source);
        lifeBarPacket = LifeBarPacket.newBuilder()
                .setId(source.id())
                .setPercent(max > 0 ? (int)(((float)current / max) * 100) : 0).build();
        this.current = current;
        this.max = max;
    }

    @Override
    public void accept(EntityEventVisitor visitor) {
    }

    @Override
    protected Packet buildPacket() {
        return Packet.newBuilder().setLifebar(lifeBarPacket).build();
    }
}
