package org.y1000.entities.objects;

import org.y1000.message.I2ClientMessage;
import org.y1000.network.gen.DynamicObjectSnapshotPacket;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.ShowDynamicObjectPacket;

import java.util.List;

public final class DynamicObjectSnapshot implements I2ClientMessage {
    private final Packet packet;

    public DynamicObjectSnapshot(IDynamicObject dynamicObject, int elapsed) {
        this(dynamicObject, elapsed, null);
    }

    public DynamicObjectSnapshot(Packet packet) {
        this.packet = packet;
    }

    public DynamicObjectSnapshot(IDynamicObject dynamicObject, int elapsed, String requiredItem) {
        var builder = ShowDynamicObjectPacket.newBuilder()
                .setId(dynamicObject.id())
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


    public static DynamicObjectSnapshot of(DynamicObject dynamicObject, List<Animation> animations, Animation current) {
        var builder = DynamicObjectSnapshotPacket.newBuilder()
                .setId(dynamicObject.id())
                .setX(dynamicObject.coordinate().x())
                .setShape(dynamicObject.shape())
                .setViewName(dynamicObject.viewName().orElse(""))
                .setCurrentAni(current.getId())
                .setCurrentElapsed(current.getElapsed());
        dynamicObject.occupiedCoordinates().forEach(coordinate -> {
            builder.addGuardX(coordinate.x());
            builder.addGuardY(coordinate.y());
        });
        animations.forEach(animation -> {
            builder.addAniId(animation.getId());
            builder.addAniStart(animation.getStart());
            builder.addAniEnd(animation.getEnd());
            builder.addAniLoop(animation.isLoop());
        });
        return new DynamicObjectSnapshot(Packet.newBuilder().setDynamicObjectSnapshot(builder).build());
    }


    @Override
    public Packet toPacket() {
        return packet;
    }

}
