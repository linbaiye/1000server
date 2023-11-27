package org.y1000.connection;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.y1000.connection.gen.Packet;

@Slf4j
public class LengthBasedMessageDecoder extends LengthFieldBasedFrameDecoder {


    public LengthBasedMessageDecoder() {
        super(Short.MAX_VALUE, 0, 4, 0, 4);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf byteBuf = (ByteBuf) super.decode(ctx, in);
        if (byteBuf == null) {
            return null;
        }
        byte[] bytes = new byte[byteBuf.readableBytes()];
        log.info("Received {} bytes.", bytes.length);
        return Packet.parseFrom(bytes);
    }
}
