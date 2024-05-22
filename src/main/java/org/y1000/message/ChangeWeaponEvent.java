package org.y1000.message;

import org.y1000.network.gen.ChangeWeaponPacket;
import org.y1000.network.gen.Packet;

public final class ChangeWeaponEvent extends AbstractServerMessage {

    private final long id;

    private final String name;

    public ChangeWeaponEvent(long id, String name) {
        this.id = id;
        this.name = name;
    }
    @Override
    protected Packet buildPacket() {
        ChangeWeaponPacket packet = ChangeWeaponPacket.newBuilder().setName(name).setId(id).build();
        return Packet.newBuilder().setChangeWeaponPacket(packet).build();
    }
}
