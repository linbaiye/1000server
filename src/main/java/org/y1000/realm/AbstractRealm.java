package org.y1000.realm;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.y1000.entities.npc.NpcInteractAbility;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.PlayerTextMessage;
import org.y1000.entities.teleport.TeleportEventHandler;
import org.y1000.input.*;
import org.y1000.realm.event.InteractableCoordinateNameMessage;
import org.y1000.network.Connection;
import org.y1000.realm.event.*;
import org.y1000.repository.PlayerRepository;
import org.y1000.sdb.MapSdb;
import org.y1000.util.Coordinate;

import java.util.ArrayList;
import java.util.List;

abstract class AbstractRealm implements Realm, TeleportEventHandler, RealmEventHandler  {
    public static final int STEP_MILLIS = 10;
    private final RealmMap realmMap;
    private final NpcManager npcManager;
    private final PlayerManager playerManager;
    private final DynamicObjectManager dynamicObjectManager;
    private final TeleportManager teleportManager;

    private final int id;
    private final RealmEventSender crossRealmEventSender;
    private final MapSdb mapSdb;
    private long accumulatedMillis;
    private final List<ActiveEntityManager<?>> entityManagers;
    private final PlayerRepository playerRepository;

    public AbstractRealm(int id,
                         RealmMap realmMap,
                         GroundItemManager itemManager,
                         NpcManager npcManager,
                         PlayerManager playerManager,
                         DynamicObjectManager dynamicObjectManager,
                         TeleportManager teleportManager,
                         RealmEventSender crossRealmEventSender,
                         MapSdb mapSdb,
                         PlayerRepository playerRepository) {
        Validate.notNull(realmMap);
        Validate.notNull(itemManager);
        Validate.notNull(playerManager);
        Validate.notNull(crossRealmEventSender);
        Validate.notNull(mapSdb);
        this.realmMap = realmMap;
        this.npcManager = npcManager;
        this.playerManager = playerManager;
        this.dynamicObjectManager = dynamicObjectManager;
        this.teleportManager = teleportManager;
        this.id = id;
        this.crossRealmEventSender = crossRealmEventSender;
        this.mapSdb = mapSdb;
        this.entityManagers = new ArrayList<>();
        entityManagers.add(playerManager);
        entityManagers.add(itemManager);
        entityManagers.add(dynamicObjectManager);
        entityManagers.add(npcManager);
        this.playerRepository = playerRepository;
    }

    void addEntityManager(ActiveEntityManager<?> manager) {
        entityManagers.add(manager);
    }

    public RealmMap map() {
        return realmMap;
    }

    public String title() {
        return mapSdb.getMapTitle(id);
    }

    public String bgm() {
        return mapSdb.getSoundBase(id);
    }

    abstract Logger log();

    PlayerManager getPlayerManager() {
        return playerManager;
    }


    void doUpdateEntities() {
        long current = System.currentTimeMillis();
        while (accumulatedMillis <= current) {
            entityManagers.forEach(m -> m.update(STEP_MILLIS));
            teleportManager.update();
            accumulatedMillis += STEP_MILLIS;
        }
    }

    @Override
    public void announceDungeonOpen(String announcement) {
        playerManager.allPlayers().forEach(
                p -> p.sendEvent(PlayerTextMessage.leftUp(p, announcement))
        );
    }


    protected void doInit() {
        try {
            accumulatedMillis = System.currentTimeMillis();
            npcManager.init();
            dynamicObjectManager.init();
            teleportManager.init(this );
            log().debug("Initialized {}.", this);
        } catch (Exception e) {
            log().error("Failed to init realm {}.", id, e);
            throw new RuntimeException(e);
        }
    }

    MapSdb getMapSdb() {
        return mapSdb;
    }

    public int id() {
        return id;
    }

    @Override
    public void teleportIn(Player player, Coordinate toCoordinate, Connection connection) {
        playerManager.teleportIn(player, this, toCoordinate, connection);
    }

