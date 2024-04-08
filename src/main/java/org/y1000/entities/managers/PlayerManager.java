package org.y1000.entities.managers;

import lombok.extern.slf4j.Slf4j;
import org.y1000.connection.Connection;
import org.y1000.entities.players.Player;
import org.y1000.message.ServerEvent;
import org.y1000.message.clientevent.ClientEvent;

import java.util.*;

@Slf4j
public final class PlayerManager extends AbstractPhysicalEntityManager<Player> {

    private final Map<Connection, Player> connectionPlayerMap;

    private final Map<Player, Connection> playerConnectionMap;

    private final Map<Player, PlayerVisibleScope> scopeMap;

    public PlayerManager() {
        connectionPlayerMap = new HashMap<>(512);
        playerConnectionMap = new HashMap<>(512);
        scopeMap = new HashMap<>(512);
    }

    public void add(Connection connection, Player player) {
        if (!connectionPlayerMap.containsKey(connection)) {
            indexCoordinate(player);
            connectionPlayerMap.put(connection, player);
            scopeMap.put(player, new PlayerVisibleScope(player));
        }
    }

    public void remove(Connection connection) {
        Player removed = connectionPlayerMap.remove(connection);
        if (removed != null) {
            scopeMap.remove(removed);
            playerConnectionMap.remove(removed);
        }
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


    private void UpdateScope() {
        for (Player player1 : scopeMap.keySet()) {
            PlayerVisibleScope player1Scope = scopeMap.get(player1);
            player1Scope.update();
            for (Player player2 : scopeMap.keySet()) {
                if (player1.equals(player2)) {
                    continue;
                }
                player1Scope.addIfVisible(player2);
            }
        }
    }

    private Map<Player, List<ServerEvent>> updatePlayers(long delta) {
        Map<Player, List<ServerEvent>> result = new HashMap<>();
        for (Map.Entry<Connection, Player> entry : connectionPlayerMap.entrySet()) {
            List<ServerEvent> playerEvents = new ArrayList<>();
            Connection connection = entry.getKey();
            Player player = entry.getValue();
            List<ClientEvent> unprocessedMessages = connection.takeMessages();
            if (!unprocessedMessages.isEmpty()) {
                playerEvents.addAll(player.handle(unprocessedMessages));
            }
            playerEvents.addAll(player.update(delta));
            result.put(entry.getValue(), playerEvents);
        }
        return result;
    }



    public void syncState() {
        for (Player source : connectionPlayerMap.values()) {
//            if (source.interpolationDuration() < 700) {
//                continue;
//            }
//            List<Interpolation> interpolations = source.drainInterpolations(500);
//            if (interpolations.isEmpty()) {
//                continue;
//            }
            Map<Connection, Player> visiblePlayers = findVisiblePlayers(source);
            for (Map.Entry<Connection, Player> cv: visiblePlayers.entrySet()) {
                Connection connection = cv.getKey();
                //connection.write(InterpolationsMessage.wrap(interpolations));
                //connection.flush();
            }
        }
    }

    private void

    @Override
    public void update(long delta) {
        Map<Player, List<ServerEvent>> events = updatePlayers(delta);
    }
}
