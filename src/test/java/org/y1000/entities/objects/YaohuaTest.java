package org.y1000.entities.objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.players.Player;
import org.y1000.realm.RealmMap;
import org.y1000.sdb.DynamicObjectSdbImpl;
import org.y1000.util.Coordinate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

class YaohuaTest {

    private Yaohua yaohua;

    private DynamicObjectFactory dynamicObjectFactory;

    private RealmMap realmMap;

    private TriggerDynamicObject foxfire;


    @BeforeEach
    void setUp() {
        dynamicObjectFactory = new DynamicObjectFactoryImpl(DynamicObjectSdbImpl.INSTANCE);
        realmMap = Mockito.mock(RealmMap.class);
        yaohua = (Yaohua) dynamicObjectFactory.createDynamicObject("妖华", 1L, realmMap, Coordinate.xy(1, 2));
        foxfire = (TriggerDynamicObject) dynamicObjectFactory.createDynamicObject("狐狸火", 2L, realmMap, Coordinate.xy(2, 5));
    }

    @Test
    void canBeAttacked() {
        assertFalse(yaohua.canBeAttackedNow());
        var player = Mockito.mock(Player.class);
        //when(player.coordinate()).thenReturn()
        //foxfire.trigger();
        //foxfire.changeAnimation();
//        yaohua.onEvent(new UpdateDynamicObjectEvent());
    }
}