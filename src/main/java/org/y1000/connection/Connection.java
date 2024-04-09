package org.y1000.connection;

import org.y1000.message.ServerEvent;
import org.y1000.message.clientevent.ClientEvent;

import java.util.List;

public interface Connection {

    List<ClientEvent> takeMessages();

    void write(ServerEvent message);

    default void write(List<ServerEvent> messages) {

    }

    void writeAndFlush(ServerEvent message);

    default void flush() {};

    long id();
}
