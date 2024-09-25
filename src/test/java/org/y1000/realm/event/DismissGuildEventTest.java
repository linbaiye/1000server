package org.y1000.realm.event;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.players.Player;
import org.y1000.guild.GuildMembership;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class DismissGuildEventTest {


    @Test
    void name() {
        var player = Mockito.mock(Player.class);
        when(player.guildMembership()).thenReturn(new DismissGuildEvent());
        DismissGuildEvent event = new DismissGuildEvent(1);
    }

}