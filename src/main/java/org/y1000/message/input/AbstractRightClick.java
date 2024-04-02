package org.y1000.message.input;

import org.y1000.entities.Direction;

public abstract class AbstractRightClick implements InputMessage {
    private final long sequence;
    private final Direction direction;

    public AbstractRightClick( long sequence, Direction direction) {
        this.direction = direction;
        this.sequence = sequence;
    }

    public Direction direction() {
        return direction;
    }


    @Override
    public long sequence() {
        return sequence;
    }

    @Override
    public String toString() {
        return "Input{" +
                "sequence=" + sequence +
                ", direction=" + direction +
                ", type=" + type().name() +
                '}';
    }
}
