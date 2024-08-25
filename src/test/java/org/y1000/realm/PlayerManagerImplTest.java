package org.y1000.realm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.TestingEntityEventSender;
import org.y1000.entities.players.Player;
import org.y1000.item.ItemFactory;
import org.y1000.message.BreakRopeEvent;
import org.y1000.message.clientevent.ClientDragPlayerEvent;
import org.y1000.message.clientevent.ClientTradePlayerEvent;
import org.y1000.message.clientevent.ClientTriggerDynamicObjectEvent;
import org.y1000.message.clientevent.ClientUpdateTradeEvent;
import org.y1000.realm.event.PlayerDataEvent;
import org.y1000.util.Coordinate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class PlayerManagerImplTest extends AbstractUnitTestFixture {
    private PlayerManagerImpl playerManager;

    private EntityEventSender eventSender;

    private GroundItemManager itemManager;

    private ItemFactory itemFactory;

    private TradeManager tradeManager;

    private DynamicObjectManager dynamicObjectManager;

    private Realm realm;
    private RealmMap realmMap;
    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        tradeManager = Mockito.mock(TradeManager.class);
        eventSender = new TestingEntityEventSender();
        itemManager = Mockito.mock(GroundItemManager.class);
        itemFactory = Mockito.mock(ItemFactory.class);
        dynamicObjectManager = Mockito.mock(DynamicObjectManager.class);
        playerManager = new PlayerManagerImpl(eventSender, itemManager, itemFactory, tradeManager, dynamicObjectManager, Mockito.mock(BankManager.class));
        realmMap = mockRealmMap();
        realm = mockRealm(realmMap);
    }

    private Player mockPlayer() {
        var player = Mockito.mock(Player.class);
        when(player.coordinate()).thenReturn(Coordinate.xy(1, 1));
        when(player.getRealm()).thenReturn(realm);
        when(player.realmMap()).thenReturn(realmMap);
        return player;
    }


    @Test
    void addNewPlayer() {
        var player = mockPlayer();
        when(player.id()).thenReturn(1L);
        playerManager.onPlayerConnected(player, realm);
        verify(player).joinRealm(realm);
        assertTrue(playerManager.find(1L).isPresent());
    }

    @Test
    void onStartTradeEvent() {
        var player = mockPlayer();
        var tradee = mockPlayer();
        playerManager.onPlayerConnected(player, realm);
        playerManager.onPlayerConnected(tradee, realm);
        when(tradee.id()).thenReturn(1L);
        PlayerDataEvent dataEvent = new PlayerDataEvent(0, player, new ClientTradePlayerEvent(1, 2));
        playerManager.onClientEvent(dataEvent, Mockito.mock(ActiveEntityManager.class));
        verify(tradeManager, times(1)).start(any(Player.class), any(Player.class), anyInt());
    }


    @Test
    void onUpdateTradeEvent() {
        var player = mockPlayer();
        playerManager.onPlayerConnected(player, realm);
        PlayerDataEvent dataEvent = new PlayerDataEvent(0, player, new ClientUpdateTradeEvent(1, 2, ClientUpdateTradeEvent.ClientUpdateType.ADD_ITEM, 3));
        playerManager.onClientEvent(dataEvent, Mockito.mock(ActiveEntityManager.class));
        verify(tradeManager, times(1)).addTradeItem(any(Player.class), anyInt(), anyLong());
        dataEvent = new PlayerDataEvent(0, player, new ClientUpdateTradeEvent(1, 2, ClientUpdateTradeEvent.ClientUpdateType.REMOVE_ITEM, 3));
        playerManager.onClientEvent(dataEvent, Mockito.mock(ActiveEntityManager.class));
        verify(tradeManager, times(1)).removeTradeItem(any(Player.class), anyInt());
        dataEvent = new PlayerDataEvent(0, player, new ClientUpdateTradeEvent(1, 2, ClientUpdateTradeEvent.ClientUpdateType.CANCEL, 3));
        playerManager.onClientEvent(dataEvent, Mockito.mock(ActiveEntityManager.class));
        verify(tradeManager, times(1)).cancelTrade(any(Player.class));
        dataEvent = new PlayerDataEvent(0, player, new ClientUpdateTradeEvent(1, 2, ClientUpdateTradeEvent.ClientUpdateType.CONFIRM, 3));
        playerManager.onClientEvent(dataEvent, Mockito.mock(ActiveEntityManager.class));
        verify(tradeManager, times(1)).confirmTrade(any(Player.class));
    }

    @Test
    void onClientTriggerDynamicObject() {
        var player = mockPlayer();
        playerManager.onPlayerConnected(player, realm);
        PlayerDataEvent dataEvent = new PlayerDataEvent(0, player, new ClientTriggerDynamicObjectEvent(1L, 2));
        playerManager.onClientEvent(dataEvent, Mockito.mock(ActiveEntityManager.class));
        verify(dynamicObjectManager, times(1)).triggerDynamicObject(1L, player, 2);
    }

    @Test
    void drag() {
        var player1 = mockPlayer();
        when(player1.id()).thenReturn(1L);
        playerManager.onPlayerConnected(player1, realm);
        var player2 = mockPlayer();
        when(player2.id()).thenReturn(2L);
        playerManager.onPlayerConnected(player2, realm);
        when(player1.canDrag(player2, 1)).thenReturn(true);
        PlayerDataEvent dataEvent = new PlayerDataEvent(0, player1, new ClientDragPlayerEvent(2L, 1));
        playerManager.onClientEvent(dataEvent, Mockito.mock(ActiveEntityManager.class));
        verify(player1, times(1)).consumeItem(1);

        var player3 = mockPlayer();
        when(player3.id()).thenReturn(3L);
        playerManager.onPlayerConnected(player3, realm);
        when(player3.canDrag(player2, 1)).thenReturn(true);
        dataEvent = new PlayerDataEvent(0, player3, new ClientDragPlayerEvent(2L, 1));
        playerManager.onClientEvent(dataEvent, Mockito.mock(ActiveEntityManager.class));
        verify(player3, times(1)).consumeItem(1);
        verify(player2, times(1)).emitEvent(any(BreakRopeEvent.class));
    }
}