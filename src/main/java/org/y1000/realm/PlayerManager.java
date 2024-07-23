package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.y1000.entities.GroundedItem;
import org.y1000.entities.creatures.event.PlayerShootEvent;
import org.y1000.entities.creatures.npc.Merchant;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.*;
import org.y1000.event.EntityEvent;
import org.y1000.item.ItemFactory;
import org.y1000.message.clientevent.*;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.message.serverevent.PlayerLeftEvent;
import org.y1000.network.Connection;
import org.y1000.realm.event.PlayerDataEvent;

@Slf4j
public final class PlayerManager extends AbstractEntityManager<Player> implements PlayerEventVisitor {

    private final EntityEventSender eventSender;

    private final GroundItemManager itemManager;

    private final ProjectileManager projectileManager;

    private final ItemFactory itemFactory;

    private final TradeManager tradeManager;

    private final DynamicObjectManager dynamicObjectManager;

    public PlayerManager(EntityEventSender eventSender,
                         GroundItemManager itemManager,
                         ItemFactory itemFactory,
                         DynamicObjectManager dynamicObjectManager) {
        this(eventSender, itemManager, itemFactory, new TradeManagerImpl(), dynamicObjectManager);
    }

    public PlayerManager(EntityEventSender eventSender,
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
    }


    public void onPlayerConnected(Player player, Connection connection, Realm realm) {
        if (eventSender.contains(player)) {
            log.warn("Player {} already existed.", player);
            player.leaveRealm();
        }
        eventSender.add(player, connection);
        player.joinReam(realm);
        add(player);
    }

    public void onPlayerDisconnected(Player player) {
        player.leaveRealm();
    }

    @Override
    protected Logger log() {
        return log;
    }

    @Override
    public void update(long delta) {
        updateManagedEntities(delta);
        projectileManager.update(delta);
    }

    @Override
    protected void onAdded(Player entity) {
        entity.registerEventListener(this);
        entity.registerEventListener(itemManager);
    }

    @Override
    protected void onDeleted(Player entity) {
        eventSender.remove(entity);
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


    public void onPlayerEvent(PlayerDataEvent dataEvent,
                              EntityManager<Npc> npcManager) {
        Validate.notNull(npcManager);
        if (dataEvent.data() instanceof ClientPickItemEvent event) {
            itemManager.pickItem(dataEvent.player(), event.id());
        } else if (dataEvent.data() instanceof ClientAttackEvent attackEvent) {
            npcManager.find(attackEvent.entityId())
                    .ifPresentOrElse(m -> dataEvent.player().attack(attackEvent, m),
                            () -> find(attackEvent.entityId()).ifPresent(p -> dataEvent.player().attack(attackEvent, p)));
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
        } else {
            dataEvent.player().handleClientEvent(dataEvent.data());
        }
    }

    @Override
    public void onEvent(EntityEvent entityEvent) {
        if (entityEvent.source() instanceof Player player) {
            tradeManager.onPlayerEvent(player, entityEvent);
        }
        if (entityEvent instanceof PlayerLeftEvent playerLeftEvent) {
            eventSender.notifyVisiblePlayers(playerLeftEvent.player(), playerLeftEvent);
            delete(playerLeftEvent.player());
        } else if (entityEvent instanceof PlayerShootEvent shootEvent) {
            projectileManager.add(shootEvent.projectile());
            eventSender.notifyVisiblePlayersAndSelf(shootEvent.source(), shootEvent);
        } else if (entityEvent instanceof AbstractPlayerEvent playerEvent && playerEvent.isSelfEvent()) {
            eventSender.notifySelf(playerEvent);
        }
    }
}
