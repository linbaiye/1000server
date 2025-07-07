package org.y1000.entities.creatures.npc;

import lombok.Getter;
import lombok.Setter;
import org.y1000.entities.AbstractActiveEntity;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.npc.event.NpcEvent;
import org.y1000.message.I2ClientMessage;
import org.y1000.message.NpcSnapshot;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.*;

public class Npc extends AbstractActiveEntity {

    private final Set<NpcAction> actions;
    private final Set<Object> abilities;
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

    @Setter
    private Direction direction;

    @Getter
    private final String idName;

    public Npc(long id,
               Set<Object> abilities,
               String viewName,
               Coordinate coordinate,
               Set<NpcAction> actions,
               NpcEventListener listener,
               RealmMap realmMap,
               String animate,
               String shape,
               String idName) {
        super(id);
        this.abilities = abilities;
        this.actions = actions;
        this.listener = listener;
        this.realmMap = realmMap;
        this.viewName = viewName != null ? viewName : "";
        this.spawnCoordinate = coordinate;
        this.coordinate = coordinate;
        this.animate = animate;
        this.shape = shape;
        this.idName = idName;
        this.direction = Direction.DOWN;
        realmMap.occupy(this);
    }

    public void sendEvent(NpcEvent event) {
        listener.onEvent(event);
    }

    public <A extends NpcAction> Optional<A> findAction(Class<A> type) {
        return actions.stream()
                .filter(a -> type.isAssignableFrom(a.getClass()))
                .findFirst().map(type::cast);
    }

    public void changeAI(NpcAI newAi) {
        ai = newAi;
    }

    @Override
    public void update(int delta) {
        ai.update(delta);
    }

    @Override
    public Coordinate coordinate() {
        return coordinate;
    }

    public <AB> Optional<AB> findAbility(Class<AB> type) {
        return abilities.stream()
                .filter(a -> type.isAssignableFrom(a.getClass()))
                .findFirst().map(type::cast);
    }

    public Direction direction() {
        return direction;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
        realmMap.occupy(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Npc npc = (Npc) o;
        return id() == npc.id();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id());
    }


    @Override
    public I2ClientMessage captureSnapshot() {
        return NpcSnapshot.of(this, ai.currentAction());
    }
}
