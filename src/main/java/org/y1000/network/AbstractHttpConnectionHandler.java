package org.y1000.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;

import java.nio.charset.StandardCharsets;

import static io.netty.handler.codec.rtsp.RtspResponseStatuses.BAD_REQUEST;

public abstract class AbstractHttpConnectionHandler extends SimpleChannelInboundHandler<HttpObject> {

    private HttpRequest request;

    private String requestBody;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    abstract Logger log();

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    protected abstract Object handle(String type, String body);


    <T> T parseJson(String content, Class<T> type) {
        try {
            return getObjectMapper().readValue(content, type);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    String serialize(Object object) {
        try {
            return getObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private FullHttpResponse handleRequest() {
        try {
            String s = request.headers().get("X-Type");
            var response = handle(s, requestBody);
            if (response instanceof String str) {
                return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR, Unpooled.copiedBuffer(str, StandardCharsets.UTF_8));
            } else if (response instanceof FullHttpResponse fullHttpResponse) {
                return fullHttpResponse;
            } else {
                return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR, Unpooled.copiedBuffer(serialize(response), StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            log().error("Failed to handle request, ", e);
            return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR, Unpooled.copiedBuffer("Internal server error.", StandardCharsets.UTF_8));
        }
    }

    private void writeResponse(ChannelHandlerContext ctx, FullHttpResponse httpResponse) {
        httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpResponse.content().readableBytes());
        ctx.writeAndFlush(httpResponse)
                .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
        if (msg instanceof HttpRequest httpRequest) {
            this.request = httpRequest;
        } else if (msg instanceof HttpContent httpContent) {
            ByteBuf content = httpContent.content();
            if (content.isReadable()) {
                requestBody =  content.toString(StandardCharsets.UTF_8);
            }
            if (msg instanceof LastHttpContent lastHttpContent) {
                if (lastHttpContent.decoderResult().isSuccess()) {
                    writeResponse(ctx, handleRequest());
                } else {
                    writeResponse(ctx, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, BAD_REQUEST, Unpooled.copiedBuffer("Bad request", StandardCharsets.UTF_8)));
                }
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log().error("Exception: ", cause);
        ctx.close();
    }
}
