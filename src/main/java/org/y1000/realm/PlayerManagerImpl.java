package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.Entity;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.Rope;
import org.y1000.entities.players.event.*;
import org.y1000.event.EntityEvent;
import org.y1000.item.ItemFactory;
import org.y1000.message.*;
import org.y1000.message.input.*;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.Connection;
import org.y1000.realm.event.PlayerDataEvent;
import org.y1000.realm.event.RealmTeleportEvent;
import org.y1000.repository.PlayerRepository;
import org.y1000.util.Coordinate;
import org.y1000.util.UnaryAction;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;


@Slf4j
final class PlayerManagerImpl extends AbstractMovableEntityManager<Player> implements PlayerEventVisitor,
        PlayerManager, PlayerEventListener, PlayerEventHandler {

    private final RealmPlayerConnectionManager connectionManager;

    private final GroundItemManager itemManager;

    private final ProjectileManager projectileManager;

    private final ItemFactory itemFactory;

    private final TradeManager tradeManager;

    private final DynamicObjectManager dynamicObjectManager;

    private final Set<Rope> ropes;

    private final BankManager bankManager;

    private final PlayerRepository playerRepository;

    private final DeadPlayerTeleportManager deadPlayerTeleportManager;

    private final CrossRealmEventSender crossRealmEventSender;


    public PlayerManagerImpl(RealmPlayerConnectionManager eventSender,
                             GroundItemManager itemManager,
                             ItemFactory itemFactory,
                             DynamicObjectManager dynamicObjectManager,
                             BankManager bankManager,
                             PlayerRepository playerRepository,
                             DeadPlayerTeleportManager deadPlayerTeleportManager,
                             CrossRealmEventSender crossRealmEventSender,
                             AOIManager aoiManager) {
        this(eventSender, itemManager, itemFactory, new TradeManagerImpl(eventSender), dynamicObjectManager, bankManager, playerRepository,
                deadPlayerTeleportManager, crossRealmEventSender, aoiManager);
    }

    public PlayerManagerImpl(RealmPlayerConnectionManager eventSender,
                             GroundItemManager itemManager,
                             ItemFactory itemFactory,
                             TradeManager tradeManager,
                             DynamicObjectManager dynamicObjectManager,
                             BankManager bankManager,
                             PlayerRepository playerRepository,
                             DeadPlayerTeleportManager deadPlayerTeleportManager,
                             CrossRealmEventSender crossRealmEventSender,
                             AOIManager aoiManager) {
        super(aoiManager, eventSender);
        this.connectionManager = eventSender;
        this.itemManager = itemManager;
        this.itemFactory = itemFactory;
        this.playerRepository = playerRepository;
        this.projectileManager = new ProjectileManager();
        this.tradeManager = tradeManager;
        this.dynamicObjectManager = dynamicObjectManager;
        this.bankManager = bankManager;
        ropes = new HashSet<>();
        this.deadPlayerTeleportManager = deadPlayerTeleportManager;
        this.crossRealmEventSender = crossRealmEventSender;
    }

    @Override
    public void onPlayerConnected(Player player, Realm realm) {
//        if (player == null || realm == null) {
//            return;
//        }
//        player.registerEventListener(this);
//        add(player);
//        player.joinRealm(realm, this);
//        connectionManager.notifySelf(new JoinedRealmEvent(player));
//        connectionManager.notifyPlayerOfEntities(player);
//        log.debug("Player {} logged in.", player);
    }

    public void sendToVisiblePlayersAndSelf(Player source, I2ClientMessage message) {
        sendToVisiblePlayers(source, message);
        sendTo(source, message);
    }



    private void doLogout(Player player) {
        if (player == null) {
            return;
        }
        // Need to update first least losing realm id.
        playerRepository.update(player);
        player.leaveRealm();
        sendToVisiblePlayers(player, new RemoveEntityMessage(player.id()));
        connectionManager.remove(player).ifPresent(Connection::tryClose);
        remove(player);
    }

    @Override
    public void loginPlayer(Player player, Login login, Realm realm) {
        if (player == null || login == null) {
            return;
        }
        find(login.playerId()).ifPresent(this::doLogout);
        connectionManager.add(player, login.connection());
        player.joinRealm(realm, this);
        sendTo(player, PlayerJoinRealmMessage.of(player));
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
    public void logoutPlayer(Connection connection) {
        if (connection == null)
            return;
        connectionManager.findPlayer(connection).ifPresent(this::doLogout);
    }

    @Override
    public void teleportIn(Player player,
                           Realm realm, Coordinate coordinate) {
        if (player == null || realm == null || coordinate == null) {
            return;
        }
        player.registerEventListener(this);
        add(player);
//        connectionManager.notifySelf(new PlayerTeleportEvent(player, realm, coordinate));
//        connectionManager.notifyPlayerOfEntities(player);
    }

    @Override
    public void clearPlayer(Player player) {
        if (player == null) {
            return;
        }
        player.leaveRealm();
//        connectionManager.notifyVisiblePlayersAndSelf(player, new RemoveEntityMessage(player.id()));
        remove(player);
        player.clearListeners();
    }

    @Override
    public void onClientEvent(PlayerDataEvent dataEvent, ActiveEntityManager<?> npcManager) {

    }

    @Override
    public void update(long delta) {
        updateManagedEntities(delta);
        projectileManager.update(delta);
        updateRopes(delta);
        if (deadPlayerTeleportManager != null) {
            deadPlayerTeleportManager.update(delta);
        }
    }

    private void updateRopes(long delta) {
        Iterator<Rope> iterator = ropes.iterator();
        while (iterator.hasNext()) {
            Rope rope = iterator.next();
            rope.update(delta);
            if (rope.isBroken()) {
                iterator.remove();
            }
        }
    }

    @Override
    protected Logger log() {
        return log;
    }



    private void handleUpdateTradeEvent(Player player, ClientUpdateTradeEvent updateTradeEvent) {
        if (updateTradeEvent.type() == ClientUpdateTradeEvent.ClientUpdateType.ADD_ITEM) {
            tradeManager.addTradeItem(player, updateTradeEvent.slot(), updateTradeEvent.number());
        } else if (updateTradeEvent.type() == ClientUpdateTradeEvent.ClientUpdateType.REMOVE_ITEM) {
            tradeManager.removeTradeItem(player, updateTradeEvent.tradeWindowSlot());
        } else if (updateTradeEvent.type() == ClientUpdateTradeEvent.ClientUpdateType.CANCEL) {
            tradeManager.cancelTrade(player);
        } else if (updateTradeEvent.type() == ClientUpdateTradeEvent.ClientUpdateType.CONFIRM) {
            tradeManager.confirmTrade(player);
        }
    }

    private void handleDragPlayerEvent(Player player, Player dragged, int ropeSlot) {
        if (player.canDrag(dragged, ropeSlot)) {
            player.consumeItem(ropeSlot);
            ropes.forEach(rope -> rope.breakIfDraggedAgain(dragged));
            ropes.add(new Rope(dragged, player));
        }
    }

//    @Override
//    public void onClientEvent(PlayerDataEvent dataEvent,
//                              ActiveEntityManager<INpc> npcManager) {
//        if (!contains(dataEvent.player())){
//            return;
//        }
//        if (dataEvent.data() instanceof ClientPickItemEvent event) {
//            itemManager.pickItem(dataEvent.player(), event.id());
//        } else if (dataEvent.data() instanceof ClientAttackEvent attackEvent) {
//            npcManager.find(attackEvent.entityId(), AttackableEntity.class)
//                    .or(() -> find(attackEvent.entityId(), AttackableEntity.class))
//                    .or(() -> dynamicObjectManager.find(attackEvent.entityId(), AttackableEntity.class))
//                    .ifPresent(attackableEntity -> dataEvent.player().attack(attackEvent, attackableEntity));
//        } else if (dataEvent.data() instanceof ClientTradePlayerEvent tradePlayerEvent) {
//            find(tradePlayerEvent.targetId(), Player.class).ifPresent(tradee -> tradeManager.start(dataEvent.player(), tradee, tradePlayerEvent.slot()));
//        } else if (dataEvent.data() instanceof ClientUpdateTradeEvent updateTradeEvent) {
//            handleUpdateTradeEvent(dataEvent.player(), updateTradeEvent);
//        } else if (dataEvent.data() instanceof ClientTriggerDynamicObjectEvent triggerDynamicObjectEvent) {
//            dynamicObjectManager.triggerDynamicObject(triggerDynamicObjectEvent.id(), dataEvent.player(), triggerDynamicObjectEvent.useSlot());
//        } else if (dataEvent.data() instanceof ClientDragPlayerEvent dragPlayerEvent) {
//            find(dragPlayerEvent.target()).ifPresent(dragged -> handleDragPlayerEvent(dataEvent.player(), dragged, dragPlayerEvent.ropeSlot()));
//        } else if (dataEvent.data() instanceof ClientOperateBankEvent bankEvent) {
//            find(dataEvent.playerId()).ifPresent(player -> bankManager.handle(player, bankEvent));
//        } else if (dataEvent.data() instanceof ClientSelfInteractEvent selfInteractEvent) {
//            find(selfInteractEvent.getPlayerId()).ifPresent(selfInteractEvent::handle);
//        } else {
//            find(dataEvent.playerId()).ifPresent(player -> player.handleClientEvent(dataEvent.data()));
//        }
//    }

    @Override
    public Set<Player> allPlayers() {
        return getEntities();
    }


    @Override
    public void onPlayerDisconnected(long playerId) {
//        find(playerId).ifPresent( player -> {
//            playerRepository.update(player);
//            clearPlayer(player);
//            log.debug("Player {} disconnected.", player);
//        });
    }

    @Override
    public void setTeleportHandler(UnaryAction<RealmTeleportEvent> teleportHandler) {
        if (deadPlayerTeleportManager != null) {
            deadPlayerTeleportManager.setTeleportHandler(teleportHandler);
        }
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


    @Override
    public void onEvent(EntityEvent entityEvent) {
//        try {
//            if (entityEvent.source() instanceof Player player) {
//                tradeManager.onPlayerEvent(player, entityEvent);
//            }
//            if (entityEvent instanceof PlayerShootEvent shootEvent) {
//                projectileManager.add(shootEvent.projectile());
//                eventSender.notifyVisiblePlayersAndSelf(shootEvent.source(), shootEvent);
//            } else if (entityEvent instanceof PlayerAttackEvent attackEvent) {
//                eventSender.notifyVisiblePlayersAndSelf(attackEvent.source(), attackEvent);
//            } else if (entityEvent instanceof PlayerDropItemEvent dropItemEvent) {
//                itemManager.dropItem(dropItemEvent);
//            } else if (entityEvent instanceof CreatureDieEvent dieEvent &&
//                    dieEvent.source() instanceof Player player &&
//                    deadPlayerTeleportManager != null) {
//                deadPlayerTeleportManager.onPlayerDead(player);
//            } else if (entityEvent instanceof PlayerKungFuFullEvent event) {
//                crossRealmEventSender.send(event);
//            } else if (entityEvent instanceof AbstractPlayerEvent playerEvent) {
//                if (playerEvent.visibleToSelf()) {
//                    eventSender.notifySelf(playerEvent);
//                } else if (playerEvent.visibleToPlayers()) {
//                    eventSender.notifyVisiblePlayersAndSelf(playerEvent.player(), playerEvent);
//                }
//            } else if (entityEvent instanceof I2ClientMessage message) {
//                eventSender.findConnection((Player) entityEvent.source())
//                        .ifPresent(connection -> connection.writeAndFlush(message));
//            }
//        } catch (Exception e) {
//            log.error("Failed to handle event.", e);
//        }
    }


    public void sendTo(Player player, I2ClientMessage message) {
        connectionManager.sendTo(player, message);
    }


    public void onMoved(Player player) {
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
    public void onEvent(PlayerEvent event) {
        event.accept(this);
    }
}
