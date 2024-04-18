package org.y1000.message.input;

public abstract class AbstractInput implements InputMessage {

    private final long sequence;

    protected AbstractInput(long sequnce) {
        this.sequence = sequnce;
    }

    @Override
    public long sequence() {
        return sequence;
    }
}
