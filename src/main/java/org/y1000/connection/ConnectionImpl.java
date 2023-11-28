package org.y1000.connection;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.y1000.connection.gen.MovementPacket;
import org.y1000.connection.gen.Packet;
import org.y1000.entities.Direction;
import org.y1000.message.*;
import org.y1000.util.Coordinate;

import java.util.*;

@Slf4j
public final class ConnectionImpl extends ChannelInboundHandlerAdapter implements Connection {

    private final List<Message> messages;

    private final ConnectionEventListener eventListener;

    private ChannelHandlerContext context;

    public ConnectionImpl(ConnectionEventListener listener) {
        messages = new ArrayList<>();
        eventListener = listener;
    }

    private Message createMovementMessage(MovementPacket packet) {
        var pkt = MessageType.fromValue(packet.getType());
        if (pkt == null) {
            throw new IllegalArgumentException("Unknown message type :" + packet.getType());
        }
        return switch (pkt) {
            case MOVE -> new MoveMessage(Direction.fromValue(packet.getDirection()), new Coordinate(packet.getX(), packet.getY()), packet.getId(), packet.getTimestamp());
            case TURN -> new TurnMessage(Direction.fromValue(packet.getDirection()), new Coordinate(packet.getX(), packet.getY()), packet.getId(), packet.getTimestamp());
            case STOP_MOVE -> new StopMoveMessage(Direction.fromValue(packet.getDirection()), new Coordinate(packet.getX(), packet.getY()), packet.getId(), packet.getTimestamp());
            case POSITION -> new PositionMessage(Direction.fromValue(packet.getDirection()), new Coordinate(packet.getX(), packet.getY()), packet.getId(), packet.getTimestamp());
            default -> throw new IllegalArgumentException();
        };
    }


    private Message createMessage(Packet packet) {
        return switch (packet.getTypedPacketCase()) {
            case MOVEMENTPACKET -> createMovementMessage(packet.getMovementPacket());
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

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (eventListener != null) {
            eventListener.OnEvent(ConnectionEventType.CLOSED, this);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        context = ctx;
        if (eventListener != null) {
            eventListener.OnEvent(ConnectionEventType.ESTABLISHED, this);
        }
    }

    @Override
    public List<Message> takeMessages() {
        synchronized (messages) {
            if (messages.isEmpty()) {
                return Collections.emptyList();
            }
            var ret = new ArrayList<>(messages);
            messages.clear();
            return ret;
        }
    }

    @Override
    public void write(Message message) {
        if (context == null) {
            return;
        }
        context.channel().writeAndFlush(message);
    }
}
