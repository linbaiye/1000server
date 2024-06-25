package org.y1000.entities.creatures.event;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.monster.AbstractMonster;
import org.y1000.event.EntityEventVisitor;
import org.y1000.network.gen.MonsterMoveEventPacket;
import org.y1000.network.gen.Packet;

public final class MonsterMoveEvent extends AbstractCreatureEvent {

    private final Direction direction;

    private final int speed;

    private MonsterMoveEvent(Creature source, Direction direction, int speedRate) {
        super(source);
        this.speed = speedRate;
        this.direction = direction;
    }

    @Override
    protected Packet buildPacket() {
        return Packet.newBuilder()
                .setMonsterMove(MonsterMoveEventPacket.newBuilder()
                        .setId(source().id())
                        .setDirection(direction.value())
                        .setSpeed(speed)
                        .build())
                .build();
    }

    @Override
    public void accept(EntityEventVisitor visitor) {
        visitor.visit(this);
    }

    public static MonsterMoveEvent move(AbstractMonster monster, Direction direction, int speedRate) {
        return new MonsterMoveEvent(monster, direction, speedRate);
    }
}
