package org.y1000.util;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.Entity;
import org.y1000.entities.RelevantScope;
import org.y1000.entities.creatures.Creature;
import org.y1000.entities.players.Player;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class RelevantScopeTest {

    @Test
    void addIfVisible() {
    }

    @Test
    void filter() {
        Entity entity = Mockito.mock(Entity.class);
        when(entity.coordinate()).thenReturn(new Coordinate(20, 20));
        RelevantScope relevantScope = new RelevantScope(entity);
        Set<Player> players = relevantScope.filter(Player.class);
        assertTrue(players.isEmpty());
        Player player = Mockito.mock(Player.class);
        when(player.coordinate()).thenReturn(new Coordinate(20, 21));
        assertTrue(relevantScope.addIfVisible(player));
        Set<Creature> creatures = relevantScope.filter(Creature.class);
        assertEquals(1, creatures.size());
        players = relevantScope.filter(Player.class);
        assertEquals(1, players.size());
    }
}