package org.y1000.realm;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.creatures.npc.InteractableNpc;
import org.y1000.entities.players.Player;
import org.y1000.entities.teleport.StaticTeleport;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.input.*;
import org.y1000.message.input.chat.ClientInputTextEvent;
import org.y1000.message.serverevent.NpcPositionEvent;
import org.y1000.network.event.ConnectionEstablishedEvent;
import org.y1000.realm.event.*;
import org.y1000.repository.PlayerRepository;
import org.y1000.sdb.MapSdb;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

abstract class AbstractRealm implements Realm {
    public static final int STEP_MILLIS = 10;
    private final RealmMap realmMap;
    private final RealmEntityEventSender eventSender;
    private final NpcManager npcManager;
    private final PlayerManager playerManager;
    private final DynamicObjectManager dynamicObjectManager;
    private final TeleportManager teleportManager;

    private final int id;
    private final CrossRealmEventSender crossRealmEventSender;
    private final MapSdb mapSdb;
    private long accumulatedMillis;
    private final List<ActiveEntityManager<?>> entityManagers;

    private final ChatManager chatManager;

    private final PlayerRepository playerRepository;

    public AbstractRealm(int id,
                         RealmMap realmMap,
                         RealmEntityEventSender eventSender,
                         GroundItemManager itemManager,
                         NpcManager npcManager,
                         PlayerManager playerManager,
                         DynamicObjectManager dynamicObjectManager,
                         TeleportManager teleportManager,
                         CrossRealmEventSender crossRealmEventSender,
                         MapSdb mapSdb,
                         ChatManager chatManager,
                         PlayerRepository playerRepository) {
        Validate.notNull(realmMap);
        Validate.notNull(eventSender);
        Validate.notNull(itemManager);
        Validate.notNull(playerManager);
        Validate.notNull(crossRealmEventSender);
        Validate.notNull(mapSdb);
        this.realmMap = realmMap;
        this.eventSender = eventSender;
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
        if (dynamicObjectManager != null)
            entityManagers.add(dynamicObjectManager);
        if (npcManager != null)
            entityManagers.add(npcManager);
        this.chatManager = chatManager;
        this.playerRepository = playerRepository;
    }

    void addEntityManager(ActiveEntityManager<?> manager) {
        entityManagers.add(manager);
    }

    public RealmMap map() {
        return realmMap;
    }

    public String name() {
        return mapSdb.getMapTitle(id);
    }

    public String bgm() {
        return mapSdb.getSoundBase(id);
    }

    abstract Logger log();

    RealmEntityEventSender getEventSender() {
        return eventSender;
    }

    PlayerManager getPlayerManager() {
        return playerManager;
    }

    @Override
    public void update() {
        long current = System.currentTimeMillis();
        while (accumulatedMillis <= current) {
            entityManagers.forEach(m -> m.update(STEP_MILLIS));
            accumulatedMillis += STEP_MILLIS;
        }
    }



