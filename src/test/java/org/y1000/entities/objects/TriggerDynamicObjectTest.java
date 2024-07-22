package org.y1000.entities.objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.TestingEventListener;
import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.item.Item;
import org.y1000.network.gen.ShowDynamicObjectPacket;
import org.y1000.network.gen.UpdateDynamicObjectPacket;
import org.y1000.realm.RealmMap;
import org.y1000.sdb.DynamicObjectSdb;
import org.y1000.util.Coordinate;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TriggerDynamicObjectTest {

    private DynamicObjectSdb dynamicObjectSdb;

    private final String idName = "test";

    private TriggerDynamicObject object;

    private RealmMap realmMap;

    private TestingEventListener eventListener;

    private Player player;

    @BeforeEach
    void setUp() {
        dynamicObjectSdb = Mockito.mock(DynamicObjectSdb.class);
        realmMap = Mockito.mock(RealmMap.class);
        when(dynamicObjectSdb.getShape(idName)).thenReturn("shape");
        when(dynamicObjectSdb.getEventItem(idName)).thenReturn("key:1");
        when(dynamicObjectSdb.getSStep0(idName)).thenReturn("0");
        when(dynamicObjectSdb.getEStep0(idName)).thenReturn("0");
        when(dynamicObjectSdb.getSStep1(idName)).thenReturn("1");
        when(dynamicObjectSdb.getEStep1(idName)).thenReturn("4");
        when(dynamicObjectSdb.getSStep2(idName)).thenReturn("5");
        when(dynamicObjectSdb.getEStep2(idName)).thenReturn("5");
        when(dynamicObjectSdb.getViewName(idName)).thenReturn(Optional.of("testView"));
        object = TriggerDynamicObject.builder()
                .id(1)
                .idName(idName)
                .dynamicObjectSdb(dynamicObjectSdb)
                .realmMap(realmMap)
                .coordinate(Coordinate.xy(1, 2))
                .build();
        eventListener = new TestingEventListener();
        object.registerEventListener(eventListener);

        player = Mockito.mock(Player.class);
        Inventory inventory = new Inventory();
        Item item = Mockito.mock(Item.class);
        when(item.name()).thenReturn("key");
        var slot = inventory.add(item);
        when(player.inventory()).thenReturn(inventory);
        when(player.consumeItem(slot)).thenReturn(true);
    }

    @Test
    void trigger() {
        object.trigger(player, 1);
        UpdateDynamicObjectPacket packet = eventListener.removeFirst(UpdateDynamicObjectEvent.class).toPacket().getUpdateDynamicObject();
        assertEquals(object.id(), packet.getId());
        assertEquals(1, packet.getStart());
        assertEquals(4, packet.getEnd());
        assertEquals(1, object.currentAnimation().frameStart());
        assertEquals(4, object.currentAnimation().frameEnd());
    }

    @Test
    void update() {
        object.trigger(player, 1);
        eventListener.clearEvents();
        when(dynamicObjectSdb.getOpenedInterval(idName)).thenReturn(6000);
        when(dynamicObjectSdb.isRemove(idName)).thenReturn(true);
        object.update(object.currentAnimation().total() * 500);
        UpdateDynamicObjectPacket packet = eventListener.removeFirst(UpdateDynamicObjectEvent.class).toPacket().getUpdateDynamicObject();
        assertEquals(object.id(), packet.getId());
        assertEquals(5, packet.getStart());
        assertEquals(5, packet.getEnd());
        assertEquals(5, object.currentAnimation().frameStart());
        assertEquals(5, object.currentAnimation().frameEnd());
        object.update(6000);
        assertEquals(object.id(), eventListener.removeFirst(RemoveEntityEvent.class).toPacket().getRemoveEntity().getId());
        verify(realmMap, times(1)).free(object);
    }

    @Test
    void interpolation() {
        ShowDynamicObjectPacket showDynamicObject = object.captureInterpolation().toPacket().getShowDynamicObject();
        assertEquals(object.id(), showDynamicObject.getId());
        assertEquals(DynamicObjectType.TRIGGER.value(), showDynamicObject.getType());
        assertEquals(object.coordinate().x(), showDynamicObject.getX());
        assertEquals(object.coordinate().y(), showDynamicObject.getY());
        assertEquals(0, showDynamicObject.getStart());
        assertEquals(0, showDynamicObject.getEnd());
        assertEquals("testView", showDynamicObject.getName());
        assertEquals("shape", showDynamicObject.getShape());
        object.trigger(player, 1);
        showDynamicObject = object.captureInterpolation().toPacket().getShowDynamicObject();
        assertEquals(1, showDynamicObject.getStart());
        assertEquals(4, showDynamicObject.getEnd());
        object.update(120);
        showDynamicObject = object.captureInterpolation().toPacket().getShowDynamicObject();
        assertEquals(120, showDynamicObject.getElapsed());
    }
}