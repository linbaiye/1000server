package org.y1000.connection;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.y1000.connection.gen.Coordinate;
import org.y1000.connection.gen.PositionMessage;
import org.y1000.connection.gen.ShowCreaturePacket;
import org.y1000.message.Message;

import java.util.ArrayList;
import java.util.List;

public final class ConnectionImpl extends MessageTo<DatagramPacket> implements Connection {

    private final List<Message> messages;

    public ConnectionImpl() {
        messages = new ArrayList<>();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
        ByteBuf byteBuf = datagramPacket.content();

        ShowCreaturePacket.newBuilder().setCoordinate(Coordinate.newBuilder().setX(10).setY(10).build());
    }

    @Override
    public List<Message> getUnprocessedMessages() {
        return null;
    }
}