    protected void doInit() {
        try {
            accumulatedMillis = System.currentTimeMillis();
            if (npcManager != null)
                npcManager.init();
            if (dynamicObjectManager != null)
                dynamicObjectManager.init();
            teleportManager.init(this::onPlayerTeleport);
            playerManager.setTeleportHandler(this::onPlayerTeleport);
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



    void onPlayerTeleport(PlayerRealmEvent event) {
        if (!(event instanceof RealmTeleportEvent realmTeleportEvent) ||
                !playerManager.contains(event.player())) {
            return;
        }
        playerManager.clearPlayer(event.player());
        eventSender.remove(event.player())
                .ifPresent(connection -> {
                    realmTeleportEvent.setConnection(connection);
                    crossRealmEventSender.send(event);
                    log().debug("Removed player {}.", event.player().id());
                });
    }

    CrossRealmEventSender getCrossRealmEventHandler() {
        return crossRealmEventSender;
    }

    void acceptIfAffordableElseReject(RealmTeleportEvent teleportEvent) {
        var unaffordableCost = teleportEvent.checkCost();
        if (unaffordableCost != null) {
            teleportEvent.getConnection().writeAndFlush(PlayerTextEvent.systemTip(teleportEvent.player(), unaffordableCost));
            teleportEvent.rejectEvent().ifPresentOrElse(getCrossRealmEventHandler()::send, () ->
                    log().error("Bad teleport config at {} in realm {}.", teleportEvent.toCoordinate(), teleportEvent.toRealmId()));
            return;
        }
        // order matters, so AOI can be computed correctly.
        teleportEvent.player().joinRealm(this, teleportEvent.toCoordinate(), null);
        eventSender.add(teleportEvent.player(), teleportEvent.getConnection());
        playerManager.teleportIn(teleportEvent.player(), this, teleportEvent.toCoordinate());
        teleportEvent.getCosts().forEach(teleportCost -> teleportCost.charge(teleportEvent.player()));
    }

    abstract void handleTeleportEvent(RealmTeleportEvent teleportEvent);

    abstract void handleConnectionEvent(ConnectionEstablishedEvent connectedEvent);

    PlayerManager playerManager() {
        return playerManager;
    }

    NpcManager npcManager() {
        return npcManager;
    }

    abstract void handleGuildCreation(Player source, ClientFoundGuildEvent event);

    abstract void handleClientEvent(PlayerDataEvent dataEvent);

    private void findEntityAndHandle(Player player, ClientSingleInteractEvent event) {
        for (ActiveEntityManager<?> entityManager : entityManagers) {
            Optional<? extends ActiveEntity> entity = entityManager.find(event.targetId());
            if (entity.isPresent()) {
                event.handle(player, entity.get());
                return;
            }
        }
    }

    private void handlePlayerDataEvent(PlayerDataEvent dataEvent) {
        if (dataEvent.data() instanceof ClientSimpleCommandEvent commandEvent) {
            if (commandEvent.isAskingPosition()) {
                Set<InteractableNpc> merchants = npcManager.findMerchants();
                Set<StaticTeleport> staticTeleports = teleportManager.findStaticTeleports();
                if (!merchants.isEmpty() || !staticTeleports.isEmpty())
                    eventSender.notifySelf(new NpcPositionEvent(dataEvent.player(), merchants, staticTeleports));
            } else if (commandEvent.isQuit()) {
                playerManager.onPlayerDisconnected(dataEvent.playerId());
            } else {
                handleClientEvent(dataEvent);
            }
        } else if (dataEvent.data() instanceof ClientFoundGuildEvent guildEvent) {
            playerManager().find(dataEvent.playerId()).ifPresent(player -> handleGuildCreation(player, guildEvent));
        } else if (dataEvent.data() instanceof ClientInputTextEvent clientInputTextEvent) {
            chatManager.handleClientChat(dataEvent.playerId(), clientInputTextEvent);
        } else if (dataEvent.data() instanceof ClientSingleInteractEvent singleInteractEvent) {
            playerManager.find(singleInteractEvent.getPlayerId())
                    .ifPresent(player -> findEntityAndHandle(player, singleInteractEvent));
        } else {
            handleClientEvent(dataEvent);
        }
    }

    protected abstract void handleLogin(Login login);


    protected void acceptLogin(Login login) {
        playerRepository.load(login.playerId()).ifPresent(p -> getPlayerManager().loginPlayer(p, login, this));
    }

    private void handleInput(ConnectionInput connectionInput) {
        if (connectionInput.input() instanceof SelfHandleInput selfHandleInput) {
            playerManager().handleInput(connectionInput.connection(), selfHandleInput);
        }
    }

    @Override
    public void handle(Object event) {
        try {
            if (event instanceof Login login) {
                handleLogin(login);
            } else if (event instanceof Logout logout) {
                playerManager.logoutPlayer(logout.connection());
            } else if (event instanceof ConnectionInput connectionInput) {
                handleInput(connectionInput);
            }
        } catch (Exception e) {
            log().error("Failed to handle event.", e);
        }
    }

    public void handle(RealmEvent event) {
        try {
            if (event instanceof ConnectionEstablishedEvent connectedEvent) {
                handleConnectionEvent(connectedEvent);
            } else if (event instanceof PlayerDisconnectedEvent disconnectedEvent) {
                playerManager.onPlayerDisconnected(disconnectedEvent.player().id());
                eventSender.remove(disconnectedEvent.player());
            } else if (event instanceof PlayerDataEvent dataEvent) {
                handlePlayerDataEvent(dataEvent);
            } else if (event instanceof RealmTeleportEvent teleportEvent) {
                handleTeleportEvent(teleportEvent);
            } else if (event instanceof BroadcastEvent broadcastEvent) {
                playerManager().allPlayers().forEach(broadcastEvent::send);
            } else if (event instanceof RealmTriggerEvent letterEvent) {
                npcManager.handleCrossRealmEvent(letterEvent);
            } else if (event instanceof PlayerWhisperEvent privateMessageEvent) {
                chatManager.handleCrossRealmChat(privateMessageEvent);
            }
        } catch (Exception e) {
            log().error("Exception when handling event .", e);
        }
    }
}
