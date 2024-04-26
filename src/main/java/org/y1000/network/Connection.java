package org.y1000.network;

import org.y1000.message.ServerMessage;
import org.y1000.message.clientevent.ClientEvent;

import java.util.List;

public interface Connection {

    void write(ServerMessage message);

    void registerClientEventListener(ClientEventListener clientEventListener);

    default void write(List<ServerMessage> messages) {

    }

    void close();


    void writeAndFlush(ServerMessage message);

    default void flush() {};

    long id();
}
