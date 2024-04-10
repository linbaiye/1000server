package org.y1000.connection;

import lombok.extern.slf4j.Slf4j;
import org.y1000.message.ServerEvent;
import org.y1000.message.ServerMessage;

@Slf4j
public final class ConnectionImpl extends AbstractConnection {

    public ConnectionImpl(ConnectionEventListener listener) {
        super(listener);
    }


    @Override
    public void write(ServerMessage message) {
        var context = getContext();
        if (context == null) {
            return;
        }
        context.channel().write(message);
    }

    @Override
    public void writeAndFlush(ServerMessage message) {
        write(message);
        flush();
    }

    @Override
    public void flush() {
        var context = getContext();
        if (context == null) {
            return;
        }
        context.channel().flush();
    }

    @Override
    public long id() {
        return 0;
    }
}
