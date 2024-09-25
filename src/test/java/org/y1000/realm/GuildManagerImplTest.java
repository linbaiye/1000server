package org.y1000.realm;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.TestingEventListener;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.Entity;
import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.objects.DynamicObjectDieEvent;
import org.y1000.entities.objects.DynamicObjectFactory;
import org.y1000.entities.objects.DynamicObjectFactoryImpl;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.event.EntityEvent;
import org.y1000.guild.GuildMembership;
import org.y1000.guild.GuildStone;
import org.y1000.item.ItemFactory;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.RemoveEntityMessage;
import org.y1000.realm.event.BroadcastTextEvent;
import org.y1000.realm.event.DismissGuildEvent;
import org.y1000.repository.GuildRepository;
import org.y1000.repository.ItemRepository;
import org.y1000.repository.JpaFixture;
import org.y1000.sdb.DynamicObjectSdbImpl;
import org.y1000.util.Coordinate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GuildManagerImplTest extends AbstractUnitTestFixture {

    private final DynamicObjectFactory dynamicObjectFactory = createDynamicObjectFactory();
    private GuildManager guildManager;
    private GuildRepository guildRepository;
    private EntityEventSender entityEventSender;
    private Player player;
    private Inventory inventory;
    private RealmMap realmMap;
    private final ItemFactory itemFactory = createItemFactory();

    private TestingEventListener testingEventListener;

    private List<GuildStone> stoneList;
    private CrossRealmEventSender crossRealmEventSender;

    @BeforeEach
    void setUp() {
        guildRepository = Mockito.mock(GuildRepository.class);
        entityEventSender = Mockito.mock(EntityEventSender.class);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        testingEventListener = new TestingEventListener();
        inventory = new Inventory();
        player = playerBuilder().id(1L).coordinate(Coordinate.xy(2, 2)).inventory(inventory).build();
        player.registerEventListener(testingEventListener);
        realmMap = Mockito.mock(RealmMap.class);
        var entityIdGenerator = new EntityIdGenerator();
        EntityManagerFactory entityManagerFactory = mock(EntityManagerFactory.class);
        EntityManager em = mock(EntityManager.class);
        when(entityManagerFactory.createEntityManager()).thenReturn(em);
        when(em.getTransaction()).thenReturn(Mockito.mock(EntityTransaction.class));
        crossRealmEventSender = mock(CrossRealmEventSender.class);
        guildManager = new GuildManagerImpl(dynamicObjectFactory, entityIdGenerator, entityEventSender, crossRealmEventSender,
                realmMap, guildRepository, itemRepository, entityManagerFactory, 1);
        stoneList = new ArrayList<>();
        when(guildRepository.findByRealm(anyInt(), any(EntityIdGenerator.class), any(RealmMap.class))).thenReturn(stoneList);
    }

    @Test
    void foundGuildWhenCoordinateNotMovable() {
        when(realmMap.movable(any(Coordinate.class))).thenReturn(false);
        int slot = inventory.add(itemFactory.createItem("门派石"));
        guildManager.foundGuild(player, Coordinate.xy(3, 3), "test", slot);
        String text = testingEventListener.removeFirst(PlayerTextEvent.class).toPacket().getText().getText();
        assertEquals("该位置不可放置门派石。", text);
        when(realmMap.movable(Coordinate.xy(3, 3))).thenReturn(true);
        when(realmMap.tileMovable(any(Coordinate.class))).thenReturn(false);
        guildManager.foundGuild(player, Coordinate.xy(3, 3), "test", slot);
        text = testingEventListener.removeFirst(PlayerTextEvent.class).toPacket().getText().getText();
        assertEquals("门派石八方不可有遮挡。", text);
    }

    @Test
    void foundGuildWhenPlayerHasGuild() {
        player = playerBuilder().id(1L).inventory(inventory).guildMembership(new GuildMembership(1, "t", "w")).build();
        player.registerEventListener(testingEventListener);
        int slot = inventory.add(itemFactory.createItem("门派石"));
        guildManager.foundGuild(player, Coordinate.xy(3, 3), "test", slot);
        String text = testingEventListener.removeFirst(PlayerTextEvent.class).toPacket().getText().getText();
        assertEquals("你已有门派。", text);
    }

    @Test
    void foundGuildWhenNameConflicts() {
        when(realmMap.movable(any(Coordinate.class))).thenReturn(true);
        when(realmMap.tileMovable(any(Coordinate.class))).thenReturn(true);
        when(guildRepository.countByName("test")).thenReturn(1);
        int slot = inventory.add(itemFactory.createItem("门派石"));
        guildManager.foundGuild(player, Coordinate.xy(3, 3), "test", slot);
        String text = testingEventListener.removeFirst(PlayerTextEvent.class).toPacket().getText().getText();
        assertEquals("此门派名称已存在。", text);
    }

    @Test
    void foundGuild() {
        when(realmMap.movable(any(Coordinate.class))).thenReturn(true);
        when(realmMap.tileMovable(any(Coordinate.class))).thenReturn(true);
        when(guildRepository.countByName("test")).thenReturn(0);
        doAnswer(invocationOnMock -> {
            ((GuildStone)invocationOnMock.getArgument(1)).setPersistentId(1);
            return null;
        }).when(guildRepository).save(any(EntityManager.class), any(GuildStone.class), anyLong());
        int slot = inventory.add(itemFactory.createItem("门派石"));
        guildManager.foundGuild(player, Coordinate.xy(3, 3), "test", slot);
        Optional<GuildStone> guildStone = guildManager.find(1L);
        assertTrue(guildStone.isPresent());
        verify(entityEventSender, times(1)).add(guildStone.get());
        verify(guildRepository, times(1)).save(any(EntityManager.class), any(GuildStone.class), anyLong());
        String text = testingEventListener.removeFirst(PlayerTextEvent.class).toPacket().getText().getText();
        assertTrue(text.contains("恭喜你，你已成为"));
        assertNull(inventory.getItem(slot));
        verify(guildRepository, times(1)).upsertMembership(any(EntityManager.class), anyLong(), any(GuildMembership.class));
    }

    @Test
    void onGuildStoneDieEvent() {
        GuildStone stone = dynamicObjectFactory.createGuildStone(1, "test", 1, realmMap, Coordinate.xy(1, 1));
        stone.setPersistentId(1);
        stoneList.add(stone);
        guildManager.init();
        guildManager.onEvent(new DynamicObjectDieEvent(stone));
        assertTrue(guildManager.find(1).isEmpty());
        verify(entityEventSender, times(1)).remove(stone);
        verify(crossRealmEventSender, times(1)).send(any(BroadcastTextEvent.class));
        verify(crossRealmEventSender, times(1)).send(any(DismissGuildEvent.class));
        verify(entityEventSender, times(1)).notifyVisiblePlayers(any(Entity.class), any(RemoveEntityMessage.class));
        verify(guildRepository, times(1)).deleteGuildAndMembership(anyInt());
    }
}