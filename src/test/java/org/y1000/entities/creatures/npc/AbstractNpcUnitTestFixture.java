package org.y1000.entities.creatures.npc;

import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.entities.players.Damage;
import org.y1000.entities.players.Player;
import org.y1000.util.Coordinate;

import static org.mockito.Mockito.when;

public abstract class AbstractNpcUnitTestFixture extends AbstractUnitTestFixture {



    protected Player mockEnemyPlayer(Coordinate coordinate) {
        var player = Mockito.mock(Player.class);
        when(player.coordinate()).thenReturn(coordinate.move(0, 1));
        when(player.damage()).thenReturn(new Damage(1, 1, 1, 1));
        when(player.hit()).thenReturn(10000);
        return player;
    }

}
