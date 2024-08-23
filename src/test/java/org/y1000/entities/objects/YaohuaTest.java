package org.y1000.entities.objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.TestingEventListener;
import org.y1000.entities.Direction;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.event.CrossRealmEvent;
import org.y1000.item.ItemFactory;
import org.y1000.item.ItemSdbImpl;
import org.y1000.kungfu.KungFuFactory;
import org.y1000.realm.RealmMap;
import org.y1000.realm.event.BroadcastSoundEvent;
import org.y1000.realm.event.RealmTriggerEvent;
import org.y1000.repository.ItemRepositoryImpl;
import org.y1000.sdb.DynamicObjectSdbImpl;
import org.y1000.sdb.ItemDrugSdbImpl;
import org.y1000.util.Coordinate;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class YaohuaTest extends AbstractUnitTestFixture {

    private Yaohua yaohua;

    private DynamicObjectFactory dynamicObjectFactory;

    private RealmMap realmMap;

    private Set<DynamicObject> foxfires;

    private ItemFactory itemFactory;

    private TestingEventListener eventListener;


    @BeforeEach
    void setUp() {
        itemFactory = new ItemRepositoryImpl(ItemSdbImpl.INSTANCE, ItemDrugSdbImpl.INSTANCE, Mockito.mock(KungFuFactory.class));
        dynamicObjectFactory = new DynamicObjectFactoryImpl(DynamicObjectSdbImpl.INSTANCE);
        realmMap = Mockito.mock(RealmMap.class);
        yaohua = (Yaohua) dynamicObjectFactory.createDynamicObject("妖华", 1L, realmMap, Coordinate.xy(1, 2));
        foxfires = new HashSet<>();
        for (int i = 0; i < 4; i++) {
            var foxfire = (TriggerDynamicObject) dynamicObjectFactory.createDynamicObject("狐狸火", 2L + i, realmMap, Coordinate.xy(2, 5));
            foxfires.add(foxfire);
        }
        eventListener = new TestingEventListener();
        yaohua.subscribe(foxfires);
        yaohua.registerEventListener(eventListener);
    }

    @Test
    void canBeAttacked() {
        assertFalse(yaohua.canBeAttackedNow());
        PlayerImpl player = playerBuilder().coordinate(Coordinate.xy(2, 6)).build();
        int slot = player.inventory().add(itemFactory.createItem("火石", 4));
        foxfires.forEach(dynamicObject -> ((TriggerDynamicObject)dynamicObject).trigger(player, slot));
        assertTrue(yaohua.canBeAttackedNow());
    }

    @Test
    void attackedByPlayer() {
        Player player = playerBuilder().coordinate(Coordinate.xy(2, 6)).build();
        int slot = player.inventory().add(itemFactory.createItem("火石", 4));
        foxfires.forEach(dynamicObject -> ((TriggerDynamicObject)dynamicObject).trigger(player, slot));
        var attacker = playerBuilder().coordinate(yaohua.coordinate().moveBy(Direction.RIGHT)).build();
        while (yaohua.currentLife() > 0)
            yaohua.attackedBy(attacker);
        assertInstanceOf(BroadcastSoundEvent.class, eventListener.removeFirst(CrossRealmEvent.class).realmEvent());
        var realmEvent = (RealmTriggerEvent)eventListener.removeFirst(CrossRealmEvent.class).realmEvent();
        assertEquals(1, realmEvent.realmId());
        assertEquals("九尾狐酒母", realmEvent.toName());
    }
}