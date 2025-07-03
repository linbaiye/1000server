package org.y1000.entities.creatures.event;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.monster.NpcActionEnum;
import org.y1000.entities.creatures.npc.INpc;
import org.y1000.event.EntityEventVisitor;
import org.y1000.network.gen.ChangeStatePacket;
import org.y1000.network.gen.Packet;

public final class NpcChangeStateEvent extends AbstractCreatureEvent {

    private final int stateValue;

    private final Direction direction;

    public NpcChangeStateEvent(INpc source, NpcActionEnum playerStateEnum) {
        super(source);
        stateValue = playerStateEnum.value();
        this.direction = source.direction();
    }

    @Override
    protected Packet buildPacket() {
        return Packet.newBuilder()
                .setChangeStatePacket(ChangeStatePacket.newBuilder()
                        .setState(stateValue)
                        .setDirection(direction.value())
                        .setId(source().id()))
                .build();
    }

    @Override
    public void accept(EntityEventVisitor visitor) {
        visitor.visit(this);
    }

    public static NpcChangeStateEvent of(INpc source) {
        return new NpcChangeStateEvent(source, source.npcStateEnum());
    }
}
