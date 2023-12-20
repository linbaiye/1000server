package org.y1000.entities.managers;

import lombok.extern.slf4j.Slf4j;
import org.y1000.connection.Connection;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.Interpolation;
import org.y1000.message.I2ClientMessage;
import org.y1000.message.LoginMessage;
import org.y1000.message.Message;

import java.util.*;

@Slf4j
public final class PlayerManager extends AbstractPhysicalEntityManager<Player> {

    private final Map<Connection, Player> players;

    private final Map<Player, Deque<Interpolation>> interpolations;

    public PlayerManager() {
        players = new HashMap<>();
        interpolations = new HashMap<>();
    }

    public void add(Connection connection, Player player) {
        if (!players.containsKey(connection)) {
            indexCoordinate(player);
            interpolations.put(player, new ArrayDeque<>());
            players.put(connection, player);
            connection.write(LoginMessage.ofPlayer(player));
        }
    }

    public void remove(Connection connection) {
        Player player = players.remove(connection);
        if (player != null) {
            interpolations.remove(player);
        }
    }


    private Map<Connection, Player> findVisiblePlayers(Player source) {
        Map<Connection, Player> result = new HashMap<>();
        for (Map.Entry<Connection, Player> connectionPlayerEntry : players.entrySet()) {
            Player player = connectionPlayerEntry.getValue();
            if (player.id() == source.id()) {
                continue;
            }
            if (Math.abs(player.coordinate().x() - source.coordinate().x()) <= 32 ||
                    Math.abs(player.coordinate().y() - source.coordinate().y()) <= 32) {
                result.put(connectionPlayerEntry.getKey(), connectionPlayerEntry.getValue());
            }
        }
        return result;
    }


    private void updatePlayers(long delta) {
        for (Map.Entry<Connection, Player> entry : players.entrySet()) {
            Connection connection = entry.getKey();
            Player player = entry.getValue();
            List<Message> unprocessedMessages = connection.takeMessages();
            if (!unprocessedMessages.isEmpty()) {
                List<I2ClientMessage> ret = player.handle(unprocessedMessages);
                ret.forEach(connection::write);
            }
            List<I2ClientMessage> updatedMessages = player.update(delta);
            updatedMessages.forEach(connection::write);
            connection.flush();
        }
    }

    public void syncState() {
        for (Player value : players.values()) {
            findVisiblePlayers(value);
        }
    }

    @Override
    public List<I2ClientMessage> update(long delta, long timeMillis) {
        updatePlayers(delta);
        return Collections.emptyList();
    }
}
