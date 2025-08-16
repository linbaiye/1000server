package org.y1000.guild;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.entities.Direction;
import org.y1000.entities.objects.DynamicObjectFactory;
import org.y1000.entities.players.Damage;
import org.y1000.entities.players.Player;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import static org.mockito.Mockito.*;


class GuildStoneTest extends AbstractUnitTestFixture {

    private DynamicObjectFactory dynamicObjectFactory = createDynamicObjectFactory();
    private RealmMap realmMap;

    @BeforeEach
    void setUp() {
        realmMap = Mockito.mock(RealmMap.class);
    }

    @Test
    void attacked() {
        var stone = dynamicObjectFactory.createGuildStone(1L, "test",1,  realmMap, Coordinate.xy(1, 1));
        var player = Mockito.mock(Player.class);
        when(player.coordinate()).thenReturn(stone.coordinate().moveBy(Direction.RIGHT));
        when(player.damage()).thenReturn(new Damage(stone.getMaxLife() / 2, 1, 1,1));
        verify(realmMap, times(1)).free(any(Guild.class));
    }

}