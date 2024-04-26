package org.y1000.message.clientevent;

import org.y1000.network.gen.PlayerLoginPacket;

public class LoginEvent implements ClientEvent{
    private final String token;

    public LoginEvent(String token) {
        this.token = token;
    }

    public static LoginEvent fromPacket(PlayerLoginPacket packet) {
        return new LoginEvent(packet.getToken());
    }
}
