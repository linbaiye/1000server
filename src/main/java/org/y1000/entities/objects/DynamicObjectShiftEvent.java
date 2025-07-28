package org.y1000.entities.objects;

import org.y1000.message.I2ClientMessage;
import org.y1000.network.gen.DynamicObjectShiftPacket;
import org.y1000.network.gen.Packet;
import org.y1000.realm.DynamicObjectEventHandler;

public class DynamicObjectShiftEvent extends AbstractDynamicObjectEvent implements I2ClientMessage  {
    private final Packet packet;
    protected DynamicObjectShiftEvent(DynamicObject source, Packet packet) {
        super(source);
        this.packet = packet;
    }

    @Override
    public void accept(DynamicObjectEventHandler handler) {
        handler.sendToVisiblePlayers(source(), this);
    }

    @Override
    public Packet toPacket() {
        return packet;
    }


    public static DynamicObjectShiftEvent of(DynamicObject object, int id, int id2, boolean lift) {
        DynamicObjectShiftPacket packet1 = DynamicObjectShiftPacket.newBuilder()
                .setId(object.id())
                .setLiftCoordinates(lift)
                .setAnimationId(id)
                .setAnimationId2(id2)
                .build();
        return new DynamicObjectShiftEvent(object, Packet.newBuilder().setDynamicObjectShift(packet1).build());
    }

    public static DynamicObjectShiftEvent of(DynamicObject object, int id, boolean lift) {
        return of(object, id, 0, lift);
    }

}
