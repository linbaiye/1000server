package org.y1000.network;

public interface Connection {

    void writeAndFlush(I2ClientMessage message);

    void tryClose();

    default void flush() {};

}
