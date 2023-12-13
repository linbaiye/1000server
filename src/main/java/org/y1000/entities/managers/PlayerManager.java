package org.y1000.entities.managers;

import lombok.extern.slf4j.Slf4j;
import org.y1000.connection.Connection;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.Interpolation;
import org.y1000.message.I2ClientMessage;
import org.y1000.message.LoginMessage;
import org.y1000.message.Message;
import org.y1000.realm.Realm;
import org.y1000.util.Coordinate;

import java.util.*;

@Slf4j
public final class PlayerManager extends AbstractPhysicalEntityManager<Player> {

    private final Map<Connection, Player> players;

    private final Map<Player, Deque<Interpolation>> interpolations;

    public PlayerManager() {
        players = new HashMap<>();
        interpolations = new HashMap<>();
    }

    public void add(Connection connection, Realm realm, long timeMillis) {
        if (!players.containsKey(connection)) {
            Player player = Player.ofRealm(realm, new Coordinate(37, 31));
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

    @Override
    public List<I2ClientMessage> update(long delta) {
        return Collections.emptyList();
    }



    @Override
    public List<I2ClientMessage> update(long delta, long timeMillis) {
        List<I2ClientMessage> messages = new ArrayList<>();
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
        return messages;
    }
}
