package org.y1000.entities.creatures.event;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.State;
import org.y1000.message.serverevent.EntityEventVisitor;
import org.y1000.network.gen.CreatureHurtEventPacket;
import org.y1000.network.gen.Packet;
import org.y1000.util.Coordinate;

public final class CreatureHurtEvent extends AbstractCreatureEvent {
    private final State afterHurtState;
    private final Direction direction;
    private final Coordinate coordinate;

    public CreatureHurtEvent(Creature source, State afterHurtState) {
        super(source);
        this.afterHurtState = afterHurtState;
        this.direction = source.direction();
        this.coordinate = source.coordinate();
    }

    @Override
    protected Packet buildPacket() {
        return Packet.newBuilder()
                .setHurtEventPacket(CreatureHurtEventPacket
                        .newBuilder()
                        .setId(source().id())
                        .setAfterHurtState(afterHurtState.value())
                        .setDirection(direction.value())
                        .setX(coordinate.x())
                        .setY(coordinate.y())
                        .build())
                .build();
    }

    @Override
    public void accept(EntityEventVisitor visitor) {
        visitor.visit(this);
    }

}
