package org.y1000.network;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.account.*;

import java.nio.charset.StandardCharsets;



@Slf4j
public class AccountConnectionHandler extends AbstractHttpConnectionHandler {

    private final AccountManager accountManager;

    public AccountConnectionHandler(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    private FullHttpResponse handleLogin(String requestBody) throws JsonProcessingException {
        var req = getObjectMapper().readValue(requestBody, LoginRequest.class);
        LoginResponse response = accountManager.login(req.getUsername(), req.getPassword());
        String body = getObjectMapper().writeValueAsString(response);
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(body, StandardCharsets.UTF_8));
    }

    private FullHttpResponse handleSingup(String requestBody) throws JsonProcessingException {
        var req = getObjectMapper().readValue(requestBody, LoginRequest.class);
        var response = accountManager.register(req.getUsername(), req.getPassword());
        String body = getObjectMapper().writeValueAsString(response);
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(body, StandardCharsets.UTF_8));
    }

    private FullHttpResponse handleCreateCharacter(String requestBody) throws JsonProcessingException {
        var req = getObjectMapper().readValue(requestBody, CreateCharacterRequest.class);
        var response = accountManager.createCharacter(req.getToken(), req.getCharacterName(), req.isMale());
        String body = getObjectMapper().writeValueAsString(response);
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(body, StandardCharsets.UTF_8));
    }

    @Override
    Logger log() {
        return log;
    }

    @Override
    protected FullHttpResponse handle(String type, String body) {
        try {
            return switch (RequestType.valueOf(type)) {
                case CREATE_CHAR -> handleCreateCharacter(body);
                case SIGNUP -> handleSingup(body);
                case LOGIN -> handleLogin(body);
            };
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
