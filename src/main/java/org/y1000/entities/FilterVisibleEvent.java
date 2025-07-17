package org.y1000.entities;

import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.creatures.npc.event.NpcEvent;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.PlayerEvent;
import org.y1000.event.EntityEvent;
import org.y1000.realm.NpcEventHandler;
import org.y1000.realm.PlayerEventHandler;

import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class FilterVisibleEvent<T extends Entity> implements EntityEvent<T> {

    protected Set<Entity> result;
    private final T source;

    protected final Predicate<Entity> filter;
    @Override
    public T source() {
        return source;
    }

    private FilterVisibleEvent(T entity,
                              Predicate<Entity> filter) {
        this.source = entity;
        this.filter = filter;
        result = Collections.emptySet();
    }

    public <R extends Entity> Stream<R> resultStream(Class<R> type) {
        return result.stream().filter(e -> type.isAssignableFrom(e.getClass()))
                        .map(type::cast);
    }

    public <R extends Entity> Stream<R> hurtableStream(Class<R> type) {
        return result.stream()
                .filter(e -> type.isAssignableFrom(e.getClass()))
                .map(type::cast);
    }

    public static class NpcFilterVisibleEntityEvent extends FilterVisibleEvent<Npc> implements NpcEvent {
        private NpcFilterVisibleEntityEvent(Npc entity, Predicate<Entity> filter) {
            super(entity, filter);
        }
        @Override
        public void accept(NpcEventHandler handler) {
            result = handler.filterVisible(source(), filter);
        }
    }

    public static class PlayerFilterVisibleEntityEvent extends FilterVisibleEvent<Player> implements PlayerEvent  {
        private PlayerFilterVisibleEntityEvent(Player entity, Predicate<Entity> filter) {
            super(entity, filter);
        }
        @Override
        public void accept(PlayerEventHandler handler) {
            result = handler.filterVisible(source(), filter);
        }
    }

    public static NpcFilterVisibleEntityEvent nearbyAlive(Npc npc, int d) {
        return nearbyAttackable(npc, d);
    }

    public static NpcFilterVisibleEntityEvent nearbyAttackable(Npc npc, int d) {
        return new NpcFilterVisibleEntityEvent(npc, e -> e instanceof ActiveEntity entity
                && entity.findAbility(HurtAbility.class).map(HurtAbility::canBeAttacked).orElse(false)
                && e.coordinate().directDistance(npc.coordinate()) <= d);
    }

    public static PlayerFilterVisibleEntityEvent filterAOE(Player player) {
        return new PlayerFilterVisibleEntityEvent(player, e -> e.coordinate().directDistance(player.coordinate()) <= 1 &&
                e instanceof ActiveEntity entity &&
                entity.findAbility(HurtAbility.class).map(HurtAbility::canBeAttacked).orElse(false));
    }

}
