package org.y1000.connection;

import org.y1000.message.ServerMessage;
import org.y1000.message.clientevent.ClientEvent;

import java.util.List;

public interface Connection {

    List<ClientEvent> takeMessages();

    void write(ServerMessage message);

    default void write(List<ServerMessage> messages) {

    }

    void writeAndFlush(ServerMessage message);

    default void flush() {};

    long id();
}
