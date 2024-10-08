package org.y1000.network;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.y1000.ServerContext;
import org.y1000.message.ServerMessage;
import org.y1000.realm.RealmManager;

import java.util.*;

/**
 * An implementation that introduces 200ms latency, for development only.
 */
@Slf4j
public final class DevelopingConnection extends AbstractConnection implements Runnable {

    private final List<ServerMessage> messages;

    public final Thread sender;


    public DevelopingConnection(RealmManager realmManager,
                                ServerContext serverContext) {
        super(realmManager, serverContext);
        messages = new ArrayList<>();
        sender = new Thread(this);
        sender.start();
    }


    @Override
    public void write(ServerMessage message) {
        synchronized (messages) {
            messages.add(message);
        }
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
               //  messages.forEach(m -> log.debug("Sent message {}.", m));
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
