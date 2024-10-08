package org.y1000.realm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.Player;
import org.y1000.realm.event.RealmTeleportEvent;
import org.y1000.util.Coordinate;
import org.y1000.util.UnaryAction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class DeadPlayerTeleportManagerImplTest {

    private DeadPlayerTeleportManagerImpl deadPlayerTeleportManager;

    private RealmTeleportEvent event;

    private void handleTeleport(RealmTeleportEvent event) {
        this.event = event;
    }

    @BeforeEach
    void setUp() {
        deadPlayerTeleportManager = new DeadPlayerTeleportManagerImpl(1, Coordinate.xy(1, 1));
        deadPlayerTeleportManager.setTeleportHandler(this::handleTeleport);
        event = null;
    }

    @Test
    void update() {
        var player = Mockito.mock(Player.class);
        when(player.stateEnum()).thenReturn(State.DIE);
        deadPlayerTeleportManager.onPlayerDead(player);
        deadPlayerTeleportManager.update(10000);
        assertNotNull(event);
    }
}