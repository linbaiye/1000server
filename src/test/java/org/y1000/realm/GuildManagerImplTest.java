package org.y1000.realm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.TestingEventListener;
import org.y1000.entities.objects.DynamicObjectFactory;
import org.y1000.entities.objects.DynamicObjectFactoryImpl;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.guild.GuildMembership;
import org.y1000.item.ItemFactory;
import org.y1000.message.PlayerTextEvent;
import org.y1000.repository.GuildRepository;
import org.y1000.repository.ItemRepository;
import org.y1000.repository.JpaFixture;
import org.y1000.sdb.DynamicObjectSdbImpl;
import org.y1000.util.Coordinate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GuildManagerImplTest extends AbstractUnitTestFixture {

    private final JpaFixture jpaFixture = new JpaFixture();
    private final DynamicObjectFactory dynamicObjectFactory = new DynamicObjectFactoryImpl(DynamicObjectSdbImpl.INSTANCE);
    private GuildManager guildManager;
    private GuildRepository guildRepository;
    private EntityEventSender entityEventSender;
    private ItemRepository itemRepository;
    private Player player;
    private Inventory inventory;
    private long playerId;
    private RealmMap realmMap;
    private final ItemFactory itemFactory = createItemFactory();

    private TestingEventListener testingEventListener;

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
        guildManager = new GuildManagerImpl(dynamicObjectFactory, new EntityIdGenerator(), entityEventSender, Mockito.mock(CrossRealmEventSender.class),
                realmMap, guildRepository, itemRepository, jpaFixture.getEntityManagerFactory(), 1);
    }

    @Test
    void foundGuildWhenCoordinateNotMovable() {
        when(realmMap.movable(any(Coordinate.class))).thenReturn(false);
        int slot = inventory.add(itemFactory.createItem("门派石"));
        guildManager.foundGuild(player, Coordinate.xy(3, 3), "test", slot);
        String text = testingEventListener.removeFirst(PlayerTextEvent.class).toPacket().getText().getText();
        assertEquals("该位置不可放置门派石。", text);
        when(realmMap.movable(Coordinate.xy(3, 3))).thenReturn(true);
        guildManager.foundGuild(player, Coordinate.xy(3, 3), "test", slot);
        text = testingEventListener.removeFirst(PlayerTextEvent.class).toPacket().getText().getText();
        assertEquals("门派石八方不可有遮挡。", text);
    }

    @Test
    void foundGuildWhenPlayerHasGuild() {
        player = playerBuilder().id(1L).inventory(inventory).guildMembership(new GuildMembership("t", "w")).build();
        player.registerEventListener(testingEventListener);
        int slot = inventory.add(itemFactory.createItem("门派石"));
        guildManager.foundGuild(player, Coordinate.xy(3, 3), "test", slot);
        String text = testingEventListener.removeFirst(PlayerTextEvent.class).toPacket().getText().getText();
        assertEquals("你已有门派。", text);
    }

    @Test
    void foundGuildWhenNameConflicts() {
        when(realmMap.movable(any(Coordinate.class))).thenReturn(true);
        when(guildRepository.countByName("test")).thenReturn(1);
        int slot = inventory.add(itemFactory.createItem("门派石"));
        guildManager.foundGuild(player, Coordinate.xy(3, 3), "test", slot);
        String text = testingEventListener.removeFirst(PlayerTextEvent.class).toPacket().getText().getText();
        assertEquals("此门派名称已存在。", text);
    }

    @Test
    void foundGuild() {
        when(realmMap.movable(any(Coordinate.class))).thenReturn(true);
        when(guildRepository.countByName("test")).thenReturn(0);
        int slot = inventory.add(itemFactory.createItem("门派石"));
        guildManager.foundGuild(player, Coordinate.xy(3, 3), "test", slot);
        String text = testingEventListener.removeFirst(PlayerTextEvent.class).toPacket().getText().getText();
    }
}