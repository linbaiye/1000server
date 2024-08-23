package org.y1000.realm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.TestingEventListener;
import org.y1000.entities.Direction;
import org.y1000.entities.objects.*;
import org.y1000.entities.players.Damage;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.item.Item;
import org.y1000.sdb.*;
import org.y1000.util.Coordinate;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DynamicObjectManagerImplTest {

    private DynamicObjectManagerImpl manager;

    private DynamicObjectFactory factory;


    private EntityIdGenerator entityIdGenerator;

    private EntityEventSender entityEventSender;

    private RealmMap realmMap;

    private GroundItemManager itemManager;

    private CreateDynamicObjectSdb createDynamicObjectSdb;

    private CrossRealmEventHandler eventHandler;


    @BeforeEach
    void setUp() {
        factory = Mockito.mock(DynamicObjectFactory.class);
        entityIdGenerator = new EntityIdGenerator();
        entityEventSender = Mockito.mock(EntityEventSender.class);
        itemManager = Mockito.mock(GroundItemManager.class);
        createDynamicObjectSdb = Mockito.mock(CreateDynamicObjectSdb.class);
        eventHandler = Mockito.mock(CrossRealmEventHandler.class);
        realmMap = Mockito.mock(RealmMap.class);
        manager = new DynamicObjectManagerImpl(factory, entityIdGenerator, entityEventSender, itemManager, createDynamicObjectSdb, eventHandler, realmMap);
    }

    @Test
    void trigger() {
        DynamicObjectSdb dynamicObjectSdb = DynamicObjectSdbImpl.INSTANCE;
        TriggerDynamicObject triggerDynamicObject = TriggerDynamicObject.builder()
                .id(2L)
                .coordinate(Coordinate.xy(1, 2))
                .idName("狐狸洞门A")
                .realmMap(realmMap)
                .dynamicObjectSdb(dynamicObjectSdb)
                .build();
        manager.add(triggerDynamicObject);
        Player player = Mockito.mock(Player.class);
        var inv = new Inventory();
        when(player.coordinate()).thenReturn(triggerDynamicObject.coordinate().moveBy(Direction.RIGHT));
        when(player.inventory()).thenReturn(inv);
        Item item = Mockito.mock(Item.class);
        when(item.name()).thenReturn("骨钥匙");
        var slot = player.inventory().add(item);
        when(player.consumeItem(slot)).thenReturn(true);
        var eventListener = new TestingEventListener();
        triggerDynamicObject.registerEventListener(eventListener);
        manager.triggerDynamicObject(2L, player, slot);
        assertNotNull(eventListener.removeFirst(UpdateDynamicObjectEvent.class));
    }

    @Test
    void respawn() {
        DynamicObjectSdb dynamicObjectSdb = DynamicObjectSdbImpl.INSTANCE;
        RespawnKillableDynamicObject killable = RespawnKillableDynamicObject.builder()
                .id(2L)
                .coordinate(Coordinate.xy(1, 2))
                .idName("倒塌的壁A")
                .realmMap(realmMap)
                .dynamicObjectSdb(dynamicObjectSdb)
                .build();
        var player = Mockito.mock(Player.class);
        when(factory.createDynamicObject(anyString(), anyLong(), any(RealmMap.class), any(Coordinate.class))).thenReturn(killable);
        when(createDynamicObjectSdb.getName(anyString())).thenReturn("test");
        when(createDynamicObjectSdb.getNumbers()).thenReturn(Set.of(killable.idName()));
        manager.init();
        var eventListener = new TestingEventListener();
        killable.registerEventListener(eventListener);
        when(player.coordinate()).thenReturn(Coordinate.xy(2, 3));
        when(player.damage()).thenReturn(new Damage(100, 100, 100, 100));
        killable.attackedBy(player);
        assertNotNull(eventListener.removeFirst(UpdateDynamicObjectEvent.class));
        manager.update(dynamicObjectSdb.getOpenedMillis(killable.idName()));
        assertTrue(manager.find(2L, RespawnKillableDynamicObject.class).isEmpty());
        var interval = dynamicObjectSdb.getRegenInterval(killable.idName()) * 10;
        manager.update(interval);
        assertTrue(manager.find(2L, RespawnKillableDynamicObject.class).isPresent());
    }

}