package org.y1000.realm;

import org.y1000.entities.Entity;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.PlayerEvent;
import org.y1000.entities.players.event.PlayerJoinRealmMessage;
import org.y1000.entities.players.event.PlayerLetFlyProjectileEvent;
import org.y1000.entities.players.event.PlayerTeleportMessage;
import org.y1000.item.Item;
import org.y1000.network.I2ClientMessage;
import org.y1000.entities.RemoveEntityMessage;
import org.y1000.input.SelfHandleInput;
import org.y1000.network.Connection;
import org.y1000.realm.event.RealmEvent;
import org.y1000.repository.PlayerRepository;
import org.y1000.util.Coordinate;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

abstract class AbstractPlayerManager extends AbstractActiveEntityManager<Player> implements PlayerManager,
        PlayerEventListener, PlayerEventHandler {

    protected final RealmPlayerConnectionManager connectionManager;

    protected final GroundItemManager itemManager;

    protected final ProjectileManager projectileManager;

    protected final PlayerRepository playerRepository;

    protected final RealmEventSender crossRealmEventSender;

    public AbstractPlayerManager(AOIManager aoiManager,
                                 RealmPlayerConnectionManager connectionManager,
                                 GroundItemManager itemManager,
                                 PlayerRepository playerRepository,
                                 RealmEventSender crossRealmEventSender) {
        super(aoiManager, connectionManager);
        this.connectionManager = connectionManager;
        this.itemManager = itemManager;
        this.projectileManager = new ProjectileManager();
        this.playerRepository = playerRepository;
        this.crossRealmEventSender = crossRealmEventSender;
    }

    public void sendToVisiblePlayersAndSelf(Player source, I2ClientMessage message) {
        sendToVisiblePlayers(source, message);
        sendTo(source, message);
    }

    public void logoutPlayer(Player player) {
        if (player == null)
            return;
        // Need to update first lest losing realm id.
        playerRepository.update(player);
        player.leaveRealm();
        sendToVisiblePlayers(player, new RemoveEntityMessage(player.id()));
        connectionManager.remove(player).ifPresent(Connection::tryClose);
        remove(player);
    }

    private void doPlayerJoinRealm(Player player, Runnable joinAction, Connection connection,
                                   Function<Player, I2ClientMessage> characterMsgCreator) {
        connectionManager.add(player, connection);
        joinAction.run();
        sendTo(player, characterMsgCreator.apply(player));
        add(player);
        I2ClientMessage snapshot = player.captureSnapshot();
        getAoiManager().filterVisibleEntities(player, Entity.class).forEach(entity -> {
            sendTo(player, entity.captureSnapshot());
            if (entity instanceof Player another) {
                sendTo(another, snapshot);
            }
        });
    }

    @Override
    public void loginPlayer(Player player, Realm realm, Coordinate coordinate, Connection connection) {
        doPlayerJoinRealm(player, () -> player.joinRealm(realm, coordinate, this), connection, PlayerJoinRealmMessage::of);
    }

    @Override
    public void loginPlayer(Player player, Realm realm, Connection connection) {
        doPlayerJoinRealm(player, () -> player.joinRealm(realm, this), connection, PlayerJoinRealmMessage::of);
    }

    @Override
    public void logoutPlayer(Connection connection) {
        if (connection == null)
            return;
        connectionManager.findPlayer(connection).ifPresent(this::logoutPlayer);
    }

    @Override
    public void teleportIn(Player player,
                           Realm realm, Coordinate coordinate,
                           Connection connection) {
        doPlayerJoinRealm(player, () -> player.joinRealm(realm, coordinate, this), connection, PlayerTeleportMessage::of);
    }

    @Override
    public Connection prepareTeleport(Player player) {
        player.leaveRealm();
        sendToVisiblePlayers(player, new RemoveEntityMessage(player.id()));
        Connection connection = connectionManager.remove(player).orElse(null);
        remove(player);
        return connection;
    }

    void updatePlayersAndProjectiles(long delta) {
        updateManagedEntities(delta);
        projectileManager.update(delta);
    }

    @Override
    public Set<Player> allPlayers() {
        return getEntities();
    }

    @Override
    public void handleInput(Connection connection, SelfHandleInput input) {
        connectionManager.findPlayer(connection).ifPresent(p -> p.handleInput(input));
    }

    @Override
    public Optional<Player> find(Connection connection) {
        return connectionManager.findPlayer(connection);
    }

    @Override
    public void shutdown() {
        allPlayers().forEach(playerRepository::update);
    }


    public void sendTo(Player player, I2ClientMessage message) {
        connectionManager.sendTo(player, message);
    }

    public void updateAOI(Player player) {
        Set<Entity> affected = getAoiManager().update(player);
        affected.forEach(entity -> {
            if (!entity.canBeSeenAt(player.coordinate())) {
                sendTo(player, new RemoveEntityMessage(entity.id()));
                if (entity instanceof Player another) {
                    sendTo(another, new RemoveEntityMessage(player.id()));
                }
            } else {
                getMessageSender().sendTo(player, entity.captureSnapshot());
                if (entity instanceof Player another) {
                    getMessageSender().sendTo(another, player.captureSnapshot());
                }
            }
        });
    }


    @Override
    public void onPlayerFireProjectile(PlayerLetFlyProjectileEvent event) {
        projectileManager.add(event.getProjectile());
        sendToVisiblePlayersAndSelf(event.source(), event);
    }

    @Override
    public void dropItem(Item item, Coordinate droppedAt) {
        itemManager.dropItem(item, droppedAt);
    }

    @Override
    public void sendCrossRealmEvent(RealmEvent event) {
        crossRealmEventSender.send(event);
    }

    @Override
    public void onEvent(PlayerEvent event) {
        event.accept(this);
    }
}
