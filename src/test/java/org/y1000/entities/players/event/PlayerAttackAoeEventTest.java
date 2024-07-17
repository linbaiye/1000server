package org.y1000.entities.players.event;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.players.Player;
import org.y1000.kungfu.AssistantKungFu;
import org.y1000.util.Coordinate;

import static org.mockito.Mockito.when;

class PlayerAttackAoeEventTest extends AbstractUnitTestFixture  {
    @Test
    void name() {
        var kungfu = kungFuFactory.create("风灵旋");
        Player player = Mockito.mock(Player.class);
        when(player.direction()).thenReturn(Direction.DOWN);
        when(player.coordinate()).thenReturn(Coordinate.xy(2, 2));
        Npc target = Mockito.mock(Npc.class);
        when(target.coordinate()).thenReturn(Coordinate.xy(2, 3));
        Npc affectedTaget = Mockito.mock(Npc.class);
        PlayerAttackAoeEvent.melee(player, target, (AssistantKungFu) kungfu);
    }
}