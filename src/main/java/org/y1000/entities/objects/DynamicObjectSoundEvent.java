package org.y1000.entities.objects;

import org.y1000.message.I2ClientMessage;
import org.y1000.network.gen.EntitySoundPacket;
import org.y1000.network.gen.Packet;
import org.y1000.realm.DynamicObjectEventHandler;

public class DynamicObjectSoundEvent extends AbstractDynamicObjectEvent implements I2ClientMessage  {

    private final Packet packet;

    private DynamicObjectSoundEvent(DynamicObject source, Packet packet) {
        super(source);
        this.packet = packet;
    }

    @Override
    public void accept(DynamicObjectEventHandler handler) {
        handler.sendToVisiblePlayers(source(), this);
    }

    public static DynamicObjectSoundEvent of(DynamicObject source, String s) {
        EntitySoundPacket packet = EntitySoundPacket.newBuilder()
                .setEntityName("")
                .setSound(s).build();
        return new DynamicObjectSoundEvent(source, Packet.newBuilder().setEntitySound(packet).build());
    }

    @Override
    public Packet toPacket() {
        return packet;
    }
}
