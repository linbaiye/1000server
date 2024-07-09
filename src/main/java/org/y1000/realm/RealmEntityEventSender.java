package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.*;
import org.y1000.entities.creatures.event.*;
import org.y1000.event.EntityEvent;
import org.y1000.event.EntityEventListener;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.*;
import org.y1000.message.*;
import org.y1000.message.serverevent.JoinedRealmEvent;
import org.y1000.message.serverevent.*;
import org.y1000.network.Connection;

import java.util.*;

/**
 * Responsible for sending events to visible clients.
 */
@Slf4j
final class RealmEntityEventSender implements EntityEventListener,
        PlayerEventVisitor, EntityEventSender {

    private final RelevantScopeManager scopeManager = new RelevantScopeManager();

    private final Map<Player, Connection> playerConnectionMap = new HashMap<>(100);


    private final TradeManager tradeManager;

    RealmEntityEventSender() {
        tradeManager = new TradeManager();
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

    private void notifyOutsightOrInsight(Entity moved,
                                         Entity affected) {
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
        Entity source = positionEvent.source();
        Set<Entity> affectedEntities = scopeManager.update(source);
        affectedEntities.forEach(entity -> notifyOutsightOrInsight(source, entity));
        doNotifyVisiblePlayers(source, positionEvent);
        if (positionEvent.source() instanceof Player player) {
            sendMessage(player, positionEvent);
        }
    }


    @Override
    public void visit(JoinedRealmEvent joinedRealmEvent) {
        sendMessage(joinedRealmEvent.player(), joinedRealmEvent);
        var visibleEntities = scopeManager.filterVisibleEntities(joinedRealmEvent.source(), Entity.class);
        visibleEntities.forEach(entity -> notifyInterpolation(joinedRealmEvent.player(), entity));
    }

    private void cleanEntity(Entity entity,
                             ServerMessage message) {
        Set<Player> affected = scopeManager.filterVisibleEntities(entity, Player.class);
        affected.forEach(player -> sendMessage(player, message));
    }

    @Override
    public void visit(PlayerLeftEvent event) {
        cleanEntity(event.source(), event);
    }

    @Override
    public void visit(CreatureAttackEvent event) {
        doNotifyVisiblePlayers(event.source(), event);
    }

    @Override
    public void visit(RewindEvent event) {
        sendMessage(event.player(), event);
        doNotifyVisiblePlayers(event.source(), event.toSetPosition());
    }

    @Override
    public void visit(PlayerAttackEventResponse event) {
        sendMessage(event.player(), event);
        event.toPlayerAttackEvent()
                .ifPresent(e -> doNotifyVisiblePlayers(event.source(), e));
    }

    private void doNotifyVisiblePlayers(Entity source, ServerMessage serverMessage) {
        scopeManager.filterVisibleEntities(source, Player.class)
                .forEach(player -> sendMessage(player, serverMessage));
    }

    public void notifyVisiblePlayers(Entity source, ServerMessage serverMessage) {
        Validate.notNull(source, "source can't be null.");
        Validate.notNull(serverMessage, "serverMessage can't be null.");
        doNotifyVisiblePlayers(source, serverMessage);
    }


    @Override
    public void visit(PlayerAttackEvent event) {
        notifyVisiblePlayersAndSelf(event.source(), event);
    }

    @Override
    public void visit(CreatureHurtEvent event) {
        notifyVisiblePlayersAndSelf(event.source(), event);
    }

    private void notifyVisiblePlayersAndSelf(Entity source,
                                             ServerMessage message) {
        doNotifyVisiblePlayers(source, message);
        if (source instanceof Player player) {
            sendMessage(player, message);
        }
    }

    @Override
    public void visit(NpcChangeStateEvent event) {
        doNotifyVisiblePlayers(event.source(), event);
    }

    @Override
    public void onEvent(EntityEvent entityEvent) {
        entityEvent.accept(this);
    }

    @Override
    public void visit(PlayerShootEvent event) {
        notifyVisiblePlayersAndSelf(event.source(), event);
    }

    @Override
    public void visit(InventorySlotSwappedEvent event) {
        sendMessage(event.player(), event);
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
    public void visit(OpenTradeWindowEvent event) {
        sendMessage(event.player(), event);
    }

    @Override
    public void visit(PlayerStartTradeEvent event) {
        Optional<Player> insight = findInsight(event.source(), event.targetPlayerId())
                .filter(entity -> entity instanceof Player).map(Player.class::cast);
        insight.ifPresent(another -> tradeManager.handle(event, another, this::onEvent));
    }


    @Override
    public void visit(PlayerToggleKungFuEvent event) {
        notifyVisiblePlayersAndSelf(event.player(), event);
    }

    @Override
    public void visit(PlayerSitDownEvent event) {
        doNotifyVisiblePlayers(event.source(), event);
        if (event.isIncludeSelf()) {
            sendMessage(event.player(), event);
        }
    }

    @Override
    public void visit(PlayerStandUpEvent event) {
        doNotifyVisiblePlayers(event.source(), event);
        if (event.isIncludeSelf()) {
            sendMessage(event.player(), event);
        }
    }

    @Override
    public void visit(PlayerCooldownEvent event) {
        notifyVisiblePlayersAndSelf(event.player(), event);
    }


    @Override
    public void visit(PlayerAttackAoeEvent event) {
        Set<AttackableEntity> entities = scopeManager.filterVisibleEntities(event.source(), AttackableEntity.class);
        event.affect(entities);
    }

    @Override
    public void visit(CreatureDieEvent event) {
        notifyVisiblePlayersAndSelf(event.source(), event);
    }

    @Override
    public void visit(PlayerAttributeEvent event) {
        sendMessage(event.player(), event);
    }

    @Override
    public void visit(PlayerReviveEvent event) {
        notifyVisiblePlayersAndSelf(event.source(), event);
    }

    @Override
    public void visit(EntitySoundEvent event) {
        notifyVisiblePlayersAndSelf(event.source(), event);
    }

    @Override
    public void visit(RemoveEntityEvent event) {
        cleanEntity(event.source(), event);
    }

    @Override
    public void visit(PlayerGainExpEvent event) {
        sendMessage(event.player(), event);
    }

    @Override
    public void visit(MonsterShootEvent event) {
        doNotifyVisiblePlayers(event.source(), event);
    }

    @Override
    public void visit(NpcMoveEvent event) {
        doNotifyVisiblePlayers(event.source(), event);
    }

    @Override
    public void visit(ShowItemEvent event) {
        doNotifyVisiblePlayers(event.source(), event);
    }

    @Override
    public void visit(NpcJoinedEvent event) {
        doNotifyVisiblePlayers(event.source(), event);
    }

    public Optional<Entity> findInsight(Entity source, long id) {
        Set<Entity> entities = scopeManager.filterVisibleEntities(source, Entity.class);
        return entities.stream().filter(e -> e.id() == id).findFirst();
    }

    public void add(Player player, Connection connection) {
        playerConnectionMap.put(player, connection);
        add(player);
    }

    public boolean contains(Player player) {
        return playerConnectionMap.containsKey(player);
    }

    public void add(Entity entity) {
        if (scopeManager.getAllEntities().contains(entity)) {
            return;
        }
        scopeManager.add(entity);
        entity.registerEventListener(this);
    }

    public void remove(Entity entity) {
        scopeManager.remove(entity);
    }

    @Override
    public void sendEvent(EntityEvent entityEvent) {
        entityEvent.accept(this);
    }


    @Override
    public void notifySelf(AbstractPlayerEvent playerEvent) {
        Validate.notNull(playerEvent);
        sendMessage(playerEvent.player(), playerEvent);
    }

    public void remove(Player player) {
        scopeManager.remove(player);
        playerConnectionMap.remove(player);
    }
}
