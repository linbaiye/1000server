package org.y1000.entities.creatures.event;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.npc.INpc;
import org.y1000.event.EntityEventVisitor;
import org.y1000.network.gen.Packet;

public final class INpcMoveEvent extends AbstractCreatureEvent {

    private final Direction direction;

    private final int speed;

    private final int x;
    private final int y;

    private INpcMoveEvent(INpc source, Direction direction, int speed) {
        super(source);
        this.speed = speed;
        this.direction = direction;
        this.x = source.coordinate().x();
        this.y = source.coordinate().y();
    }

    @Override
    protected Packet buildPacket() {
        return null;
    }

    @Override
    public void accept(EntityEventVisitor visitor) {
        visitor.visit(this);
    }

    public static INpcMoveEvent move(INpc monster, Direction direction, int speed) {
        return new INpcMoveEvent(monster, direction, speed);
    }
}
