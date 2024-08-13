package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.y1000.entities.AttackableActiveEntity;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.PlayerShootEvent;
import org.y1000.entities.creatures.npc.Merchant;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.Rope;
import org.y1000.entities.players.event.*;
import org.y1000.event.EntityEvent;
import org.y1000.item.ItemFactory;
import org.y1000.message.PlayerDropItemEvent;
import org.y1000.message.clientevent.*;
import org.y1000.message.serverevent.JoinedRealmEvent;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.message.serverevent.PlayerLeftEvent;
import org.y1000.realm.event.PlayerDataEvent;
import org.y1000.util.Coordinate;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;


@Slf4j
final class PlayerManagerImpl extends AbstractActiveEntityManager<Player> implements PlayerEventVisitor, PlayerManager {

    private final EntityEventSender eventSender;

    private final GroundItemManager itemManager;

    private final ProjectileManager projectileManager;

    private final ItemFactory itemFactory;

    private final TradeManager tradeManager;

    private final DynamicObjectManager dynamicObjectManager;

    private final Set<Rope> ropes;

    public PlayerManagerImpl(EntityEventSender eventSender,
                             GroundItemManager itemManager,
                             ItemFactory itemFactory,
                             DynamicObjectManager dynamicObjectManager) {
        this(eventSender, itemManager, itemFactory, new TradeManagerImpl(), dynamicObjectManager);
    }

    public PlayerManagerImpl(EntityEventSender eventSender,
                             GroundItemManager itemManager,
                             ItemFactory itemFactory,
                             TradeManager tradeManager,
                             DynamicObjectManager dynamicObjectManager) {
        this.eventSender = eventSender;
        this.itemManager = itemManager;
        this.itemFactory = itemFactory;
        this.projectileManager = new ProjectileManager();
        this.tradeManager = tradeManager;
        this.dynamicObjectManager = dynamicObjectManager;
        ropes = new HashSet<>();
    }

    @Override
    public void onPlayerConnected(Player player, Realm realm) {
        doAdd(player);
        player.joinReam(realm);
    }

    private void doAdd(Player player) {
        player.registerEventListener(this);
        add(player);
    }

    @Override
    public void teleportIn(Player player,
                           Realm realm, Coordinate coordinate) {
        if (player == null || realm == null) {
            return;
        }
        doAdd(player);
        player.teleport(realm, coordinate);
    }

    @Override
    public void teleportOut(Player player) {
        if (player == null) {
            return;
        }
        player.leaveRealm();
        player.clearListeners();
        remove(player);
    }

    @Override
    public void onPlayerDisconnected(Player player) {
        player.leaveRealm();
        remove(player);
    }

    @Override
    public void update(long delta) {
        updateManagedEntities(delta);
        projectileManager.update(delta);
        updateRopes(delta);
    }

    private void updateRopes(long delta) {
        Iterator<Rope> iterator = ropes.iterator();
        while (iterator.hasNext()) {
            Rope next = iterator.next();
            next.update(delta);
            if (next.done()) {
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

    private void handleDragPlayerEvent(Player player, Player dragged) {
        if (dragged.stateEnum() != State.DIE || dragged.coordinate().directDistance(player.coordinate()) > 2) {
            return;
        }
        ropes.add(new Rope(dragged, player));
    }

    @Override
    public void onClientEvent(PlayerDataEvent dataEvent,
                              ActiveEntityManager<Npc> npcManager) {
        Validate.notNull(npcManager);
        if (!contains(dataEvent.player())){
            return;
        }
        if (dataEvent.data() instanceof ClientPickItemEvent event) {
            itemManager.pickItem(dataEvent.player(), event.id());
        } else if (dataEvent.data() instanceof ClientAttackEvent attackEvent) {
            npcManager.find(attackEvent.entityId(), AttackableActiveEntity.class)
                    .or(() -> find(attackEvent.entityId(), AttackableActiveEntity.class))
                    .or(() -> dynamicObjectManager.find(attackEvent.entityId(), AttackableActiveEntity.class))
                    .ifPresent(attackableEntity -> dataEvent.player().attack(attackEvent, attackableEntity));
        } else if (dataEvent.data() instanceof ClientSellEvent sellEvent) {
            npcManager.find(sellEvent.merchantId(), Merchant.class)
                    .ifPresent(merchant -> merchant.buy(dataEvent.player(), sellEvent.items(), itemFactory::createMoney));
        } else if (dataEvent.data() instanceof ClientBuyItemsEvent buyItemsEvent) {
            npcManager.find(buyItemsEvent.merchantId(), Merchant.class)
                    .ifPresent(merchant -> merchant.sell(dataEvent.player(), buyItemsEvent.items(), itemFactory::createItem));
        } else if (dataEvent.data() instanceof ClientTradePlayerEvent tradePlayerEvent) {
            find(tradePlayerEvent.targetId(), Player.class).ifPresent(tradee -> tradeManager.start(dataEvent.player(), tradee, tradePlayerEvent.slot()));
        } else if (dataEvent.data() instanceof ClientUpdateTradeEvent updateTradeEvent) {
            handleUpdateTradeEvent(dataEvent.player(), updateTradeEvent);
        } else if (dataEvent.data() instanceof ClientTriggerDynamicObjectEvent triggerDynamicObjectEvent) {
            dynamicObjectManager.triggerDynamicObject(triggerDynamicObjectEvent.id(), dataEvent.player(), triggerDynamicObjectEvent.useSlot());
        } else if (dataEvent.data() instanceof ClientDragPlayerEvent dragPlayerEvent) {
            find(dragPlayerEvent.target()).ifPresent(dragged -> handleDragPlayerEvent(dataEvent.player(), dragged));
        } else {
            dataEvent.player().handleClientEvent(dataEvent.data());
        }
    }

    @Override
    public Set<Player> allPlayers() {
        return getEntities();
    }

    @Override
    public void onEvent(EntityEvent entityEvent) {
        if (entityEvent.source() instanceof Player player) {
            tradeManager.onPlayerEvent(player, entityEvent);
        }
        if (entityEvent instanceof PlayerLeftEvent playerLeftEvent) {
            eventSender.notifyVisiblePlayers(playerLeftEvent.player(), playerLeftEvent);
            log.debug("Sent remove player {} event.", playerLeftEvent.source());
        } else if (entityEvent instanceof JoinedRealmEvent joinedRealmEvent) {
            eventSender.notifySelf(joinedRealmEvent);
            eventSender.notifyPlayerOfEntities(joinedRealmEvent.player());
        } else if (entityEvent instanceof PlayerShootEvent shootEvent) {
            projectileManager.add(shootEvent.projectile());
            eventSender.notifyVisiblePlayersAndSelf(shootEvent.source(), shootEvent);
        } else if (entityEvent instanceof PlayerTeleportEvent teleportEvent) {
            eventSender.updateScope(teleportEvent.player());
            eventSender.notifySelf(teleportEvent);
            eventSender.notifyPlayerOfEntities(teleportEvent.player());
        } else if (entityEvent instanceof PlayerAttackEvent attackEvent) {
            eventSender.notifyVisiblePlayersAndSelf(attackEvent.source(), attackEvent);
        } else if (entityEvent instanceof PlayerDropItemEvent dropItemEvent) {
            itemManager.dropItem(dropItemEvent.getDroppedItemName(), dropItemEvent.getNumberOnGround(), dropItemEvent.getCoordinate());
        } else if (entityEvent instanceof AbstractPlayerEvent playerEvent && playerEvent.isSelfEvent()) {
            eventSender.notifySelf(playerEvent);
        }
    }
}
