package org.y1000.message;

import org.junit.jupiter.api.Test;
import org.y1000.message.clientevent.chat.ClientWhisperEvent;

import static org.junit.jupiter.api.Assertions.*;

class ClientDirectMessageEventTest {

    @Test
    void isFormatCorrect() {
        assertFalse(ClientWhisperEvent.isFormatCorrect("@纸条"));
        assertFalse(ClientWhisperEvent.isFormatCorrect("@纸条 "));
        assertFalse(ClientWhisperEvent.isFormatCorrect("@纸条  test"));
        assertTrue(ClientWhisperEvent.isFormatCorrect("@纸条 test"));
        assertTrue(ClientWhisperEvent.isFormatCorrect("@纸条 @纸条 "));
    }


    @Test
    void parse() {
        ClientWhisperEvent event = ClientWhisperEvent.parse("@纸条 t ");
        assertEquals("t", event.receiver());
        assertEquals("", event.content());
        event = ClientWhisperEvent.parse("@纸条 t  ");
        assertEquals(" ", event.content());
        event = ClientWhisperEvent.parse("@纸条 t @纸条");
        assertEquals("@纸条", event.content());
    }
}