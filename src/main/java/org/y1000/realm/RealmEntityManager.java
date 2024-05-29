package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.Entity;
import org.y1000.entities.PhysicalEntity;
import org.y1000.entities.Projectile;
import org.y1000.entities.creatures.event.ChangeStateEvent;
import org.y1000.entities.creatures.event.CreatureAttackEvent;
import org.y1000.entities.creatures.event.CreatureHurtEvent;
import org.y1000.entities.creatures.event.CreatureShootEvent;
import org.y1000.entities.GroundedItem;
import org.y1000.item.ItemFactory;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.*;
import org.y1000.entities.repository.ItemRepository;
import org.y1000.message.*;
import org.y1000.message.serverevent.JoinedRealmEvent;
import org.y1000.message.serverevent.*;
import org.y1000.network.Connection;

import java.util.*;

@Slf4j
final class RealmEntityManager implements EntityEventListener,
        PlayerEventVisitor {

    private final RelevantScopeManager scopeManager = new RelevantScopeManager();

    private final Set<Projectile> projectiles = new HashSet<>();

    private final Map<Player, Connection> playerConnectionMap = new HashMap<>(100);

    private int temporaryEntityId = 1;

    private final ItemRepository itemRepository;

    private final ItemFactory itemFactory;

    RealmEntityManager(ItemRepository itemRepository,
                       ItemFactory itemFactory) {
        this.itemRepository = itemRepository;
        this.itemFactory = itemFactory;
    }


    @Override
    public void visit(InputResponseMessage inputResponseMessage) {
        sendMessage(inputResponseMessage.player(), inputResponseMessage);
        visit(inputResponseMessage.positionMessage());
    }

    private void notifyInterpolation(Player joined, Entity entity) {
        sendMessage(joined, entity.captureInterpolation());
        log.debug("Notified player {} of player {}", joined.id(), entity.id());
        if (entity instanceof Player another) {
            log.debug("Notified player {} of player {}", another.id(), joined.id());
            sendMessage(another, joined.captureInterpolation());
        }
    }

    private void sendMessage(Player player, ServerMessage serverMessage) {
        playerConnectionMap.get(player).write(serverMessage);
    }

    private void notifyOutsightOrInsight(PhysicalEntity moved,
                                         PhysicalEntity affected) {
        boolean outOfView = scopeManager.outOfScope(moved, affected);
        if (outOfView) {
            if (moved instanceof Player movedPlayer) {
                sendMessage(movedPlayer, new RemoveEntityMessage(affected.id()));
            }
            if (affected instanceof Player affectedPlayer) {
                sendMessage(affectedPlayer, new RemoveEntityMessage(moved.id()));
            }
        } else {
            if (moved instanceof Player movedPlayer) {
                sendMessage(movedPlayer, affected.captureInterpolation());
            }
            if (affected instanceof Player affectedPlayer) {
                sendMessage(affectedPlayer, moved.captureInterpolation());
            }
        }
    }


    @Override
    public void visit(AbstractPositionEvent positionEvent) {
        PhysicalEntity source = positionEvent.source();
        Set<PhysicalEntity> affectedEntities = scopeManager.update(source);
        affectedEntities.forEach(entity -> notifyOutsightOrInsight(source, entity));
        notifyVisiblePlayers(source, positionEvent);
    }

    @Override
    public void visit(JoinedRealmEvent joinedRealmEvent) {
        sendMessage(joinedRealmEvent.player(), joinedRealmEvent);
        var visibleEntities = scopeManager.filterVisibleEntities(joinedRealmEvent.source(), PhysicalEntity.class);
        visibleEntities.forEach(entity -> notifyInterpolation(joinedRealmEvent.player(), entity));
    }

    @Override
    public void visit(PlayerLeftEvent event) {
        Set<PhysicalEntity> affected = scopeManager.remove(event.player());
        affected.stream()
                .filter(entity -> entity instanceof Player)
                .map(Player.class::cast)
                .forEach(player -> sendMessage(player, event));
    }

    @Override
    public void visit(CreatureAttackEvent event) {
        notifyVisiblePlayers(event.source(), event);
    }

    @Override
    public void visit(RewindEvent event) {
        sendMessage(event.player(), event);
        notifyVisiblePlayers(event.source(), event.toSetPosition());
    }

    @Override
    public void visit(PlayerAttackEventResponse event) {
        sendMessage(event.player(), event);
        event.toPlayerAttackEvent()
                .ifPresent(e -> notifyVisiblePlayers(event.source(), e));
    }

    private void notifyVisiblePlayers(PhysicalEntity source, ServerMessage serverMessage) {
        scopeManager.filterVisibleEntities(source, Player.class)
                .forEach(player -> sendMessage(player, serverMessage));
    }

    @Override
    public void visit(PlayerAttackEvent event) {
        notifyVisiblePlayersAndSelf(event.source(), event);
    }

    @Override
    public void visit(CreatureHurtEvent event) {
        notifyVisiblePlayersAndSelf(event.source(), event);
    }

    private void notifyVisiblePlayersAndSelf(PhysicalEntity source,
                                             ServerMessage message) {
        notifyVisiblePlayers(source, message);
        if (source instanceof Player player) {
            sendMessage(player, message);
        }
    }

    @Override
    public void visit(ChangeStateEvent event) {
        notifyVisiblePlayersAndSelf(event.source(), event);
    }

    @Override
    public void OnEvent(EntityEvent entityEvent) {
        entityEvent.accept(this);
    }

    @Override
    public void visit(CreatureShootEvent event) {
        projectiles.add(event.projectile());
    }

    @Override
    public void visit(InventorySlotSwappedEvent event) {
        sendMessage(event.player(), event);
    }

    @Override
    public void visit(PlayerDropItemEvent event) {
        sendMessage(event.player(), event);
        GroundedItem groundedItem = event.createGroundedItem(temporaryEntityId++);
        add(groundedItem);
        notifyVisiblePlayers(groundedItem, groundedItem.captureInterpolation());
    }

    @Override
    public void visit(CharacterChangeWeaponEvent event) {
        sendMessage(event.player(), event);
        notifyVisiblePlayers(event.source(), event.packetForOtherPlayers());
    }

    @Override
    public void visit(PlayerPickedItemEvent event) {
        sendMessage(event.player(), event);
        notifyVisiblePlayers(event.groundedItem(), new RemoveEntityMessage(event.groundedItem().getId()));
        scopeManager.remove(event.groundedItem());
        //itemRepository.save(event.player().id(), event, currentItemInSlot);
    }

    @Override
    public void visit(UpdateInventorySlotEvent event) {
        sendMessage(event.player(), event);
    }

    @Override
    public void visit(PlayerUnequipEvent event) {
        notifyVisiblePlayersAndSelf(event.player(), event);
    }

    @Override
    public void visit(PlayerEquipEvent event) {
        notifyVisiblePlayersAndSelf(event.player(), event);
    }

    @Override
    public void visit(PlayerTextEvent event) {
        sendMessage(event.player(), event);
    }

    @Override
    public void visit(GetGroundItemEvent event) {
        GroundedItem pickingItem = event.getPickingItem();
        event.player().pickItem(itemFactory.createItem(pickingItem), pickingItem);
    }

    private void update(PhysicalEntity entity, int delta) {
        try {
            entity.update(delta);
        } catch (Exception e) {
            log.error("Exception when updating {}.", entity, e);
        }
    }

    private void updateProjectiles(int delta) {
        for (Iterator<Projectile> iterator = projectiles.iterator(); iterator.hasNext();) {
            Projectile projectile = iterator.next();
            try {
                if (projectile.update(delta)) {
                    iterator.remove();
                }
            } catch (Exception e) {
                iterator.remove();
                log.error("Exception when updating {}.", projectile, e);
            }
        }
    }

    public void updateEntities(int delta) {
        scopeManager.getAllEntities().forEach(e -> update(e, delta));
        updateProjectiles(delta);
    }

    public Optional<PhysicalEntity> findInsight(PhysicalEntity source, long id) {
        Set<PhysicalEntity> entities = scopeManager.filterVisibleEntities(source, PhysicalEntity.class);
        return entities.stream().filter(e -> e.id() == id).findFirst();
    }

    public void add(Player player, Connection connection) {
        playerConnectionMap.put(player, connection);
        add(player);
    }

    public boolean contains(Player player) {
        return playerConnectionMap.containsKey(player);
    }

    public void add(PhysicalEntity entity) {
        if (scopeManager.getAllEntities().contains(entity)) {
            return;
        }
        scopeManager.add(entity);
        entity.registerOrderedEventListener(this);
    }

    public void remove(Player player) {
        scopeManager.remove(player);
        playerConnectionMap.remove(player);
    }
}
