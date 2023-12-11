package org.y1000.connection;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.y1000.message.I2ClientMessage;
import org.y1000.message.Message;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * An implementation that introduces 200ms latency, for development only.
 */
@Slf4j
public final class DevelopingConnection extends AbstractConnection implements Runnable {

    private final Deque<I2ClientMessage> messages;

    public final Thread sender;

    private final long id;

    public DevelopingConnection(ConnectionEventListener listener, long id) {
        super(listener);
        messages = new ArrayDeque<>();
        sender = new Thread(this);
        sender.start();
        this.id = id;
    }

    @Override
    public void write(I2ClientMessage message) {
        synchronized (messages) {
            messages.addLast(message);
        }
    }

    @Override
    public long id() {
        return id;
    }


    private void handleMessages() {
        ChannelHandlerContext context = getContext();
        if (context == null) {
            return;
        }
        long curMilli = System.currentTimeMillis();
        synchronized (messages) {
            if (messages.isEmpty()) {
                return;
            }
            I2ClientMessage message = messages.peekFirst();
            if (curMilli - message.timestamp() < 200) {
                return;
            }
            message = messages.pollFirst();
            context.channel().writeAndFlush(message);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                handleMessages();
                Thread.sleep(10);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
