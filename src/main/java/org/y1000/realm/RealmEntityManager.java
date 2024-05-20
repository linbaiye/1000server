package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.Entity;
import org.y1000.entities.Projectile;
import org.y1000.entities.creatures.event.ChangeStateEvent;
import org.y1000.entities.creatures.event.CreatureAttackEvent;
import org.y1000.entities.creatures.event.CreatureHurtEvent;
import org.y1000.entities.creatures.event.CreatureShootEvent;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.PlayerAttackEvent;
import org.y1000.entities.players.event.PlayerAttackEventResponse;
import org.y1000.entities.players.event.RewindEvent;
import org.y1000.message.AbstractPositionEvent;
import org.y1000.message.InputResponseMessage;
import org.y1000.message.ServerMessage;
import org.y1000.message.serverevent.JoinedRealmEvent;
import org.y1000.message.RemoveEntityMessage;
import org.y1000.message.serverevent.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

@Slf4j
final class RealmEntityManager implements EntityEventListener,
        PlayerEventVisitor {

    private final RelevantScopeManager scopeManager = new RelevantScopeManager();

    private final Set<Projectile> projectiles = new HashSet<>();

    @Override
    public void visit(InputResponseMessage inputResponseMessage) {
        inputResponseMessage.player().connection().write(inputResponseMessage);
        visit(inputResponseMessage.positionMessage());
    }

    private void notifyInterpolation(Player joined, Entity entity) {
        joined.connection().write(entity.captureInterpolation());
        if (entity instanceof Player another) {
            another.connection().write(joined.captureInterpolation());
        }
    }

    private void notifyOutsightOrInsight(Entity moved,
                                         Entity affected) {
        boolean outOfView = scopeManager.outOfScope(moved, affected);
        if (outOfView) {
            if (moved instanceof Player movedPlayer) {
                movedPlayer.connection().write(new RemoveEntityMessage(affected.id()));
            }
            if (affected instanceof Player affectedPlayer) {
                affectedPlayer.connection().write(new RemoveEntityMessage(moved.id()));
            }
        } else {
            if (moved instanceof Player movedPlayer) {
                movedPlayer.connection().write(affected.captureInterpolation());
            }
            if (affected instanceof Player affectedPlayer) {
                affectedPlayer.connection().write(moved.captureInterpolation());
            }
        }
    }


    @Override
    public void visit(AbstractPositionEvent positionEvent) {
        Entity source = positionEvent.source();
        Set<Entity> affectedEntities = scopeManager.update(source);
        affectedEntities.forEach(entity -> notifyOutsightOrInsight(source, entity));
        notifyVisiblePlayers(source, positionEvent);
    }

    @Override
    public void visit(JoinedRealmEvent joinedRealmEvent) {
        joinedRealmEvent.player().connection().write(joinedRealmEvent);
        var visibleEntities = scopeManager.filterVisibleEntities(joinedRealmEvent.source(), Entity.class);
        visibleEntities.forEach(entity -> notifyInterpolation(joinedRealmEvent.player(), entity));
    }

    @Override
    public void visit(PlayerLeftEvent event) {
        Set<Entity> affected = scopeManager.remove(event.player());
        affected.stream()
                .filter(entity -> entity instanceof Player)
                .map(Player.class::cast)
                .forEach(player -> player.connection().write(event));
    }

    @Override
    public void visit(CreatureAttackEvent event) {
        notifyVisiblePlayers(event.source(), event);
    }

    @Override
    public void visit(RewindEvent event) {
        event.player().connection().write(event);
        notifyVisiblePlayers(event.source(), event.toSetPosition());
    }

    @Override
    public void visit(PlayerAttackEventResponse event) {
        event.player().connection().write(event);
        event.toPlayerAttackEvent()
                .ifPresent(e -> notifyVisiblePlayers(event.source(), e));
    }

    private void notifyVisiblePlayers(Entity source, ServerMessage serverMessage) {
        scopeManager.filterVisibleEntities(source, Player.class)
                .forEach(player -> player.connection().write(serverMessage));
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
        notifyVisiblePlayers(source, message);
        if (source instanceof Player player) {
            player.connection().write(message);
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

    private void update(Entity entity, int delta) {
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

    public Optional<Entity> findInsight(Entity source, long id) {
        Set<Entity> entities = scopeManager.filterVisibleEntities(source, Entity.class);
        return entities.stream().filter(e -> e.id() == id).findFirst();
    }

    public void add(Entity entity) {
        if (scopeManager.getAllEntities().contains(entity)) {
            return;
        }
        scopeManager.add(entity);
        entity.registerOrderedEventListener(this);
    }
}
