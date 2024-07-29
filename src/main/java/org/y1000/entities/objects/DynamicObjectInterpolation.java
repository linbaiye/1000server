package org.y1000.entities.objects;

import org.y1000.message.AbstractEntityInterpolation;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.ShowDynamicObjectPacket;

public final class DynamicObjectInterpolation extends AbstractEntityInterpolation  {
    private final Packet packet;

    public DynamicObjectInterpolation(DynamicObject dynamicObject, int elapsed) {
        this(dynamicObject, elapsed, null);
    }

    public DynamicObjectInterpolation(DynamicObject dynamicObject, int elapsed, String requiredItem) {
        super(dynamicObject.id(), dynamicObject.coordinate());
        var builder = ShowDynamicObjectPacket.newBuilder()
                .setId(dynamicObject.id())
                .setX(coordinate().x())
                .setY(coordinate().y())
                .setType(dynamicObject.type().value())
                .setStart(dynamicObject.currentAnimation().frameStart())
                .setLoop(dynamicObject.currentAnimation().loop())
                .setShape(dynamicObject.shape())
                .setElapsed(elapsed)
                .setEnd(dynamicObject.currentAnimation().frameEnd());
        dynamicObject.occupyingCoordinates().forEach(coordinate -> {
            builder.addGuardX(coordinate.x());
            builder.addGuardY(coordinate.y());
        });
        if (dynamicObject.type() == DynamicObjectType.TRIGGER) {
            builder.setRequiredItem(requiredItem);
        }
        dynamicObject.viewName().ifPresent(builder::setName);
        packet = Packet.newBuilder().setShowDynamicObject(builder).build();
    }


    @Override
    public Packet toPacket() {
        return packet;
    }
}
