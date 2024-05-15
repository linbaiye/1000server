package org.y1000.message.clientevent.input;

public interface InputMessage {
    long sequence();

    InputType type();
}
