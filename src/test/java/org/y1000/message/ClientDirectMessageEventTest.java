package org.y1000.message;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientDirectMessageEventTest {

    @Test
    void isFormatCorrect() {
        assertFalse(ClientDirectMessageEvent.isFormatCorrect("@纸条"));
        assertFalse(ClientDirectMessageEvent.isFormatCorrect("@纸条 "));
        assertFalse(ClientDirectMessageEvent.isFormatCorrect("@纸条  test"));
        assertTrue(ClientDirectMessageEvent.isFormatCorrect("@纸条 test"));
        assertTrue(ClientDirectMessageEvent.isFormatCorrect("@纸条 @纸条 "));
    }


    @Test
    void parse() {
        ClientDirectMessageEvent event = ClientDirectMessageEvent.parse("@纸条 t ");
        assertEquals("t", event.receiver());
        assertEquals("", event.content());
        event = ClientDirectMessageEvent.parse("@纸条 t  ");
        assertEquals(" ", event.content());
        event = ClientDirectMessageEvent.parse("@纸条 t @纸条");
        assertEquals("@纸条", event.content());
    }
}