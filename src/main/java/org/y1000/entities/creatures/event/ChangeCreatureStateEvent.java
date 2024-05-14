package org.y1000.entities.creatures.event;

import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.State;
import org.y1000.message.serverevent.EntityEventVisitor;
import org.y1000.network.gen.ChangeStatePacket;
import org.y1000.network.gen.Packet;

public final class ChangeCreatureStateEvent extends AbstractCreatureEvent {

    private final State state;

    public ChangeCreatureStateEvent(Creature source, State state) {
        super(source);
        this.state = state;
    }

    @Override
    protected Packet buildPacket() {
        return Packet.newBuilder()
                .setChangeStatePacket(ChangeStatePacket.newBuilder()
                        .setState(state.value())
                        .setId(source().id()))
                .build();
    }

    @Override
    public void accept(EntityEventVisitor visitor) {

    }
}
