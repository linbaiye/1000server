package org.y1000.network;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.y1000.message.I2ClientMessage;
import org.y1000.network.gen.ClientPacket;
import org.y1000.realm.RealmManager;

import java.util.*;

/**
 * An implementation that introduces latency, for development only.
 */
@Slf4j
public final class DevelopingConnection extends AbstractConnection implements Runnable {

    private final List<LatencyMessage<I2ClientMessage>> writingMessages;
    private final List<LatencyMessage<Object>> deliveryMessages;
    public final Thread sender;

    public DevelopingConnection(RealmManager realmManager) {
        super(realmManager);
        deliveryMessages = new ArrayList<>();
        writingMessages = new ArrayList<>();
        sender = new Thread(this);
        sender.start();
    }

    private record LatencyMessage<T>(T msg, long deliveryTime) {
        public static LatencyMessage<I2ClientMessage> of(I2ClientMessage msg) {
            return new LatencyMessage<>(msg, System.currentTimeMillis() + 50);
        }

        public static LatencyMessage<Object> of(Object msg) {
            return new LatencyMessage<>(msg, System.currentTimeMillis() + 50);
        }
    }



    @Override
    public synchronized void writeAndFlush(I2ClientMessage message) {
        writingMessages.add(LatencyMessage.of(message));
        notify();
    }

    private synchronized void addToDelivery(Object msg) {
        deliveryMessages.add(LatencyMessage.of(msg));
        notify();
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof ClientPacket packet) {
            try {
                var message = createMessage(packet);
                if (message != null) {
                    addToDelivery(message);
                }
            } catch (Exception e) {
                log.error("Exception ", e);
            }
        }
    }


    private synchronized void handleDelivery() {
        if (deliveryMessages.isEmpty())
            return;
        Iterator<LatencyMessage<Object>> iterator = deliveryMessages.iterator();
        long l = System.currentTimeMillis();
        while (iterator.hasNext()) {
            LatencyMessage<Object> next = iterator.next();
            if (next.deliveryTime >= l) {
                getRealmManager().queueEvent(ConnectionEvent.Data(this, next.msg));
                iterator.remove();
            }
        }
    }


    private synchronized void handleWrite() {
        if (writingMessages.isEmpty())
            return;
        Iterator<LatencyMessage<I2ClientMessage>> iterator = writingMessages.iterator();
        long l = System.currentTimeMillis();
        while (iterator.hasNext()) {
            LatencyMessage<I2ClientMessage> next = iterator.next();
            if (next.deliveryTime >= l) {
                iterator.remove();
                var context = getContext();
                context.channel().writeAndFlush(next.msg);
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                synchronized (this) {
                    while (writingMessages.isEmpty() && deliveryMessages.isEmpty()) {
                        wait(10);
                    }
                }
                handleDelivery();
                handleWrite();
            } catch (InterruptedException e) {
                log.error("Exception ", e);
                break;
            }
        }
    }
}
