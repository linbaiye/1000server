package org.y1000.entities.managers;

import lombok.extern.slf4j.Slf4j;
import org.y1000.connection.Connection;
import org.y1000.entities.creatures.players.Player;
import org.y1000.message.Message;
import org.y1000.realm.Realm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public final class PlayerManager extends AbstractPhysicalEntityManager<Player> {

    private final Map<Connection, Player> players;

    public PlayerManager() {
        players = new HashMap<>();
    }

    public void add(Connection connection, Realm realm) {
        if (!players.containsKey(connection)) {
            players.put(connection, Player.ofRealm(realm));
        }
    }

    public void remove(Connection connection) {
        players.remove(connection);
    }

    @Override
    public List<Message> update(long delta) {
        List<Message> messages = new ArrayList<>();
        players.values().forEach(p -> p.update(delta).ifPresent(messages::add));
        for (Map.Entry<Connection, Player> entry : players.entrySet()) {
            Connection connection = entry.getKey();
            List<Message> unprocessedMessages = connection.takeMessages();
            if (!unprocessedMessages.isEmpty()) {
                entry.getValue().handle(unprocessedMessages);
            }
        }
        return messages;
    }
}
