package org.y1000.connection;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.y1000.message.ServerEvent;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * An implementation that introduces 200ms latency, for development only.
 */
@Slf4j
public final class DevelopingConnection extends AbstractConnection implements Runnable {

    private final Deque<ServerEvent> messages;

    public final Thread sender;

    private long id;

    private static final Set<Long> IDs = new HashSet<>();

    public DevelopingConnection(ConnectionEventListener listener) {
        super(listener);
        messages = new ArrayDeque<>();
        sender = new Thread(this);
        sender.start();
    }

    private static synchronized long allocateId() {
        if (!IDs.contains(0L)) {
            IDs.add(0L);
            return 0L;
        }
        if (!IDs.contains(1L)) {
            IDs.add(1L);
            return 1L;
        }
        throw new IllegalArgumentException("No Id free.");
    }

    private static synchronized void freeId(long id) {
        IDs.remove(id);
    }

    @Override
    public void write(ServerEvent message) {
        synchronized (messages) {
            messages.addLast(message);
        }
    }

    @Override
    public void writeAndFlush(ServerEvent message) {
        write(message);
        flush();
    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        id = allocateId();
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        freeId(id);
        super.channelInactive(ctx);
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
            ServerEvent message = messages.pollFirst();
            context.channel().writeAndFlush(message);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                handleMessages();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
