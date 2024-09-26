package org.y1000.realm.event;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.PlayerUpdateGuildEvent;
import org.y1000.event.EntityEvent;
import org.y1000.guild.GuildMembership;
import org.y1000.message.PlayerTextEvent;

import java.util.Optional;

import static org.mockito.Mockito.*;

class DismissGuildEventTest {

    @Test
    void send() {
        var player = Mockito.mock(Player.class);
        when(player.guildMembership()).thenReturn(Optional.of(new GuildMembership(1, "test",  "test")));
        DismissGuildEvent event = new DismissGuildEvent(0);
        event.send(player);
        verify(player, times(0)).emitEvent(any(EntityEvent.class));
        event = new DismissGuildEvent(1);
        event.send(player);
        verify(player, times(1)).emitEvent(any(PlayerTextEvent.class));
        verify(player, times(1)).emitEvent(any(PlayerUpdateGuildEvent.class));
        verify(player, times(1)).quitGuild();
    }

}