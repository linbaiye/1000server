package org.y1000.realm.event;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.players.Player;
import org.y1000.network.gen.TextMessagePacket;

import static org.junit.jupiter.api.Assertions.*;


class PrivateChatEventTest {

    @Test
    void send() {
        PlayerWhisperEvent chatEvent = PlayerWhisperEvent.send("r", "s", "test" );
        var player = Mockito.mock(Player.class);
        TextMessagePacket text = chatEvent.toTextEvent(player).toPacket().getText();
        assertTrue(text.getText().contains("s>：test"));
        assertTrue(chatEvent.needConfirm());
    }

    @Test
    void confirm() {
        PlayerWhisperEvent chatEvent = PlayerWhisperEvent.send("r", "s", "test" );
        PlayerWhisperEvent confirmation = chatEvent.createConfirmation();
        assertFalse(confirmation.needConfirm());
        var player = Mockito.mock(Player.class);
        TextMessagePacket text = confirmation.toTextEvent(player).toPacket().getText();
        assertTrue(text.getText().contains(">r：test"));
    }


    @Test
    void noRecipient() {
        PlayerWhisperEvent chatEvent = PlayerWhisperEvent.send("r", "s", "test");
        PlayerWhisperEvent noRecipient = chatEvent.noRecipient();
        assertFalse(noRecipient.needConfirm());
        var player = Mockito.mock(Player.class);
        TextMessagePacket text = noRecipient.toTextEvent(player).toPacket().getText();
        assertTrue(text.getText().contains("玩家不在线"));
    }
}