package org.y1000.realm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.TestingEntityEventSender;
import org.y1000.entities.GroundedItem;
import org.y1000.entities.players.Player;
import org.y1000.item.ItemFactory;
import org.y1000.message.clientevent.ClientEvent;
import org.y1000.message.clientevent.ClientTradePlayerEvent;
import org.y1000.message.clientevent.ClientUpdateTradeEvent;
import org.y1000.network.Connection;
import org.y1000.realm.event.PlayerDataEvent;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class PlayerManagerTest {
    private PlayerManager playerManager;

    private EntityEventSender eventSender;

    private GroundItemManager itemManager;

    private ItemFactory itemFactory;

    private TradeManager tradeManager;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        tradeManager = Mockito.mock(TradeManager.class);
        eventSender = new TestingEntityEventSender();
        itemManager = Mockito.mock(GroundItemManager.class);
        itemFactory = Mockito.mock(ItemFactory.class);
        playerManager = new PlayerManager(eventSender, itemManager, itemFactory, tradeManager);
    }


    @Test
    void addNewPlayer() {
        var player = Mockito.mock(Player.class);
        when(player.id()).thenReturn(1L);
        var connection = Mockito.mock(Connection.class);
        var realm = Mockito.mock(Realm.class);
        playerManager.onPlayerConnected(player, connection, realm);
        verify(player).joinReam(realm);
        assertTrue(playerManager.find(1L).isPresent());
    }

    @Test
    void onStartTradeEvent() {
        var player = Mockito.mock(Player.class);
        var tradee = Mockito.mock(Player.class);
        playerManager.add(tradee);
        when(tradee.id()).thenReturn(1L);
        PlayerDataEvent dataEvent = new PlayerDataEvent(player, new ClientTradePlayerEvent(1, 2));
        playerManager.onPlayerEvent(dataEvent, Mockito.mock(EntityManager.class));
        verify(tradeManager, times(1)).start(any(Player.class), any(Player.class), anyInt());
    }


    @Test
    void onUpdateTradeEvent() {
        var player = Mockito.mock(Player.class);
        PlayerDataEvent dataEvent = new PlayerDataEvent(player, new ClientUpdateTradeEvent(1, 2, ClientUpdateTradeEvent.ClientUpdateType.ADD_ITEM, 3));
        playerManager.onPlayerEvent(dataEvent, Mockito.mock(EntityManager.class));
        verify(tradeManager, times(1)).addTradeItem(any(Player.class), anyInt(), anyLong());
        dataEvent = new PlayerDataEvent(player, new ClientUpdateTradeEvent(1, 2, ClientUpdateTradeEvent.ClientUpdateType.REMOVE_ITEM, 3));
        playerManager.onPlayerEvent(dataEvent, Mockito.mock(EntityManager.class));
        verify(tradeManager, times(1)).removeTradeItem(any(Player.class), anyInt());
        dataEvent = new PlayerDataEvent(player, new ClientUpdateTradeEvent(1, 2, ClientUpdateTradeEvent.ClientUpdateType.CANCEL, 3));
        playerManager.onPlayerEvent(dataEvent, Mockito.mock(EntityManager.class));
        verify(tradeManager, times(1)).cancelTrade(any(Player.class));
        dataEvent = new PlayerDataEvent(player, new ClientUpdateTradeEvent(1, 2, ClientUpdateTradeEvent.ClientUpdateType.CONFIRM, 3));
        playerManager.onPlayerEvent(dataEvent, Mockito.mock(EntityManager.class));
        verify(tradeManager, times(1)).confirmTrade(any(Player.class));
    }

}