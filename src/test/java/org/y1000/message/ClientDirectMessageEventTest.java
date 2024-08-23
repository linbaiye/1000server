package org.y1000.message;

import org.junit.jupiter.api.Test;
import org.y1000.message.clientevent.chat.ClientPrivateChatEvent;

import static org.junit.jupiter.api.Assertions.*;

class ClientDirectMessageEventTest {

    @Test
    void isFormatCorrect() {
        assertFalse(ClientPrivateChatEvent.isFormatCorrect("@纸条"));
        assertFalse(ClientPrivateChatEvent.isFormatCorrect("@纸条 "));
        assertFalse(ClientPrivateChatEvent.isFormatCorrect("@纸条  test"));
        assertTrue(ClientPrivateChatEvent.isFormatCorrect("@纸条 test"));
        assertTrue(ClientPrivateChatEvent.isFormatCorrect("@纸条 @纸条 "));
    }


    @Test
    void parse() {
        ClientPrivateChatEvent event = ClientPrivateChatEvent.parse("@纸条 t ");
        assertEquals("t", event.receiver());
        assertEquals("", event.content());
        event = ClientPrivateChatEvent.parse("@纸条 t  ");
        assertEquals(" ", event.content());
        event = ClientPrivateChatEvent.parse("@纸条 t @纸条");
        assertEquals("@纸条", event.content());
    }
}