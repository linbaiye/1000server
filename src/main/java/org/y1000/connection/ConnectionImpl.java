package org.y1000.connection;

import lombok.extern.slf4j.Slf4j;
import org.y1000.message.I2ClientMessage;
import org.y1000.message.Message;

@Slf4j
public final class ConnectionImpl extends AbstractConnection {

    public ConnectionImpl(ConnectionEventListener listener) {
        super(listener);
    }

    @Override
    public void write(I2ClientMessage message) {
        var context = getContext();
        if (context == null) {
            return;
        }
        context.channel().writeAndFlush(message);
    }
}
