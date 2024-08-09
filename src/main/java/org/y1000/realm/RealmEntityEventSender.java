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
import org.y1000.message.serverevent.*;
import org.y1000.network.Connection;

import java.util.*;

/**
 * Responsible for sending events to visible clients.
 */
@Slf4j
final class RealmEntityEventSender implements EntityEventListener,
        PlayerEventVisitor, EntityEventSender {

    private final AOIManager scopeManager;

    private final Map<Player, Connection> playerConnectionMap = new HashMap<>(100);

    public RealmEntityEventSender(AOIManager scopeManager) {
        Validate.notNull(scopeManager);
        this.scopeManager = scopeManager;
    }

    @Override
    public void visit(InputResponseMessage inputResponseMessage) {
        sendMessage(inputResponseMessage.player(), inputResponseMessage);
        visit(inputResponseMessage.positionMessage());
    }

    private void notifyInterpolation(Player joined, Entity entity) {
        sendMessage(joined, entity.captureInterpolation());
        if (entity instanceof Player another) {
            sendMessage(another, joined.captureInterpolation());
        }
    }

    private void sendMessage(Player player, ServerMessage serverMessage) {
        if (playerConnectionMap.containsKey(player))
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
        Set<Entity> outViewEntities = scopeManager.update(source);
        outViewEntities.forEach(entity -> notifyOutsightOrInsight(source, entity));
        doNotifyVisiblePlayers(source, positionEvent);
        if (positionEvent.source() instanceof Player player) {
            sendMessage(player, positionEvent);
        }
    }


    @Override
    public void notifyPlayerOfEntities(Player player) {
        if (player == null) {
            return;
        }
        var visibleEntities = scopeManager.filterVisibleEntities(player, Entity.class);
        visibleEntities.forEach(entity -> notifyInterpolation(player, entity));
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
    public void visit(CreatureHurtEvent event) {
        notifyVisiblePlayersAndSelf(event.source(), event);
    }

    public void notifyVisiblePlayersAndSelf(Entity source,
                                            ServerMessage message) {
        Validate.notNull(source);
        Validate.notNull(message);
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
    public void visit(PlayerUnequipEvent event) {
        notifyVisiblePlayersAndSelf(event.player(), event);
    }

    @Override
    public void visit(PlayerEquipEvent event) {
        notifyVisiblePlayersAndSelf(event.player(), event);
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
        Set<AttackableActiveEntity> entities = scopeManager.filterVisibleEntities(event.source(), AttackableActiveEntity.class);
        event.affect(entities);
    }

    @Override
    public void visit(CreatureDieEvent event) {
        notifyVisiblePlayersAndSelf(event.source(), event);
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
        notifyVisiblePlayers(event.source(), event);
    }


    @Override
    public void visit(MonsterShootEvent event) {
        doNotifyVisiblePlayers(event.source(), event);
    }

    @Override
    public void visit(NpcMoveEvent event) {
        Entity source = event.source();
        Set<Entity> inOrOutViewEntities = scopeManager.update(source);
        inOrOutViewEntities.forEach(entity -> notifyOutsightOrInsight(source, entity));
        doNotifyVisiblePlayers(source, event);
    }

    @Override
    public void visit(ShowItemEvent event) {
        doNotifyVisiblePlayers(event.source(), event);
    }


    public void add(Player player, Connection connection) {
        Validate.notNull(player);
        Validate.notNull(connection);
        playerConnectionMap.put(player, connection);
        add(player);
    }


    public void add(Entity entity) {
        Validate.notNull(entity);
        if (scopeManager.contains(entity)) {
            return;
        }
        scopeManager.add(entity);
        if (entity instanceof ActiveEntity activeEntity)
            activeEntity.registerEventListener(this);
    }

    public void remove(Entity entity) {
        scopeManager.remove(entity);
    }

    @Override
    public void sendEvent(EntityEvent entityEvent) {
        Validate.notNull(entityEvent);
        entityEvent.accept(this);
    }


    @Override
    public void notifySelf(AbstractPlayerEvent playerEvent) {
        Validate.notNull(playerEvent);
        sendMessage(playerEvent.player(), playerEvent);
    }

    public Connection remove(Player player) {
        scopeManager.remove(player);
        return playerConnectionMap.remove(player);
    }

    @Override
    public void updateScope(Player player) {
        scopeManager.update(player);
    }
}
