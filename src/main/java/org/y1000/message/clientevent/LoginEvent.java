package org.y1000.message.clientevent;

import lombok.Getter;
import org.y1000.network.gen.PlayerLoginPacket;

public record LoginEvent(String token, String charName) implements ClientEvent {
    public static LoginEvent fromPacket(PlayerLoginPacket packet) {
        return new LoginEvent(packet.getToken(), packet.getCharName());
    }
}
