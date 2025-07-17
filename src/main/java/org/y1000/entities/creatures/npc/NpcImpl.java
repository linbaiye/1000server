package org.y1000.entities.creatures.npc;

import lombok.Getter;
import org.y1000.entities.AbstractActiveEntity;
import org.y1000.entities.Direction;
import org.y1000.entities.Entity;
import org.y1000.entities.FilterVisibleEvent;
import org.y1000.entities.creatures.npc.event.NpcEvent;
import org.y1000.message.I2ClientMessage;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NpcImpl extends AbstractActiveEntity implements Npc {

    private final List<Object> abilities;
    private final NpcEventListener listener;
    private Coordinate coordinate;

    @Getter
    private final RealmMap realmMap;

    private NpcAI ai;

    @Getter
    private final String viewName;

    @Getter
    private final String animate;

    @Getter
    private final String shape;

    @Getter
    private final Coordinate spawnCoordinate;

    private Direction direction;

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
        super(id);
        this.abilities = abilities;
        this.listener = listener;
        this.realmMap = realmMap;
        this.viewName = viewName != null ? viewName : "";
        this.spawnCoordinate = coordinate;
        this.coordinate = coordinate;
        this.animate = animate;
        this.shape = shape;
        this.idName = idName;
        this.direction = direction;
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
    public Coordinate coordinate() {
        return coordinate;
    }

    @Override
    public <AB> Optional<AB> findAbility(Class<AB> type) {
        return abilities.stream()
                .filter(a -> type.isAssignableFrom(a.getClass()))
                .findFirst().map(type::cast);
    }

    @Override
    public Direction direction() {
        return direction;
    }

    @Override
    public void changeCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
        realmMap.occupy(this);
    }

    @Override
    public void changeDirection(Direction direction) {
        this.direction = direction;
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
}
