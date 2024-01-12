package org.y1000.entities.managers;

import lombok.extern.slf4j.Slf4j;
import org.y1000.connection.Connection;
import org.y1000.connection.gen.InterpolationPacket;
import org.y1000.connection.gen.InterpolationsPacket;
import org.y1000.connection.gen.Packet;
import org.y1000.entities.players.Interpolation;
import org.y1000.entities.players.Player;
import org.y1000.message.I2ClientMessage;
import org.y1000.message.InterpolationsMessage;
import org.y1000.message.LoginMessage;
import org.y1000.message.Message;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public final class PlayerManager extends AbstractPhysicalEntityManager<Player> {

    private final Map<Connection, Player> connectionPlayerMap;

    private final Map<Player, Connection> playerConnectionMap;



    public PlayerManager() {
        connectionPlayerMap = new HashMap<>();
        playerConnectionMap = new HashMap<>();
    }

    public void add(Connection connection, Player player) {
        if (!connectionPlayerMap.containsKey(connection)) {
            indexCoordinate(player);
            connectionPlayerMap.put(connection, player);

            connection.write(LoginMessage.ofPlayer(player));
        }
    }

    public void remove(Connection connection) {
        connectionPlayerMap.remove(connection);
    }


    private Map<Connection, Player> findVisiblePlayers(Player source) {
        Map<Connection, Player> result = new HashMap<>();
        for (Map.Entry<Connection, Player> connectionPlayerEntry : connectionPlayerMap.entrySet()) {
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


    private void updatePlayers(long delta, long timeMillis) {
        for (Map.Entry<Connection, Player> entry : connectionPlayerMap.entrySet()) {
            Connection connection = entry.getKey();
            Player player = entry.getValue();
            List<Message> unprocessedMessages = connection.takeMessages();
            if (!unprocessedMessages.isEmpty()) {
                List<I2ClientMessage> ret = player.handle(unprocessedMessages);
                ret.forEach(connection::write);
            }
            List<I2ClientMessage> updatedMessages = player.update(delta, timeMillis);
            updatedMessages.forEach(connection::write);
        }
    }


    public void syncState() {
        for (Player source : connectionPlayerMap.values()) {
            if (source.interpolationDuration() < 700) {
                continue;
            }
            List<Interpolation> interpolations = source.drainInterpolations(500);
            if (interpolations.isEmpty()) {
                continue;
            }
            Map<Connection, Player> visiblePlayers = findVisiblePlayers(source);
            for (Map.Entry<Connection, Player> cv: visiblePlayers.entrySet()) {
                Connection connection = cv.getKey();
                connection.write(InterpolationsMessage.wrap(interpolations));
                connection.flush();
            }
        }
    }

    @Override
    public List<I2ClientMessage> update(long delta, long timeMillis) {
        updatePlayers(delta, timeMillis);
        return Collections.emptyList();
    }
}
