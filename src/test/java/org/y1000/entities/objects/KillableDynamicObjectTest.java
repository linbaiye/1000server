package org.y1000.entities.objects;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.Direction;
import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.network.gen.UpdateDynamicObjectPacket;
import org.y1000.sdb.DynamicObjectSdbImpl;
import org.y1000.util.Coordinate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class KillableDynamicObjectTest extends AbstractObjectUnitTestFixture {

    private KillableDynamicObject killableDynamicObject;

    private final DynamicObjectFactory dynamicObjectFactory = new DynamicObjectFactoryImpl(DynamicObjectSdbImpl.INSTANCE);

    @BeforeEach
    void setUp() {
        setup();
        when(dynamicObjectSdb.getEStep2(anyString())).thenReturn(null);
        when(dynamicObjectSdb.getSStep2(anyString())).thenReturn(null);
        killableDynamicObject = KillableDynamicObject.builder()
                .realmMap(realmMap)
                .dynamicObjectSdb(dynamicObjectSdb)
                .coordinate(Coordinate.xy(1, 1))
                .idName(idName)
                .build();
    }

    @Test
    void attacked() {
        var player = playerBuilder().coordinate((killableDynamicObject.coordinate().moveBy(Direction.RIGHT))).build();
        killableDynamicObject.registerEventListener(eventListener);
        killableDynamicObject.attackedBy(player);
        UpdateDynamicObjectPacket update = eventListener.removeFirst(UpdateDynamicObjectEvent.class).toPacket().getUpdateDynamicObject();
        assertEquals(Integer.valueOf(dynamicObjectSdb.getSStep1(idName)), update.getStart());
        assertEquals(Integer.valueOf(dynamicObjectSdb.getEStep1(idName)), update.getEnd());
        killableDynamicObject.update(10000);
        assertNotNull(eventListener.removeFirst(RemoveEntityEvent.class));
    }
}