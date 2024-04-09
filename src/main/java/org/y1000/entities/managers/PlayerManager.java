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
            playerConnectionMap.put(player, connection);
        }
    }

    public void remove(Connection connection) {
        Player removed = connectionPlayerMap.remove(connection);
        if (removed != null) {
            scopeMap.remove(removed);
            playerConnectionMap.remove(removed);
        }
    }

    private void updateScopes() {
        for (Player player1 : scopeMap.keySet()) {
            PlayerVisibleScope player1Scope = scopeMap.get(player1);
            player1Scope.clearNewlyAdded();
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


    public void sendEvents(Map<Player, List<ServerEvent>> events) {
        for (Player player : events.keySet()) {
            Connection connection = playerConnectionMap.get(player);
            List<ServerEvent> messages = events.get(player);
            connection.write(messages);
            events.get(player).forEach(e -> {
                log.debug("Wrote message {} to player {}.", e, player.id());
            });
        }
    }

    private void updatePlayerEvents(Map<Player, List<ServerEvent>> events) {
        for (Player player : scopeMap.keySet()) {
            PlayerVisibleScope scope = scopeMap.get(player);
            for (Player newlyAddedPlayer : scope.getNewlyAddedPlayers()) {
                events.computeIfAbsent(newlyAddedPlayer, p -> new ArrayList<>());
                events.get(newlyAddedPlayer).add(player.captureInterpolation());
            }
        }

        for (Player player: events.keySet()) {
            PlayerVisibleScope scope = scopeMap.get(player);
            Set<Player> visiblePlayers = scope.getNonNewlyAddedPlayers();
            List<ServerEvent> currentPlayerEvents = events.get(player);
            for (Player another: visiblePlayers) {
                events.computeIfAbsent(another, p -> new ArrayList<>());
                List<ServerEvent> anotherPlayerEvents = events.get(another);
                currentPlayerEvents.forEach(e ->
                        e.eventToPlayer(another.id()).ifPresent(anotherPlayerEvents::add)
                );
            }
        }
    }

    @Override
    public void update(long delta) {
        Map<Player, List<ServerEvent>> events = updatePlayers(delta);
        updateScopes();
        updatePlayerEvents(events);
        sendEvents(events);
    }
}
