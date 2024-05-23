package org.y1000.message;

import org.y1000.network.gen.ChangeWeaponPacket;
import org.y1000.network.gen.Packet;

public final class ChangeWeaponEvent extends AbstractServerMessage {

    private final long id;

    private final String name;

    private final int state;

    public ChangeWeaponEvent(long id, String name, int state) {
        this.id = id;
        this.name = name;
        this.state = state;
    }
    @Override
    protected Packet buildPacket() {
        ChangeWeaponPacket packet = ChangeWeaponPacket.newBuilder().setName(name)
                .setId(id)
                .setState(state)
                .build();
        return Packet.newBuilder().setChangeWeaponPacket(packet).build();
    }
}
