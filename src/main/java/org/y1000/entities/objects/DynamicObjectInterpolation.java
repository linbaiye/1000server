package org.y1000.entities.objects;

import org.y1000.message.AbstractEntityInterpolation;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.ShowDynamicObjectPacket;

public final class DynamicObjectInterpolation extends AbstractEntityInterpolation  {
    private final Packet packet;

    public DynamicObjectInterpolation(TriggerDynamicObject dynamicObject, int elapsed) {
        super(dynamicObject.id(), dynamicObject.coordinate());
        var builder = ShowDynamicObjectPacket.newBuilder()
                .setId(dynamicObject.id())
                .setX(coordinate().x())
                .setY(coordinate().y())
                .setType(dynamicObject.type().value())
                .setStart(dynamicObject.currentAnimation().frameStart())
                .setShape(dynamicObject.shape())
                .setElapsed(elapsed)
                .setEnd(dynamicObject.currentAnimation().frameEnd());
        dynamicObject.name().ifPresent(builder::setName);
        packet = Packet.newBuilder().setShowDynamicObject(builder).build();
    }

    @Override
    public Packet toPacket() {
        return packet;
    }
}
