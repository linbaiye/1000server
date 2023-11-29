package org.y1000.connection;

import io.netty.channel.ChannelHandlerContext;
import org.y1000.message.Message;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * An implementation that introduces 200ms latency, for development only.
 */
public final class DevelopingConnection extends AbstractConnection implements Runnable {

    private final Deque<Message> messages;

    public final Thread sender;

    public DevelopingConnection(ConnectionEventListener listener) {
        super(listener);
        messages = new ArrayDeque<>();
        sender = new Thread(this);
        sender.start();
    }

    @Override
    public void write(Message message) {
        synchronized (messages) {
            messages.addLast(message);
        }
    }

    @Override
    public void run() {
        ChannelHandlerContext context = getContext();
        if (context == null) {
            return;
        }
        long curMilli = System.currentTimeMillis();
        synchronized (messages) {
            if (messages.isEmpty()) {
                return;
            }
            Message message = messages.peekFirst();
            if (curMilli - message.timestamp() < 200) {
                return;
            }
            message = messages.pollFirst();
            context.channel().writeAndFlush(message);
        }
    }
}
