package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.y1000.entities.GroundedItem;
import org.y1000.entities.creatures.event.PlayerShootEvent;
import org.y1000.entities.creatures.npc.Merchant;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.players.Player;
import org.y1000.event.EntityEvent;
import org.y1000.item.ItemFactory;
import org.y1000.message.clientevent.ClientAttackEvent;
import org.y1000.message.clientevent.ClientBuyItemsEvent;
import org.y1000.message.clientevent.ClientPickItemEvent;
import org.y1000.message.clientevent.ClientSellEvent;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.message.serverevent.PlayerLeftEvent;
import org.y1000.network.Connection;
import org.y1000.realm.event.PlayerDataEvent;

@Slf4j
public final class PlayerManager extends AbstractEntityManager<Player> implements PlayerEventVisitor {

    private final EntityEventSender eventSender;

    private final EntityManager<GroundedItem> itemManager;

    private final ProjectileManager projectileManager;

    private final ItemFactory itemFactory;

    public PlayerManager(EntityEventSender eventSender,
                         EntityManager<GroundedItem> itemManager,
                         ItemFactory itemFactory) {
        this.eventSender = eventSender;
        this.itemManager = itemManager;
        this.itemFactory = itemFactory;
        this.projectileManager = new ProjectileManager();
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
        entity.deregisterEventListener(this);
        entity.deregisterEventListener(itemManager);
        eventSender.remove(entity);
    }



    public void onPlayerEvent(PlayerDataEvent dataEvent,
                              EntityManager<Npc> npcManager) {
        Validate.notNull(npcManager);
        if (dataEvent.data() instanceof ClientPickItemEvent event) {
            itemManager.find(event.id())
                    .ifPresent(groundItem -> dataEvent.player().pickItem(groundItem, itemFactory::createItem));
        } else if (dataEvent.data() instanceof ClientAttackEvent attackEvent) {
            npcManager.find(attackEvent.entityId())
                    .ifPresentOrElse(m -> dataEvent.player().attack(attackEvent, m),
                    () -> find(attackEvent.entityId()).ifPresent(p -> dataEvent.player().attack(attackEvent, p)));
        } else if (dataEvent.data() instanceof ClientSellEvent sellEvent) {
            npcManager.find(sellEvent.merchantId(), Merchant.class)
                    .ifPresent(merchant -> merchant.buy(dataEvent.player(), sellEvent.items()));
        } else if (dataEvent.data() instanceof ClientBuyItemsEvent buyItemsEvent) {
            npcManager.find(buyItemsEvent.merchantId(), Merchant.class)
                    .ifPresent(merchant -> merchant.sell(dataEvent.player(), buyItemsEvent.items(), itemFactory::createItem));
        } else {
            dataEvent.player().handleClientEvent(dataEvent.data());
        }
    }

    @Override
    public void onEvent(EntityEvent entityEvent) {
        if (entityEvent instanceof PlayerLeftEvent playerLeftEvent) {
            delete(playerLeftEvent.player());
        } else if (entityEvent instanceof PlayerShootEvent shootEvent) {
            projectileManager.add(shootEvent.projectile());
        }
    }
}
