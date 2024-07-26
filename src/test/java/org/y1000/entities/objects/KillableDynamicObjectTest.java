package org.y1000.entities.objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.TestingEventListener;
import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.players.Damage;
import org.y1000.entities.players.Player;
import org.y1000.realm.RealmMap;
import org.y1000.sdb.DynamicObjectSdb;
import org.y1000.util.Coordinate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KillableDynamicObjectTest {

    private KillableDynamicObject object;

    private DynamicObjectSdb dynamicObjectSdb;

    private final String idName = "test";

    private RealmMap realmMap;

    private TestingEventListener eventListener;

    private Player player;

    @BeforeEach
    void setUp() {
        dynamicObjectSdb = Mockito.mock(DynamicObjectSdb.class);
        realmMap = Mockito.mock(RealmMap.class);
        when(dynamicObjectSdb.getShape(idName)).thenReturn("shape");
        when(dynamicObjectSdb.getSStep0(idName)).thenReturn("0");
        when(dynamicObjectSdb.getEStep0(idName)).thenReturn("0");
        when(dynamicObjectSdb.getSStep1(idName)).thenReturn("1");
        when(dynamicObjectSdb.getEStep1(idName)).thenReturn("4");
        when(dynamicObjectSdb.getSStep2(idName)).thenReturn("5");
        when(dynamicObjectSdb.getEStep2(idName)).thenReturn("5");
        when(dynamicObjectSdb.getViewName(idName)).thenReturn(Optional.of("testView"));
        when(dynamicObjectSdb.getArmor(idName)).thenReturn(0);
        when(dynamicObjectSdb.getLife(idName)).thenReturn(1);
        when(dynamicObjectSdb.getOpenedMillis(idName)).thenReturn(400);
        object = KillableDynamicObject.builder()
                .id(1)
                .idName(idName)
                .dynamicObjectSdb(dynamicObjectSdb)
                .realmMap(realmMap)
                .coordinate(Coordinate.xy(1, 2))
                .build();
        eventListener = new TestingEventListener();
        object.registerEventListener(eventListener);

        player = Mockito.mock(Player.class);
        when(player.coordinate()).thenReturn(object.coordinate().move(1, 1));
        when(player.damage()).thenReturn(new Damage(100, 100, 100, 100));
    }


    @Test
    void occupyCoordinates() {
        verify(realmMap, times(1)).occupy(object);
    }

    @Test
    void attackedByPlayer() {
        object.attackedBy(player);
        UpdateDynamicObjectEvent event = eventListener.removeFirst(UpdateDynamicObjectEvent.class);
        assertEquals(1, event.toPacket().getUpdateDynamicObject().getStart());
        assertEquals(4, event.toPacket().getUpdateDynamicObject().getEnd());
        assertEquals(1, event.toPacket().getUpdateDynamicObject().getId());
        object.attackedBy(player);
        assertNull(eventListener.removeFirst(UpdateDynamicObjectEvent.class));
    }

    @Test
    void update() {
        object.attackedBy(player);
        eventListener.clearEvents();
        // closed state.
        object.update(400);
        RemoveEntityEvent event = eventListener.removeFirst(RemoveEntityEvent.class);
        assertEquals(1L, event.toPacket().getRemoveEntity().getId());
    }
}