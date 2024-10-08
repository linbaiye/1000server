package org.y1000.network;

import org.y1000.message.ServerMessage;

import java.util.List;

public interface Connection {

    void write(ServerMessage message);

    void close();

    default void flush() {};

}
