package org.y1000.connection;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import org.y1000.message.ServerEvent;

@Slf4j
@ChannelHandler.Sharable
public class MessageEncoder extends MessageToByteEncoder<ServerEvent> {
    public static final MessageEncoder ENCODER = new MessageEncoder();

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ServerEvent message, ByteBuf byteBuf) throws Exception {
        log.debug("Sending message {}.", message);
        byteBuf.writeBytes(message.toPacket().toByteArray());
    }
}
