package org.y1000.realm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.TestingEntityEventSender;
import org.y1000.entities.GroundedItem;
import org.y1000.entities.players.Player;
import org.y1000.item.ItemFactory;
import org.y1000.network.Connection;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PlayerManagerTest {
    private PlayerManager playerManager;

    private EntityEventSender eventSender;

    private EntityManager<GroundedItem> itemManager;

    private ItemFactory itemFactory;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        eventSender = new TestingEntityEventSender();
        itemManager = (EntityManager<GroundedItem>)Mockito.mock(EntityManager.class);
        itemFactory = Mockito.mock(ItemFactory.class);
        playerManager = new PlayerManager(eventSender, itemManager, itemFactory);
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

}