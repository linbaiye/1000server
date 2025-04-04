package org.y1000.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import org.y1000.message.ServerMessage;

@Slf4j
@ChannelHandler.Sharable
public final class MessageEncoder extends MessageToByteEncoder<ServerMessage> {

    public static final MessageEncoder ENCODER = new MessageEncoder();

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ServerMessage message, ByteBuf byteBuf) {
        try {
            byteBuf.writeBytes(message.toPacket().toByteArray());
        } catch (Exception e) {
            log.error("Exception ", e);
        }
    }
}
