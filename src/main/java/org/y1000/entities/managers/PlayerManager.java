package org.y1000.entities.managers;

import lombok.extern.slf4j.Slf4j;
import org.y1000.connection.Connection;
import org.y1000.entities.players.Player;
import org.y1000.message.*;

import java.util.*;

@Slf4j
public final class PlayerManager extends AbstractPhysicalEntityManager<Player> implements
        ServerEventListener<ServerEvent>,
        ServerEventVisitor {

    private final Map<Connection, Player> connectionPlayerMap;

    private final Map<Player, Connection> playerConnectionMap;

    private final Map<Player, PlayerVisibleScope> scopeMap;

    private final Map<Long, Player> id2Player;

    public PlayerManager() {
        connectionPlayerMap = new HashMap<>(512);
        playerConnectionMap = new HashMap<>(512);
        scopeMap = new HashMap<>(512);
        id2Player = new HashMap<>(512);
    }

    public void add(Connection connection, Player player) {
        if (!connectionPlayerMap.containsKey(connection)) {
            indexCoordinate(player);
            connectionPlayerMap.put(connection, player);
            playerConnectionMap.put(player, connection);
            id2Player.put(player.id(), player);
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

    }

    private Map<Player, List<ServerEvent>> updatePlayers(long delta) {
        Map<Player, List<ServerEvent>> result = new HashMap<>();
        for (Map.Entry<Connection, Player> entry : connectionPlayerMap.entrySet()) {
            List<ServerEvent> playerEvents = new ArrayList<>();
            Connection connection = entry.getKey();
            Player player = entry.getValue();
//            List<ClientEvent> unprocessedMessages = connection.takeMessages();
            player.addAll(connection.takeMessages());
            player.update(delta);
//            if (!unprocessedMessages.isEmpty()) {
//                playerEvents.addAll(player.handle(unprocessedMessages));
//            }
//            playerEvents.addAll(player.update(delta));
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

    private void broadcastAppearance(Player source) {
        for (Player another: scopeMap.keySet()) {
            if (another.equals(source)) {
                continue;
            }
            PlayerVisibleScope playerVisibleScope = scopeMap.get(another);
            if (playerVisibleScope.addIfVisible(source)) {
                log.debug("Players {} and {} see each other now.", source, another);
                playerConnectionMap.get(another).write(source.captureInterpolation());
                playerConnectionMap.get(source).write(another.captureInterpolation());
            }
        }
    }

    @Override
    public void visit(LoginMessage loginMessage) {
        if (id2Player.containsKey(loginMessage.id())) {
            Player player = id2Player.get(loginMessage.id());
            scopeMap.put(player, new PlayerVisibleScope(player));
            broadcastAppearance(player);
        }
    }

    @Override
    public void visit(InputResponseMessage inputResponseMessage) {
        playerConnectionMap.get(inputResponseMessage.player())
                .write(inputResponseMessage);
        visit(inputResponseMessage.positionMessage());
    }

    @Override
    public void visit(AbstractPositionEvent positionEvent) {
        for (Player player: scopeMap.keySet()) {
            if (player.equals(positionEvent.source())) {
                continue;
            }
            PlayerVisibleScope playerVisibleScope = scopeMap.get(player);
            if (playerVisibleScope.removeIfOutOfView(positionEvent.source())) {
                //
            } else if (playerVisibleScope.contains(positionEvent.source())){
                playerConnectionMap.get(player).write(positionEvent);
            } else if (playerVisibleScope.addIfVisible(positionEvent.source())) {
            }
        }
    }

    @Override
    public void update(long delta) {
        Map<Player, List<ServerEvent>> events = updatePlayers(delta);
        updateScopes();
        updatePlayerEvents(events);
        sendEvents(events);
        connectionPlayerMap.keySet().forEach(Connection::flush);
    }

    @Override
    public void OnEvent(ServerEvent event) {
        event.accept(this);
    }
}
