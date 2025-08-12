package org.y1000.entities.objects;

import org.y1000.message.I2ClientMessage;
import org.y1000.network.gen.DynamicObjectSnapshotPacket;
import org.y1000.network.gen.Packet;

import java.util.List;

public final class DynamicObjectSnapshot implements I2ClientMessage {
    private final Packet packet;


    public DynamicObjectSnapshot(Packet packet) {
        this.packet = packet;
    }

    public static DynamicObjectSnapshot of(DynamicObject dynamicObject, List<Animation> animations, Animation current, boolean o) {
        var builder = DynamicObjectSnapshotPacket.newBuilder()
                .setId(dynamicObject.id())
                .setX(dynamicObject.coordinate().x())
                .setY(dynamicObject.coordinate().y())
                .setShape(dynamicObject.shape())
                .setViewName(dynamicObject.viewName().orElse(""))
                .setCurrentAni(current.getId())
                .setOffsetX(dynamicObject.getOffset().x())
                .setOffsetY(dynamicObject.getOffset().y())
                .setOccupying(o)
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
