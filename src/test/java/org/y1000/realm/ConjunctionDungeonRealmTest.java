package org.y1000.realm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.network.Connection;
import org.y1000.realm.event.RealmTeleportEvent;
import org.y1000.util.Coordinate;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ConjunctionDungeonRealmTest extends AbstractRealmUnitTextFixture {


    @BeforeEach
    void setUp() {
        setup();
    }

    @Test
    void alwaysEnter() {
        var dungeonRealm = new ConjunctionDungeonRealm(1, realmMap, eventSender, itemManager, npcManager, playerManager, dynamicObjectManager, teleportManager, crossRealmEventSender, mapSdb,
                360000, chatManager);
        PlayerImpl player = playerBuilder().build();
        Connection connection = Mockito.mock(Connection.class);
        RealmTeleportEvent realmTeleportEvent = new RealmTeleportEvent(player, 1, Coordinate.xy(1, 1), connection, 1);
        dungeonRealm.handle(realmTeleportEvent);
        verify(playerManager, times(1)).teleportIn(any(Player.class), any(Realm.class), any(Coordinate.class));

    }
}