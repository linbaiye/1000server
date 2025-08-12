package org.y1000.entities.creatures.npc;

import lombok.Getter;
import org.y1000.entities.AbstractActiveEntity;
import org.y1000.entities.Direction;
import org.y1000.entities.Entity;
import org.y1000.entities.FilterVisibleEvent;
import org.y1000.entities.creatures.AbstractCreature;
import org.y1000.entities.creatures.npc.event.NpcEvent;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.PlayerLeaveListener;
import org.y1000.message.I2ClientMessage;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NpcImpl extends AbstractCreature implements Npc {

    private final List<Object> abilities;
    private final NpcEventListener listener;

    private final RealmMap realmMap;

    private NpcAI ai;

    @Getter
    private final String animate;

    @Getter
    private final String shape;

    @Getter
    private final Coordinate spawnCoordinate;

    @Getter
    private final String idName;

    @Getter
    private final int wanderRage;

    private final List<CooldownAbility> cooldownList;

    public NpcImpl(long id,
                   List<Object> abilities,
                   String viewName,
                   Coordinate coordinate,
                   NpcEventListener listener,
                   RealmMap realmMap,
                   String animate,
                   String shape,
                   String idName,
                   Direction direction,
                   int wanderRage) {
        super(id, coordinate, direction, viewName);
        this.abilities = abilities;
        this.listener = listener;
        this.realmMap = realmMap;
        this.spawnCoordinate = coordinate;
        this.animate = animate;
        this.shape = shape;
        this.idName = idName;
        this.wanderRage = wanderRage;
        this.cooldownList = abilities.stream().filter(a -> CooldownAbility.class.isAssignableFrom(a.getClass()))
                        .map(CooldownAbility.class::cast).collect(Collectors.toList());
        realmMap.occupy(this);
    }

    public void sendEvent(NpcEvent event) {
        if (listener != null)
            listener.onEvent(event);
    }

    @Override
    public <T> Optional<T> findAbility(Class<T> type, Predicate<? super T> filter) {
        return abilities.stream().filter(a -> type.isAssignableFrom(a.getClass()))
                .map(type::cast)
                .filter(filter)
                .findFirst();
    }

    @Override
    public <T> List<T> findAbilities(Class<T> type) {
        return abilities.stream().filter(a -> type.isAssignableFrom(a.getClass()))
                .map(type::cast)
                .toList();
    }

    @Override
    public void instantKill() {
        ai.instantKill();
    }

    @Override
    public void changeAI(NpcAI newAi) {
        ai = newAi;
    }

    @Override
    public void startAI() {
        ai.start();
    }

    @Override
    public void update(int delta) {
        cooldownList.forEach(a -> a.cooldown(delta));
        ai.update(delta);
    }

    @Override
    public RealmMap realmMap() {
        return realmMap;
    }

    @Override
    public <AB> Optional<AB> findAbility(Class<AB> type) {
        return abilities.stream()
                .filter(a -> type.isAssignableFrom(a.getClass()))
                .findFirst().map(type::cast);
    }


    @Override
    public void free() {
        realmMap.free(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NpcImpl npc = (NpcImpl) o;
        return id() == npc.id();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id());
    }


    @Override
    public I2ClientMessage captureSnapshot() {
        return ai.captureSnapshot();
    }


    @Override
    public Set<Entity> getEntitiesAt(Set<Coordinate> coordinates) {
        var event = FilterVisibleEvent.filterVisibleAt(this, coordinates);
        sendEvent(event);
        return event.resultStream(Entity.class).collect(Collectors.toSet());
    }

    @Override
    public Optional<String> clickText() {
        return Optional.of(viewName() + "。");
    }

}
