package org.y1000.realm.event;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.players.Player;
import org.y1000.entities.teleport.TeleportCost;
import org.y1000.network.Connection;
import org.y1000.util.Coordinate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RealmTeleportEventTest {

    private TeleportCost createCost(String check) {
        return new TeleportCost() {
            @Override
            public String check(Player player) {
                return check;
            }

            @Override
            public void charge(Player player) {

            }
        };
    }

    @Test
    void whenCostNotAffordable() {
        Player player = Mockito.mock(Player.class);
        RealmTeleportEvent event = new RealmTeleportEvent(player, 1, Coordinate.xy(1, 1), Mockito.mock(Connection.class), 1, Coordinate.xy(2, 2), List.of(createCost(null), createCost("no money")));
        assertEquals("no money", event.checkCost());
        event = new RealmTeleportEvent(player, 1, Coordinate.xy(1, 1), Mockito.mock(Connection.class), 1, Coordinate.xy(2, 2), null);
        assertNull(event.checkCost());
        event = new RealmTeleportEvent(player, 1, Coordinate.xy(1, 1), Mockito.mock(Connection.class), 1, Coordinate.xy(2, 2), List.of(createCost(null)));
        assertNull(event.checkCost());
    }
}