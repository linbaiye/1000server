package org.y1000.entities.players.event;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.players.Damage;
import org.y1000.entities.players.Player;
import org.y1000.kungfu.AssistantKungFu;
import org.y1000.util.Coordinate;

import java.util.Collections;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class PlayerAttackAoeEventTest extends AbstractUnitTestFixture  {
    @Test
    void affectBy5Directions() {
        var kungfu = kungFuFactory.create("风灵旋");
        Player player = Mockito.mock(Player.class);
        when(player.direction()).thenReturn(Direction.DOWN);
        when(player.coordinate()).thenReturn(Coordinate.xy(2, 2));
        when(player.hit()).thenReturn(1);
        when(player.damage()).thenReturn(Damage.DEFAULT);
        when(player.attackKungFu()).thenReturn(kungFuFactory.createAttackKungFu("无名剑法"));

        Npc mainTarget = Mockito.mock(Npc.class);
        when(mainTarget.attackedByAoe(any(ViolentCreature.class), anyInt(), any(Damage.class))).thenReturn(1);
        when(mainTarget.coordinate()).thenReturn(Coordinate.xy(2, 3));

        Npc affectedTaget = Mockito.mock(Npc.class);
        when(affectedTaget.attackedByAoe(any(ViolentCreature.class), anyInt(), any(Damage.class))).thenReturn(1);
        when(affectedTaget.coordinate()).thenReturn(Coordinate.xy(1, 3));

        PlayerAttackAoeEvent event = PlayerAttackAoeEvent.melee(player, mainTarget, (AssistantKungFu) kungfu);
        event.affect(Collections.singleton(affectedTaget));
        Mockito.verify(mainTarget, times(1)).attackedByAoe(any(ViolentCreature.class), anyInt(), any(Damage.class));
        Mockito.verify(affectedTaget, times(1)).attackedByAoe(any(ViolentCreature.class), anyInt(), any(Damage.class));
    }
}