package org.y1000.connection;

import org.y1000.message.I2ClientMessage;
import org.y1000.message.input.InputMessage;

import java.util.List;

public interface Connection {

    List<InputMessage> takeMessages();

    void write(I2ClientMessage message);

    void writeAndFlush(I2ClientMessage message);

    default void flush() {};

    long id();
}
