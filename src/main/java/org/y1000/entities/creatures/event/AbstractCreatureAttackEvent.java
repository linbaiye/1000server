package org.y1000.entities.creatures.event;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.Creature;
import org.y1000.network.gen.CreatureAttackEventPacket;
import org.y1000.network.gen.Packet;
import org.y1000.util.Coordinate;

public abstract class AbstractCreatureAttackEvent extends AbstractCreatureEvent {

    private final Coordinate coordinate;

    private final Direction direction;

    protected AbstractCreatureAttackEvent(Creature source,
                                          Coordinate coordinate,
                                          Direction direction) {
        super(source);
        this.coordinate = coordinate;
        this.direction = direction;
    }


    protected abstract CreatureAttackEventPacket.Builder setCreatureSpecificFields(
            CreatureAttackEventPacket.Builder builder);


    @Override
    protected Packet buildPacket() {
        return Packet.newBuilder()
                .setAttackEventPacket(
                        setCreatureSpecificFields(
                                CreatureAttackEventPacket.newBuilder()
                                .setDirection(direction.value())
                                .setId(source().id()))
                                .setX(coordinate.x())
                                .setY(coordinate.y())
                                .build())
                .build();
    }
}
