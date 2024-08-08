package org.y1000.realm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.TestingEntityEventSender;
import org.y1000.entities.players.Player;
import org.y1000.item.ItemFactory;
import org.y1000.message.clientevent.ClientTradePlayerEvent;
import org.y1000.message.clientevent.ClientTriggerDynamicObjectEvent;
import org.y1000.message.clientevent.ClientUpdateTradeEvent;
import org.y1000.realm.event.PlayerDataEvent;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class PlayerManagerImplTest {
    private PlayerManagerImpl playerManager;

    private EntityEventSender eventSender;

    private GroundItemManager itemManager;

    private ItemFactory itemFactory;

    private TradeManager tradeManager;

    private DynamicObjectManager dynamicObjectManager;

    private Realm realm = Mockito.mock(Realm.class);
    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        tradeManager = Mockito.mock(TradeManager.class);
        eventSender = new TestingEntityEventSender();
        itemManager = Mockito.mock(GroundItemManager.class);
        itemFactory = Mockito.mock(ItemFactory.class);
        dynamicObjectManager = Mockito.mock(DynamicObjectManager.class);
        playerManager = new PlayerManagerImpl(eventSender, itemManager, itemFactory, tradeManager, dynamicObjectManager);
        realm = Mockito.mock(Realm.class);
    }


    @Test
    void addNewPlayer() {
        var player = Mockito.mock(Player.class);
        when(player.id()).thenReturn(1L);
        playerManager.onPlayerConnected(player, realm);
        verify(player).joinReam(realm);
        assertTrue(playerManager.find(1L).isPresent());
    }

    @Test
    void onStartTradeEvent() {
        var player = Mockito.mock(Player.class);
        var tradee = Mockito.mock(Player.class);
        playerManager.onPlayerConnected(player, realm);
        playerManager.onPlayerConnected(tradee, realm);
        when(tradee.id()).thenReturn(1L);
        PlayerDataEvent dataEvent = new PlayerDataEvent(0, player, new ClientTradePlayerEvent(1, 2));
        playerManager.onClientEvent(dataEvent, Mockito.mock(ActiveEntityManager.class));
        verify(tradeManager, times(1)).start(any(Player.class), any(Player.class), anyInt());
    }


    @Test
    void onUpdateTradeEvent() {
        var player = Mockito.mock(Player.class);
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
        var player = Mockito.mock(Player.class);
        playerManager.onPlayerConnected(player, realm);
        PlayerDataEvent dataEvent = new PlayerDataEvent(0, player, new ClientTriggerDynamicObjectEvent(1L, 2));
        playerManager.onClientEvent(dataEvent, Mockito.mock(ActiveEntityManager.class));
        verify(dynamicObjectManager, times(1)).triggerDynamicObject(1L, player, 2);
    }
}