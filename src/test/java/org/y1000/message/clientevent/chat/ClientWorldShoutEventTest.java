package org.y1000.message.clientevent.chat;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.Player;
import org.y1000.message.PlayerTextEvent;
import org.y1000.realm.event.BroadcastChatEvent;
import org.y1000.realm.event.RealmEvent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientWorldShoutEventTest {

    @Test
    void format() {
        assertFalse(ClientWorldShoutEvent.isFormatCorrect(null));
        assertFalse(ClientWorldShoutEvent.isFormatCorrect(""));
        assertFalse(ClientWorldShoutEvent.isFormatCorrect(" !"));
        assertFalse(ClientWorldShoutEvent.isFormatCorrect("! "));
        assertFalse(ClientWorldShoutEvent.isFormatCorrect("！ "));
        assertTrue(ClientWorldShoutEvent.isFormatCorrect("!!2"));
        assertTrue(ClientWorldShoutEvent.isFormatCorrect("! 2"));
    }

    @Test
    void parsed() {
        ClientWorldShoutEvent event = ClientWorldShoutEvent.parse("!test");
        assertEquals("test", event.content());
        event = ClientWorldShoutEvent.parse("！ est");
        assertEquals(" est", event.content());
    }

    @Test
    void toRealmEvent() {
        ClientWorldShoutEvent event = ClientWorldShoutEvent.parse("!test");
        Player player = Mockito.mock(Player.class);
        when(player.viewName()).thenReturn("好犀利");
        ((BroadcastChatEvent)event.toRealmEvent(player)).send(player);
        doAnswer(invocationOnMock -> {
            PlayerTextEvent textEvent = invocationOnMock.getArgument(0);
            assertTrue(textEvent.toPacket().getText().getText().startsWith("好犀利：tes"));
            return null;
        }).when(player).emitEvent(any(PlayerTextEvent.class));
        verify(player, times(1)).emitEvent(any(PlayerTextEvent.class));
    }

    @Test
    void canSend() {
        ClientWorldShoutEvent event = ClientWorldShoutEvent.parse("!test");
        Player player = Mockito.mock(Player.class);
        when(player.stateEnum()).thenReturn(State.DIE);
        assertFalse(event.canSend(player));
        when(player.stateEnum()).thenReturn(State.IDLE);
        when(player.currentLife()).thenReturn(4999);
        assertFalse(event.canSend(player));
        when(player.currentLife()).thenReturn(5000);
        assertTrue(event.canSend(player));
    }
}