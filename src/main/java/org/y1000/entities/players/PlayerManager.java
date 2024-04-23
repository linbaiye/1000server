package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.connection.Connection;
import org.y1000.entities.RelevanceScope;
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
    private final Map<Player, RelevanceScope> scopeMap;


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

    public Optional<RelevanceScope> getVisibleScope(Player player) {
        return Optional.ofNullable(scopeMap.get(player));
    }


    private void broadcastAppearance(Player source) {
        for (Player another: scopeMap.keySet()) {
            if (another.equals(source)) {
                continue;
            }
            RelevanceScope relevanceScope = scopeMap.get(another);
            if (relevanceScope.addIfVisible(source)) {
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
            scopeMap.put(loginMessage.player(), new RelevanceScope(loginMessage.player()));
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
            RelevanceScope relevanceScope = scopeMap.get(player);
            if (relevanceScope.removeIfOutOfView(positionEvent.source())) {
                playerConnectionMap.get(player).write(new RemoveEntityMessage(positionEvent.id()));
            } else if (relevanceScope.contains(positionEvent.source())){
                playerConnectionMap.get(player).write(positionEvent);
            } else if (relevanceScope.addIfVisible(positionEvent.source())) {
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
