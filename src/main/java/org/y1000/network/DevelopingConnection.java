package org.y1000.network;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.y1000.message.ServerMessage;

import java.util.*;

/**
 * An implementation that introduces 200ms latency, for development only.
 */
@Slf4j
public final class DevelopingConnection extends AbstractConnection implements Runnable {

    private final List<ServerMessage> messages;

    public final Thread sender;

    private long id;

    private static final Set<Long> IDs = new HashSet<>();

    public DevelopingConnection(ConnectionManager connectionManager) {
        super(connectionManager);
        messages = new ArrayList<>();
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
    public void write(ServerMessage message) {
        synchronized (messages) {
            messages.add(message);
        }
    }

    @Override
    public void writeAndFlush(ServerMessage message) {
        write(message);
        flush();
    }

    @Override
    public void write(List<ServerMessage> messages) {
        synchronized (this.messages) {
            this.messages.addAll(messages);
        }
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
        synchronized (messages) {
            if (messages.isEmpty()) {
                return;
            }
            try {
                messages.forEach(context.channel()::write);
                // messages.forEach(m -> log.debug("Sent message {}.", m));
                context.channel().flush();
                messages.clear();
            } catch (Exception e) {
                log.error("Failed to write message.", e);;
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                handleMessages();
                Thread.sleep(120);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
