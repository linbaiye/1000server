package org.y1000.realm;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.TestingEventListener;
import org.y1000.entities.Entity;
import org.y1000.entities.objects.DynamicObjectDieEvent;
import org.y1000.entities.objects.DynamicObjectFactory;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.AbstractPlayerEvent;
import org.y1000.entities.players.event.PlayerLearnKungFuEvent;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.event.EntityEvent;
import org.y1000.guild.GuildMembership;
import org.y1000.guild.GuildStone;
import org.y1000.item.ItemFactory;
import org.y1000.kungfu.KungFuBook;
import org.y1000.kungfu.KungFuSdb;
import org.y1000.kungfu.KungFuType;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.kungfu.attack.AttackKungFuType;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.RemoveEntityMessage;
import org.y1000.message.clientevent.ClientCreateGuildKungFuEvent;
import org.y1000.message.serverevent.UpdateGuildKungFuFormEvent;
import org.y1000.persistence.AttackKungFuParametersProvider;
import org.y1000.realm.event.BroadcastTextEvent;
import org.y1000.realm.event.DismissGuildEvent;
import org.y1000.realm.event.GuildBroadcastTextEvent;
import org.y1000.repository.*;
import org.y1000.util.Coordinate;

import java.util.ArrayList;
import java.util.HashMap;
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
    private KungFuBookRepository kungFuBookRepository;

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
        kungFuBookRepository = Mockito.mock(KungFuBookRepository.class);
        guildManager = new GuildManagerImpl(dynamicObjectFactory, entityIdGenerator, entityEventSender, crossRealmEventSender,
                realmMap, guildRepository, itemRepository, entityManagerFactory, 1, KungFuSdb.INSTANCE, kungFuBookRepository);
        stoneList = new ArrayList<>();
        when(guildRepository.findByRealm(anyInt(), any(EntityIdGenerator.class), any(RealmMap.class))).thenReturn(stoneList);
    }

    @Test
    void foundGuildWhenCoordinateNotMovable() {
        when(realmMap.movable(any(Coordinate.class))).thenReturn(false);
        int slot = inventory.put(itemFactory.createItem("门派石"));
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
        int slot = inventory.put(itemFactory.createItem("门派石"));
        guildManager.foundGuild(player, Coordinate.xy(3, 3), "test", slot);
        String text = testingEventListener.removeFirst(PlayerTextEvent.class).toPacket().getText().getText();
        assertEquals("你已有门派。", text);
    }

    @Test
    void foundGuildWhenNameConflicts() {
        when(realmMap.movable(any(Coordinate.class))).thenReturn(true);
        when(realmMap.tileMovable(any(Coordinate.class))).thenReturn(true);
        when(guildRepository.countByName("test")).thenReturn(1);
        int slot = inventory.put(itemFactory.createItem("门派石"));
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
        int slot = inventory.put(itemFactory.createItem("门派石"));
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

    @Test
    void createGuildKungFuWhenNotFounder() {
        ClientCreateGuildKungFuEvent event = ClientCreateGuildKungFuEvent.builder().name("test").type(AttackKungFuType.QUANFA)
                        .build();
        var founder = Mockito.mock(Player.class);
        when(founder.guildMembership()).thenReturn(Optional.empty());
        guildManager.createGuildKungFu(founder, event);
        verify(kungFuBookRepository, times(0)).saveGuildKungFuParameter(any(AttackKungFuParametersProvider.class), anyInt());
    }
    @Test
    void createGuildKungFuWhenNotCorrect() {
        ClientCreateGuildKungFuEvent event = ClientCreateGuildKungFuEvent.builder().name("test").type(AttackKungFuType.QUANFA)
                .speed(10)
                .bodyDamage(70)
                .avoid(50)
                .recovery(50)
                .headDamage(40)
                .armDamage(40)
                .legDamage(40)
                .bodyArmor(40)
                .headArmor(40)
                .armArmor(14)
                .legArmor(14)
                .innerPowerToSwing(20)
                .outerPowerToSwing(20)
                .powerToSwing(20)
                .lifeToSwing(20)
                .build();
        var founder = Mockito.mock(Player.class);
        when(founder.guildMembership())
                .thenReturn(Optional.of(new GuildMembership(1, "门主", "test")));
        guildManager.createGuildKungFu(founder, event);
        verify(kungFuBookRepository, times(0)).saveGuildKungFuParameter(any(AttackKungFuParametersProvider.class), anyInt());
        verify(entityEventSender, times(1)).notifySelf(any(UpdateGuildKungFuFormEvent.class));
    }

    @Test
    void createGuildKungFuWhenCorrect() {
        ClientCreateGuildKungFuEvent event = ClientCreateGuildKungFuEvent.builder().name("test").type(AttackKungFuType.QUANFA)
                .speed(30)
                .bodyDamage(70)
                .avoid(50)
                .recovery(50)
                .headDamage(40)
                .armDamage(40)
                .legDamage(40)
                .bodyArmor(40)
                .headArmor(40)
                .armArmor(14)
                .legArmor(14)
                .innerPowerToSwing(20)
                .outerPowerToSwing(20)
                .powerToSwing(20)
                .lifeToSwing(20)
                .build();
        var founder = Mockito.mock(Player.class);
        when(founder.guildMembership())
                .thenReturn(Optional.of(new GuildMembership(1, "门主", "test")));
        guildManager.createGuildKungFu(founder, event);
        verify(kungFuBookRepository, times(1)).saveGuildKungFuParameter(any(AttackKungFuParametersProvider.class), anyInt());
        verify(entityEventSender, times(1)).notifySelf(any(UpdateGuildKungFuFormEvent.class));
        verify(entityEventSender, times(1)).notifySelf(any(PlayerTextEvent.class));
    }


    @Test
    void inviteMember() {
        GuildStone stone = dynamicObjectFactory.createGuildStone(1, "test", 1, realmMap, Coordinate.xy(1, 1));
        stone.setPersistentId(1);
        stoneList.add(stone);
        guildManager.init();
        Player founder = Mockito.mock(Player.class);
        Player invitee = Mockito.mock(Player.class);
        TestingEventListener founderEvents = new TestingEventListener();
        TestingEventListener inviteeEvents = new TestingEventListener();
        doAnswer(invocationOnMock -> {
            EntityEvent argument = invocationOnMock.getArgument(0);
            if (argument.source() == founder)
                founderEvents.onEvent(argument);
            else
                inviteeEvents.onEvent(argument);
            return null; }).when(entityEventSender).notifySelf(any(AbstractPlayerEvent.class));
        guildManager.inviteMember(founder, invitee);
        var text = founderEvents.removeFirst(PlayerTextEvent.class).toPacket().getText().getText();
        assertTrue(text.contains("没有门派"));
        when(founder.guildMembership()).thenReturn(Optional.of(new GuildMembership(stone.getPersistentId(), "门主", "test")));
        when(founder.coordinate()).thenReturn(Coordinate.xy(4, 4));
        guildManager.inviteMember(founder, invitee);
        text = founderEvents.removeFirst(PlayerTextEvent.class).toPacket().getText().getText();
        assertTrue(text.contains("门派石距离"));
        when(founder.coordinate()).thenReturn(Coordinate.xy(2, 2));
        when(invitee.coordinate()).thenReturn(Coordinate.xy(5, 5));
        guildManager.inviteMember(founder, invitee);
        text = founderEvents.removeFirst(PlayerTextEvent.class).toPacket().getText().getText();
        assertTrue(text.contains("玩家距离"));
        when(invitee.coordinate()).thenReturn(Coordinate.xy(3, 3));
        when(invitee.viewName()).thenReturn("invitee");
        when(invitee.guildMembership()).thenReturn(Optional.of(new GuildMembership(3, "t", "t")));
        guildManager.inviteMember(founder, invitee);
        text = founderEvents.removeFirst(PlayerTextEvent.class).toPacket().getText().getText();
        assertTrue(text.contains("invitee已有门派"));
        when(invitee.guildMembership()).thenReturn(Optional.empty());
        guildManager.inviteMember(founder, invitee);
        verify(invitee, times(1)).joinGuild(any(GuildMembership.class));
        verify(crossRealmEventSender, times(1)).send(any(GuildBroadcastTextEvent.class));
    }

    @Test
    void teachGuildKungFu() {
        GuildStone stone = dynamicObjectFactory.createGuildStone(1, "test", 1, realmMap, Coordinate.xy(1, 1));
        stone.setPersistentId(1);
        stoneList.add(stone);
        guildManager.init();
        Player founder = Mockito.mock(Player.class);
        Player invitee = Mockito.mock(Player.class);
        TestingEventListener founderEvents = new TestingEventListener();
        TestingEventListener inviteeEvents = new TestingEventListener();
        doAnswer(invocationOnMock -> {
            EntityEvent argument = invocationOnMock.getArgument(0);
            if (argument.source() == founder)
                founderEvents.onEvent(argument);
            else
                inviteeEvents.onEvent(argument);
            return null; }).when(entityEventSender).notifySelf(any(AbstractPlayerEvent.class));
        guildManager.teachGuildKungFu(founder, invitee);
        var text = founderEvents.removeFirst(PlayerTextEvent.class).toPacket().getText().getText();
        assertTrue(text.contains("没有门派"));
        when(founder.guildMembership()).thenReturn(Optional.of(new GuildMembership(stone.getPersistentId(), "门主", "test")));
        when(founder.coordinate()).thenReturn(Coordinate.xy(4, 4));
        guildManager.teachGuildKungFu(founder, invitee);
        text = founderEvents.removeFirst(PlayerTextEvent.class).toPacket().getText().getText();
        assertTrue(text.contains("门派石距离"));
        when(founder.coordinate()).thenReturn(Coordinate.xy(2, 2));
        when(invitee.coordinate()).thenReturn(Coordinate.xy(5, 5));
        guildManager.teachGuildKungFu(founder, invitee);
        text = founderEvents.removeFirst(PlayerTextEvent.class).toPacket().getText().getText();
        assertTrue(text.contains("玩家距离"));
        when(invitee.coordinate()).thenReturn(Coordinate.xy(3, 3));
        when(invitee.viewName()).thenReturn("invitee");
        when(invitee.guildMembership()).thenReturn(Optional.of(new GuildMembership(stone.getPersistentId() + 1, "", "test1")));
        guildManager.teachGuildKungFu(founder, invitee);
        text = founderEvents.removeFirst(PlayerTextEvent.class).toPacket().getText().getText();
        assertTrue(text.contains("门派不同"));

        var kf = mock(AttackKungFu.class);
        when(kf.kungFuType()).thenReturn(KungFuType.SWORD);
        when(kf.name()).thenReturn("test");
        when(kungFuBookRepository.findGuildKungfu(anyInt())).thenReturn(Optional.of(kf));
        when(invitee.guildMembership()).thenReturn(Optional.of(new GuildMembership(stone.getPersistentId(), "", "test")));
        KungFuBook book = new KungFuBook(new HashMap<>());
        when(invitee.kungFuBook()).thenReturn(book);
        guildManager.teachGuildKungFu(founder, invitee);
        assertNotNull(inviteeEvents.removeFirst(PlayerLearnKungFuEvent.class));
    }
}