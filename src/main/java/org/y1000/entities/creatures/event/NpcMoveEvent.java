package org.y1000.entities.creatures.event;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.monster.AbstractMonster;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.event.EntityEventVisitor;
import org.y1000.network.gen.MonsterMoveEventPacket;
import org.y1000.network.gen.Packet;

public final class NpcMoveEvent extends AbstractCreatureEvent {

    private final Direction direction;

    private final int speed;

    private final int x;
    private final int y;

    private NpcMoveEvent(Npc source, Direction direction, int speed) {
        super(source);
        this.speed = speed;
        this.direction = direction;
        this.x = source.coordinate().x();
        this.y = source.coordinate().y();
    }

    @Override
    protected Packet buildPacket() {
        return Packet.newBuilder()
                .setMonsterMove(MonsterMoveEventPacket.newBuilder()
                        .setId(source().id())
                        .setDirection(direction.value())
                        .setSpeed(speed)
                        .setX(x)
                        .setY(y)
                        .build())
                .build();
    }

    @Override
    public void accept(EntityEventVisitor visitor) {
        visitor.visit(this);
    }

    public static NpcMoveEvent move(Npc monster, Direction direction, int speed) {
        return new NpcMoveEvent(monster, direction, speed);
    }
}
