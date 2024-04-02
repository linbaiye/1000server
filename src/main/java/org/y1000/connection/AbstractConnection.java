package org.y1000.connection;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.y1000.connection.gen.InputPacket;
import org.y1000.connection.gen.Packet;
import org.y1000.message.*;
import org.y1000.message.input.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public abstract class AbstractConnection extends ChannelInboundHandlerAdapter implements Connection {

    private final List<InputMessage> messages;

    private final ConnectionEventListener eventListener;

    private final AtomicReference<ChannelHandlerContext> context;

    public AbstractConnection(ConnectionEventListener listener) {
        messages = new ArrayList<>();
        eventListener = listener;
        context = new AtomicReference<>();
    }


    private InputMessage createInputMessage(InputPacket inputPacket) {
        InputType type = ValueEnum.fromValueOrThrow(InputType.values(), inputPacket.getType());
        return switch (type) {
            case MOUSE_RIGHT_CLICK -> RightMouseClick.fromPacket(inputPacket);
            case MOUSE_RIGHT_RELEASE -> RightMouseRelease.fromPacket(inputPacket);
            case MOUSE_RIGHT_MOTION -> RightMousePressedMotion.fromPacket(inputPacket);
            default -> throw new IllegalArgumentException();
        };
    }

    private InputMessage createMessage(Packet packet) {
        return switch (packet.getTypedPacketCase()) {
            case INPUTPACKET -> createInputMessage(packet.getInputPacket());
            default -> throw new IllegalArgumentException();
        };
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Packet packet) {
            var message = createMessage(packet);
            log.debug("Received message {}.", message);
            synchronized (messages) {
                messages.add(message);
            }
        }
    }

    ChannelHandlerContext getContext() {
        return context.get();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (eventListener != null) {
            eventListener.OnEvent(ConnectionEventType.CLOSED, this);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (!ctx.channel().isActive()) {
            channelInactive(ctx);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        context.set(ctx);
        if (eventListener != null) {
            eventListener.OnEvent(ConnectionEventType.ESTABLISHED, this);
        }
    }



    @Override
    public List<InputMessage> takeMessages() {
        synchronized (messages) {
            if (messages.isEmpty()) {
                return Collections.emptyList();
            }
            var ret = new ArrayList<>(messages);
            messages.clear();
            return ret;
        }
    }
}
