package org.y1000.entities.players;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.Direction;
import org.y1000.message.clientevent.ClientMovementEvent;
import org.y1000.message.clientevent.input.RightMouseClick;
import org.y1000.realm.Realm;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class PlayerMoveStateTest extends AbstractPlayerUnitTestFixture {

    @BeforeEach
    void setUp() {
        setup();
    }

    @Test
    void move() {
        var map = Mockito.mock(RealmMap.class);
        var realm = Mockito.mock(Realm.class);
        when(realm.map()).thenReturn(map);
        when(map.movable(any(Coordinate.class))).thenReturn(true);
        player.joinReam(realm);
        clickBasicFootKungFu();
        for (int i = 0; i < 10; i++) {
            player.handleClientEvent(new ClientMovementEvent(new RightMouseClick(1, Direction.RIGHT), player.coordinate()));
            player.update(player.getStateMillis(player.stateEnum()));
        }
        assertTrue(player.footKungFu().isPresent());
        player.footKungFu().ifPresent(k -> assertNotEquals(100, k.level()));
    }
}