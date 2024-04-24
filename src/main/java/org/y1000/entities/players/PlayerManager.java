package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.network.Connection;
import org.y1000.entities.PlayerVisibleScope;
import org.y1000.entities.creatures.Creature;
import org.y1000.entities.managers.EntityManager;
import org.y1000.message.*;
import org.y1000.message.serverevent.*;
import java.util.*;

@Slf4j
public final class PlayerManager implements
        EntityManager<Player>,
        EntityEventListener,
        PlayerEventHandler {

    private final Map<Connection, Player> connectionPlayerMap;
    private final Map<Player, Connection> playerConnectionMap;
    private final Map<Player, PlayerVisibleScope> scopeMap;


    public PlayerManager() {
        int initialCapacity = 512;
        connectionPlayerMap = new HashMap<>(initialCapacity);
        playerConnectionMap = new HashMap<>(initialCapacity);
        scopeMap = new HashMap<>(initialCapacity);
    }


    public void add(Connection connection, Player player) {
        if (!connectionPlayerMap.containsKey(connection)) {
            connectionPlayerMap.put(connection, player);
            playerConnectionMap.put(player, connection);
        }
    }


    public void remove(Connection connection) {
        Player removed = connectionPlayerMap.remove(connection);
        if (removed != null) {
            removed.leaveRealm();
            playerConnectionMap.remove(removed);
        }
    }


    private void updatePlayers(long delta) {
        for (Map.Entry<Connection, Player> entry : connectionPlayerMap.entrySet()) {
            Connection connection = entry.getKey();
            Player player = entry.getValue();
            player.addAll(connection.takeMessages());
            player.update(delta);
        }
    }

    public Optional<PlayerVisibleScope> getVisibleScope(Player player) {
        return Optional.ofNullable(scopeMap.get(player));
    }


    private void broadcastAppearance(Player source) {
        for (Player another: scopeMap.keySet()) {
            if (another.equals(source)) {
                continue;
            }
            PlayerVisibleScope playerVisibleScope = scopeMap.get(another);
            if (playerVisibleScope.addIfVisible(source)) {
                log.debug("Players {} and {} see each other now.", source.id(), another.id());
                scopeMap.get(source).addIfVisible(another);
                playerConnectionMap.get(another).write(source.captureInterpolation());
                playerConnectionMap.get(source).write(another.captureInterpolation());
            }
        }
    }


    @Override
    public void handle(LoginSucceededEvent loginMessage) {
        if (!scopeMap.containsKey(loginMessage.player())) {
            log.debug("Logged in player {}.", loginMessage.player());
            scopeMap.put(loginMessage.player(), new PlayerVisibleScope(loginMessage.player()));
            broadcastAppearance(loginMessage.player());
            playerConnectionMap.get(loginMessage.player()).write(loginMessage);
        }
    }


    @Override
    public void handle(InputResponseMessage inputResponseMessage) {
        playerConnectionMap.get(inputResponseMessage.player())
                .write(inputResponseMessage);
        handle(inputResponseMessage.positionMessage());
    }


    public void sendVisibleCreatures(Player player, Set<Creature> creatures) {
        Connection connection = playerConnectionMap.get(player);
        creatures.stream()
                .map(Creature::captureInterpolation)
                .forEach(connection::write);
    }


    @Override
    public void handle(AbstractPositionEvent positionEvent) {
        for (Player player: scopeMap.keySet()) {
            if (player.equals(positionEvent.source())) {
                continue;
            }
            PlayerVisibleScope playerVisibleScope = scopeMap.get(player);
            if (playerVisibleScope.removeIfOutOfView(positionEvent.source())) {
                playerConnectionMap.get(player).write(new RemoveEntityMessage(positionEvent.id()));
            } else if (playerVisibleScope.contains(positionEvent.source())){
                playerConnectionMap.get(player).write(positionEvent);
            } else if (playerVisibleScope.addIfVisible(positionEvent.source())) {
                playerConnectionMap.get(player).write(positionEvent);
            }
        }
    }

    @Override
    public void update(long delta) {
        updatePlayers(delta);
        connectionPlayerMap.keySet().forEach(Connection::flush);
    }


    @Override
    public void OnEvent(EntityEvent entityEvent) {
        entityEvent.accept(this);
    }
}
