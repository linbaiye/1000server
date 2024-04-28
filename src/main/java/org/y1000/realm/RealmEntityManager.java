package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.Entity;
import org.y1000.entities.players.Player;
import org.y1000.message.AbstractPositionEvent;
import org.y1000.message.InputResponseMessage;
import org.y1000.message.serverevent.JoinedRealmEvent;
import org.y1000.message.RemoveEntityMessage;
import org.y1000.message.serverevent.*;

import java.util.Optional;
import java.util.Set;

@Slf4j
public final class RealmEntityManager implements EntityEventListener,
        PlayerEventHandler {

    private final RelevantScopeManager scopeManager = new RelevantScopeManager();

    @Override
    public void handle(InputResponseMessage inputResponseMessage) {
        inputResponseMessage.player().connection().write(inputResponseMessage);
        handle(inputResponseMessage.positionMessage());
    }

    private void notifyPlayerJoined(Player joined, Entity entity) {
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
    public void handle(AbstractPositionEvent positionEvent) {
        Entity source = positionEvent.source();
        Set<Entity> affectedEntities = scopeManager.update(source);
        affectedEntities.forEach(entity -> notifyOutsightOrInsight(source, entity));
        Set<Player> players = scopeManager.filterVisibleEntities(source, Player.class);
        players.forEach(p -> p.connection().write(positionEvent));
    }

    @Override
    public void handle(JoinedRealmEvent loginMessage) {
        loginMessage.player().connection().write(loginMessage);
        var visibleEntities = scopeManager.filterVisibleEntities(loginMessage.source(), Entity.class);
        visibleEntities.forEach(entity -> notifyPlayerJoined(loginMessage.player(), entity));
    }

    @Override
    public void handle(PlayerLeftEvent event) {
        Set<Entity> affected = scopeManager.remove(event.player());
        affected.stream()
                .filter(entity -> entity instanceof Player)
                .map(Player.class::cast)
                .forEach(player -> player.connection().write(event));
    }

    @Override
    public void OnEvent(EntityEvent entityEvent) {
        entityEvent.accept(this);
    }

    public void updateEntities(long delta) {
        scopeManager.getAllEntities().forEach(e -> e.update(delta));
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
