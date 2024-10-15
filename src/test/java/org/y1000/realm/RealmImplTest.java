package org.y1000.realm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.creatures.npc.InteractableNpc;
import org.y1000.entities.creatures.npc.Merchant;
import org.y1000.entities.players.Player;
import org.y1000.message.clientevent.ClientSimpleCommandEvent;
import org.y1000.message.clientevent.SimpleCommand;
import org.y1000.message.serverevent.NpcPositionEvent;
import org.y1000.network.Connection;
import org.y1000.network.event.ConnectionEstablishedEvent;
import org.y1000.realm.event.PlayerDataEvent;
import org.y1000.realm.event.RealmTeleportEvent;
import org.y1000.util.Coordinate;

import java.util.Collections;
import java.util.Set;

import static org.mockito.Mockito.*;

class RealmImplTest extends AbstractRealmUnitTextFixture {

    private RealmImpl realm;

    private Player player;
    private Connection connection;

    @BeforeEach
    void setUp() {
        setup();
        player = Mockito.mock(Player.class);
        realm = new RealmImpl(1, realmMap, eventSender, itemManager, npcManager, playerManager, dynamicObjectManager, teleportManager, crossRealmEventSender, mapSdb, chatManager);
        connection = Mockito.mock(Connection.class);
        realm.handle(new ConnectionEstablishedEvent(realm.id(), player, connection));
    }

    @Test
    void handleNpcPositionEvent() {
        when(npcManager.findMerchants()).thenReturn(Collections.emptySet());
        realm.handle(new PlayerDataEvent(1, player, new ClientSimpleCommandEvent(SimpleCommand.NPC_POSITION)));
        verify(connection, times(0)).write(any(NpcPositionEvent.class));
        InteractableNpc m1 = Mockito.mock(InteractableNpc.class);
        when(m1.coordinate()).thenReturn(Coordinate.xy(1, 1));
        when(m1.viewName()).thenReturn("m1");
        InteractableNpc m2 = Mockito.mock(InteractableNpc.class);
        when(m2.coordinate()).thenReturn(Coordinate.xy(2, 2));
        when(m2.viewName()).thenReturn("m2");
        when(npcManager.findMerchants()).thenReturn(Set.of(m1, m2));
        realm.handle(new PlayerDataEvent(1, player, new ClientSimpleCommandEvent(SimpleCommand.NPC_POSITION)));
        verify(connection, times(1)).write(any(NpcPositionEvent.class));
    }
}
