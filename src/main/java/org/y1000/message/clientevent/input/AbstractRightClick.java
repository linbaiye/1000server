package org.y1000.message.clientevent.input;

import org.y1000.entities.Direction;

public abstract class AbstractRightClick extends AbstractInput {
    private final Direction direction;

    public AbstractRightClick( long sequence, Direction direction) {
        super(sequence);
        this.direction = direction;
    }

    public Direction direction() {
        return direction;
    }

    @Override
    public String toString() {
        return "Input{" +
                "sequence=" + sequence() +
                ", direction=" + direction +
                ", type=" + type().name() +
                '}';
    }
}
