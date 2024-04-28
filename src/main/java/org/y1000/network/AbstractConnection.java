package org.y1000.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.y1000.message.clientevent.ClientAttackEvent;
import org.y1000.message.clientevent.LoginEvent;
import org.y1000.network.gen.ClientPacket;
import org.y1000.message.clientevent.CharacterMovementEvent;
import org.y1000.message.clientevent.ClientEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public abstract class AbstractConnection extends ChannelInboundHandlerAdapter implements Connection {

    private final AtomicReference<ChannelHandlerContext> context;

    private final List<ClientEventListener> clientEventListeners;

    private final ConnectionManager connectionManager;

    public AbstractConnection(ConnectionManager connectionManager) {
        context = new AtomicReference<>();
        clientEventListeners = new ArrayList<>();
        this.connectionManager = connectionManager;
    }


    @Override
    public void registerClientEventListener(ClientEventListener clientEventListener) {
        clientEventListeners.add(clientEventListener);
    }


    private ClientEvent createMessage(ClientPacket clientPacket) {
        return switch (clientPacket.getTypeCase()) {
            case MOVEEVENTPACKET -> CharacterMovementEvent.fromPacket(clientPacket);
            case LOGINPACKET -> LoginEvent.fromPacket(clientPacket.getLoginPacket());
            case ATTACKEVENTPACKET -> ClientAttackEvent.fromPacket(clientPacket.getAttackEventPacket());
            default -> throw new IllegalArgumentException();
        };
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ClientPacket packet) {
            var message = createMessage(packet);
            log.debug("Received message {}.", message);
            connectionManager.onClientEvent(this, message);
            clientEventListeners.forEach(clientEventListener -> clientEventListener.OnEvent(message));
        }
    }

    ChannelHandlerContext getContext() {
        return context.get();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("Channel closed.");
        connectionManager.onClosed(this);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (!ctx.channel().isActive()) {
            connectionManager.onClosed(this);
            context.get().close();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        context.set(ctx);
        connectionManager.onEstablished(this);
    }

    @Override
    public void close() {
        try {
            context.get().close();
        } catch (Exception e) {
            //ignored.
        }
    }
}
