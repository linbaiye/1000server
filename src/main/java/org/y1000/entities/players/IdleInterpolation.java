package org.y1000.entities.players;

import lombok.Builder;
import org.y1000.connection.gen.MovementPacket;
import org.y1000.connection.gen.Packet;
import org.y1000.connection.gen.ShowPlayerPacket;
import org.y1000.entities.Direction;
import org.y1000.util.Coordinate;

@Builder
public class IdleInterpolation implements Interpolation {

    private final long startAtMillis;

    private long length;

    private final Coordinate coordinate;

    private final long id;

    private final Direction direction;

    @Builder.Default
    private long timestamp = System.currentTimeMillis();

    @Override
    public State state() {
        return State.IDLE;
    }

    @Override
    public boolean canMerge(Interpolation interpolation) {
        if (interpolation instanceof IdleInterpolation idleInterpolation) {
            return idleInterpolation.startAtMillis() == startAtMillis
                    && direction == idleInterpolation.direction
                    && coordinate.equals(idleInterpolation.coordinate);
        }
        return false;
    }

    @Override
    public void merge(Interpolation interpolation) {
        if (!canMerge(interpolation)) {
            return;
        }
        IdleInterpolation idleInterpolation = (IdleInterpolation) interpolation;
        length = Math.max(length, idleInterpolation.length);
    }

    @Override
    public long lengthMillis() {
        return length;
    }

    @Override
    public long startAtMillis() {
        return startAtMillis;
    }

    @Override
    public Packet toPacket() {
        MovementPacket movementPacket = MovementPacket.newBuilder()
                .setId((int)id)
                .setState(state().value())
                .setDirection(direction.value())
                .setTimestamp(timestamp)
                .setX(coordinate.x())
                .setY(coordinate.y())
                .build();
        return Packet.newBuilder()
                .setShowPlayerPacket(ShowPlayerPacket.newBuilder()
                        .setMovement(movementPacket)
                        .build()
                ).build();
    }

    @Override
    public long timestamp() {
        return timestamp;
    }
}
