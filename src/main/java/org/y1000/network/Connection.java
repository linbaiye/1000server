package org.y1000.network;

import org.y1000.message.I2ClientMessage;

public interface Connection {

    void write(I2ClientMessage message);

    void tryClose();

    default void flush() {};

}
