package org.y1000.message.clientevent.chat;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.players.Player;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ClientSpeakEventTest {

    @Test
    void canSend() {
        assertTrue(new ClientSayEvent("te").canSend(Mockito.mock(Player.class)));
    }

    @Test
    void toPlayerEvent() {
        ClientSayEvent event = new ClientSayEvent("你好啊");
        Player player = Mockito.mock(Player.class);
        when(player.viewName()).thenReturn("测");
        String content = event.toPlayerEvent(player).toPacket().getChat().getContent();
        assertTrue(content.startsWith("测：你好"));
    }
}