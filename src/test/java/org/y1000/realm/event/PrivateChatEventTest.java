package org.y1000.realm.event;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.players.Player;
import org.y1000.network.gen.TextMessagePacket;

import static org.junit.jupiter.api.Assertions.*;


class PrivateChatEventTest {

    @Test
    void send() {
        PrivateChatEvent chatEvent = PrivateChatEvent.send("r", "s", "test" );
        var player = Mockito.mock(Player.class);
        TextMessagePacket text = chatEvent.toTextEvent(player).toPacket().getText();
        assertTrue(text.getText().contains("s>：test"));
        assertTrue(chatEvent.needConfirm());
    }

    @Test
    void confirm() {
        PrivateChatEvent chatEvent = PrivateChatEvent.send("r", "s", "test" );
        PrivateChatEvent confirmation = chatEvent.createConfirmation();
        assertFalse(confirmation.needConfirm());
        var player = Mockito.mock(Player.class);
        TextMessagePacket text = confirmation.toTextEvent(player).toPacket().getText();
        assertTrue(text.getText().contains(">r：test"));
    }


    @Test
    void noRecipient() {
        PrivateChatEvent chatEvent = PrivateChatEvent.send("r", "s", "test");
        PrivateChatEvent noRecipient = chatEvent.noRecipient();
        assertFalse(noRecipient.needConfirm());
        var player = Mockito.mock(Player.class);
        TextMessagePacket text = noRecipient.toTextEvent(player).toPacket().getText();
        assertTrue(text.getText().contains("玩家不在线"));
    }
}