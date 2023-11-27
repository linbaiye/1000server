package org.y1000.connection;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.y1000.connection.gen.MovementPacket;
import org.y1000.connection.gen.Packet;
import org.y1000.entities.Direction;
import org.y1000.message.*;
import org.y1000.util.Coordinate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public final class ConnectionImpl extends SimpleChannelInboundHandler<Packet> implements Connection {

    private final List<Message> messages;

    public ConnectionImpl() {
        messages = new ArrayList<>();
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
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {
        log.info("Received message.");
        var message = createMessage(packet);
        synchronized (messages) {
            messages.add(message);
        }
    }

    @Override
    public List<Message> getUnprocessedMessages() {
        synchronized (messages) {
            if (!messages.isEmpty()) {
               return new ArrayList<>(messages);
            }
            return Collections.emptyList();
        }
    }
}
