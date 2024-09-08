package org.y1000.network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import org.y1000.account.*;

import java.nio.charset.StandardCharsets;

import static io.netty.handler.codec.rtsp.RtspResponseStatuses.BAD_REQUEST;


@Slf4j
public class HttpConnectionHandler extends SimpleChannelInboundHandler<HttpObject> {

    private HttpRequest request;

    private final AccountManager accountManager;

    private String requestBody;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public HttpConnectionHandler(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    private FullHttpResponse handleLogin() throws JsonProcessingException {
        var req = OBJECT_MAPPER.readValue(requestBody, LoginRequest.class);
        LoginResponse response = accountManager.login(req.getUsername(), req.getPassword());
        String body = OBJECT_MAPPER.writeValueAsString(response);
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(body, StandardCharsets.UTF_8));
    }

    private FullHttpResponse handleSingup() throws JsonProcessingException {
        var req = OBJECT_MAPPER.readValue(requestBody, LoginRequest.class);
        var response = accountManager.register(req.getUsername(), req.getPassword());
        String body = OBJECT_MAPPER.writeValueAsString(response);
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(body, StandardCharsets.UTF_8));
    }

    private FullHttpResponse handleCreateCharacter() throws JsonProcessingException {
        var req = OBJECT_MAPPER.readValue(requestBody, CreateCharacterRequest.class);
        var response = accountManager.createCharacter(req.getToken(), req.getCharacterName(), req.isMale());
        String body = OBJECT_MAPPER.writeValueAsString(response);
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(body, StandardCharsets.UTF_8));
    }

    private FullHttpResponse handleRequest() {
        try {
            String s = request.headers().get("X-Type");
            return switch (RequestType.valueOf(s)) {
                case CREATE_CHAR -> handleCreateCharacter();
                case SIGNUP -> handleSingup();
                case LOGIN -> handleLogin();
            };
        } catch (Exception e) {
            log.error("Failed to handle request, ", e);
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
        log.error("Exception: ", cause);
        ctx.close();
    }
}