    @Override
    public void teleportTo(Player player, int toReam, Coordinate toCoordinate) {
        Connection connection = playerManager.prepareTeleport(player);
        if (connection == null)
            return;
        RealmTeleportEvent teleportEvent = RealmTeleportEvent.toDestination(player, toReam, toCoordinate, connection);
        crossRealmEventSender.send(teleportEvent);
    }

    PlayerManager playerManager() {
        return playerManager;
    }

    abstract void handleGuildCreation(Player source, ClientFoundGuildEvent event);

    protected abstract void handleLogin(Login login);

    void acceptLogin(long playerId, Connection connection, Coordinate coordinate) {
        playerRepository.load(playerId).ifPresent(p -> {
            if (coordinate == null)
                getPlayerManager().loginPlayer(p, this, connection);
            else
                getPlayerManager().loginPlayer(p, this, coordinate, connection);
        });
    }

    private void handleEntityInteraction(Player player, EntityInteractInput interactionInput) {
        for (ActiveEntityManager<?> entityManager : entityManagers) {
            entityManager.find(interactionInput.interactId())
                    .ifPresent(e -> interactionInput.onEntityFound(player, e));
        }
    }

    private void handleRealmInput(Connection connection, RealmInput input) {
        if (playerManager.find(connection).isEmpty())
            return;
        if (input.type() == RealmInput.Type.GetNpcCoordinates) {
            var npcSet = npcManager.find(npc -> npc.findAbility(NpcInteractAbility.class).isPresent());
            var staticTeleports = teleportManager.findStaticTeleports();
            connection.writeAndFlush(new InteractableCoordinateNameMessage(npcSet, staticTeleports));
        }
    }

    private void handleInput(ConnectionInput connectionInput) {
        if (connectionInput.input() instanceof SelfHandleInput selfHandleInput) {
            playerManager().handleInput(connectionInput.connection(), selfHandleInput);
        } else if (connectionInput.input() instanceof EntityInteractInput interactionInput) {
            playerManager().find(connectionInput.connection()).ifPresent(p -> handleEntityInteraction(p, interactionInput));
        } else if (connectionInput.input() instanceof RealmInput realmInput) {
            handleRealmInput(connectionInput.connection(), realmInput);
        }
    }

    private void handleLogout(Logout logout) {
        if (logout.playerId() != null) {
            playerManager().find(logout.playerId()).ifPresent(playerManager()::logoutPlayer);
        } else {
            playerManager.logoutPlayer(logout.connection());
        }
    }

    @Override
    public void handle(Object event) {
        try {
            if (event instanceof Login login) {
                handleLogin(login);
            } else if (event instanceof Logout logout) {
                handleLogout(logout);
            } else if (event instanceof ConnectionInput connectionInput) {
                handleInput(connectionInput);
            } else if (event instanceof RealmEvent realmEvent) {
                realmEvent.accept(this);
            }
        } catch (Exception e) {
            log().error("Failed to handle event.", e);
        }
    }

    @Override
    public void broadcastText(BroadcastTextEvent event) {
        getPlayerManager().allPlayers().forEach(player -> player.sendEvent(event.createMessage(player)));
    }

    @Override
    public void deliverPrivateChat(DeliveryPrivateChatEvent event) {
        boolean found = false;
        for (Player player: getPlayerManager().allPlayers()) {
            if (player.viewName().equals(event.getToPlayerName())) {
                found = true;
                player.sendEvent(PlayerTextMessage.privateChat(player, event.formatDeliveredContent()));
                crossRealmEventSender.send(DeliveryPrivateChatResultEvent.delivered(event));
                break;
            }
        }
        if (!found)
            crossRealmEventSender.send(DeliveryPrivateChatResultEvent.notFound(event));
    }

    @Override
    public void deliverPrivateChatResult(long playerId, String reply) {
        getPlayerManager().find(playerId)
                .ifPresent(player -> player.sendEvent(PlayerTextMessage.privateChat(player, reply)));
    }
    @Override
    public void handleProxiedLogin(long playerId, Coordinate toCoordinate, Connection connection) {
        acceptLogin(playerId, connection, toCoordinate);
    }
}
