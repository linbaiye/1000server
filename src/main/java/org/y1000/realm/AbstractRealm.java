package org.y1000.realm;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.y1000.entities.creatures.npc.Merchant;
import org.y1000.entities.players.Player;
import org.y1000.entities.teleport.StaticTeleport;
import org.y1000.message.clientevent.ClientFoundGuildEvent;
import org.y1000.message.clientevent.ClientSimpleCommandEvent;
import org.y1000.message.clientevent.chat.ClientChatEvent;
import org.y1000.message.serverevent.NpcPositionEvent;
import org.y1000.network.event.ConnectionEstablishedEvent;
import org.y1000.realm.event.*;
import org.y1000.sdb.MapSdb;

import java.util.ArrayList;
import java.util.List;
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
                         ChatManager chatManager) {
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

    void doUpdateEntities() {
        long current = System.currentTimeMillis();
        while (accumulatedMillis <= current) {
            entityManagers.forEach(m -> m.update(STEP_MILLIS));
            accumulatedMillis += STEP_MILLIS;
        }
    }

    abstract Logger log();

    RealmEntityEventSender getEventSender() {
        return eventSender;
    }

    PlayerManager getPlayerManager() {
        return playerManager;
    }


    @Override
    public void shutdown() {
        playerManager.shutdown();
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
        var connection = eventSender.remove(event.player());
        realmTeleportEvent.setConnection(connection);
        crossRealmEventSender.send(event);
        log().debug("Removed player {}.", event.player().id());
    }

    CrossRealmEventSender getCrossRealmEventHandler() {
        return crossRealmEventSender;
    }

    void acceptTeleport(RealmTeleportEvent teleportEvent) {
        // order matters, so AOI can be computed correctly.
        teleportEvent.player().joinRealm(this, teleportEvent.toCoordinate());
        eventSender.add(teleportEvent.player(), teleportEvent.getConnection());
        playerManager.teleportIn(teleportEvent.player(), this, teleportEvent.toCoordinate());
    }

    abstract void handleTeleportEvent(RealmTeleportEvent teleportEvent);

    abstract void handleConnectionEvent(ConnectionEstablishedEvent connectedEvent);

    PlayerManager playerManager() {
        return playerManager;
    }

    abstract void handleGuidCreation(Player source, ClientFoundGuildEvent event);


    private void handlePlayerDataEvent(PlayerDataEvent dataEvent) {
        if (dataEvent.data() instanceof ClientSimpleCommandEvent commandEvent) {
            if (commandEvent.isAskingPosition()) {
                Set<Merchant> merchants = npcManager.findMerchants();
                Set<StaticTeleport> staticTeleports = teleportManager.findStaticTeleports();
                if (!merchants.isEmpty() || !staticTeleports.isEmpty())
                    eventSender.notifySelf(new NpcPositionEvent(dataEvent.player(), merchants, staticTeleports));
            } else if (commandEvent.isQuit()) {
                playerManager.onPlayerDisconnected(dataEvent.playerId());
            }
        } else if (dataEvent.data() instanceof ClientFoundGuildEvent guildEvent) {
            playerManager().find(dataEvent.playerId()).ifPresent(player -> handleGuidCreation(player, guildEvent));
        } else if (dataEvent.data() instanceof ClientChatEvent clientChatEvent) {
            chatManager.handleClientChat(dataEvent.playerId(), clientChatEvent);
        } else {
            playerManager.onClientEvent(dataEvent, npcManager);
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
