package org.y1000.connection;

import org.y1000.message.I2ClientMessage;
import org.y1000.message.Message;

import java.util.List;

public interface Connection {

    List<Message> takeMessages();

    void write(I2ClientMessage message);

    default void flush() {};

    long id();
}
