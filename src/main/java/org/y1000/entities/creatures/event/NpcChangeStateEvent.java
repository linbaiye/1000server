package org.y1000.entities.creatures.event;

import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.event.EntityEventVisitor;
import org.y1000.network.gen.ChangeStatePacket;
import org.y1000.network.gen.Packet;

public final class NpcChangeStateEvent extends AbstractCreatureEvent {

    private final State state;

    public NpcChangeStateEvent(Npc source, State state) {
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
        visitor.visit(this);
    }

    public static NpcChangeStateEvent of(Npc source) {
        return new NpcChangeStateEvent(source, source.stateEnum());
    }
}
