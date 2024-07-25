package org.y1000.entities.objects;

import org.y1000.event.EntityEventVisitor;
import org.y1000.message.serverevent.Abstract2ClientEntityEvent;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.UpdateDynamicObjectPacket;

public final class UpdateDynamicObjectEvent extends Abstract2ClientEntityEvent {

    private final Packet packet;

    public UpdateDynamicObjectEvent(DynamicObject object) {
        super(object);
        packet = Packet.newBuilder()
                .setUpdateDynamicObject(UpdateDynamicObjectPacket.newBuilder()
                        .setId(object.id())
                        .setStart(object.currentAnimation().frameStart())
                        .setEnd(object.currentAnimation().frameEnd())).build();
    }

    @Override
    public void accept(EntityEventVisitor visitor) {

    }

    @Override
    protected Packet buildPacket() {
        return packet;
    }
}
