package org.y1000.message.clientevent;

import org.y1000.entities.players.PlayerImpl;
import org.y1000.network.gen.PlayerLoginPacket;

public final class LoginEvent implements ClientEvent {
    private final String token;

    public LoginEvent(String token) {
        this.token = token;
    }

    public static LoginEvent fromPacket(PlayerLoginPacket packet) {
        return new LoginEvent(packet.getToken());
    }

    @Override
    public void accept(PlayerImpl player, ClientEventVisitor handler) {
    }
}
