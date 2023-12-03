package org.y1000.message.input;

import org.y1000.message.Message;

public interface InputMessage extends Message {
    long sequence();

    InputType type();
}
