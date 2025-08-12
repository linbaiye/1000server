package org.y1000.realm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.players.Player;
import org.y1000.network.I2ClientMessage;
import org.y1000.network.Connection;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class RealmConnectionManagerTests {

    private RealmPlayerConnectionManager connectionManager;

    @BeforeEach
    void setUp() {
        connectionManager = new RealmPlayerConnectionManager();
    }

    @Test
    void add() {
        Player player = Mockito.mock(Player.class);
        Connection connection = Mockito.mock(Connection.class);
        connectionManager.add(player, connection);
        assertTrue(connectionManager.remove(player).isPresent());
    }

    @Test
    void findPlayer() {
        assertFalse(connectionManager.findPlayer(null).isPresent());
        Player player = Mockito.mock(Player.class);
        Connection connection = Mockito.mock(Connection.class);
        assertFalse(connectionManager.findPlayer(connection).isPresent());
        connectionManager.add(player, connection);
        assertTrue(connectionManager.findPlayer(connection).isPresent());
    }

    @Test
    void remove() {
        assertFalse(connectionManager.remove(null).isPresent());
        Player player = Mockito.mock(Player.class);
        Connection connection = Mockito.mock(Connection.class);
        connectionManager.add(player, connection);
        assertTrue(connectionManager.remove(player).isPresent());
        assertFalse(connectionManager.findPlayer(connection).isPresent());
    }

    @Test
    void sendTo() {
        Player player = Mockito.mock(Player.class);
        Connection connection = Mockito.mock(Connection.class);
        connectionManager.add(player, connection);
        I2ClientMessage message = Mockito.mock(I2ClientMessage.class);
        connectionManager.sendTo(player, message);
        verify(connection, times(1)).writeAndFlush(message);
    }
}
