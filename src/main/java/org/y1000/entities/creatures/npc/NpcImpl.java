package org.y1000.entities.creatures.npc;

import lombok.Getter;
import org.y1000.entities.AbstractActiveEntity;
import org.y1000.entities.Direction;
import org.y1000.entities.HurtAbility;
import org.y1000.entities.creatures.npc.event.NpcEvent;
import org.y1000.message.I2ClientMessage;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.*;

public class NpcImpl extends AbstractActiveEntity implements Npc {

    private final List<NpcAbility> abilities;
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

    private final int escapeLife;

    @Getter
    private final int wanderRage;

    private final String sound;

    private final int viewRange;

    private final List<Cooldown> cooldownAbilities;

    public NpcImpl(long id,
                   List<NpcAbility> abilities,
                   String viewName,
                   Coordinate coordinate,
                   NpcEventListener listener,
                   RealmMap realmMap,
                   String animate,
                   String shape,
                   String idName,
                   Direction direction,
                   int escapeLife,
                   int wanderRage,
                   String sound,
                   int viewRange) {
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
        this.escapeLife = escapeLife;
        this.wanderRage = wanderRage;
        this.sound = sound;
        this.viewRange = viewRange;
        cooldownAbilities = abilities.stream()
                .filter(a -> Cooldown.class.isAssignableFrom(a.getClass()))
                .map(Cooldown.class::cast)
                .toList();
        realmMap.occupy(this);
    }

    public void sendEvent(NpcEvent event) {
        if (listener != null)
            listener.onEvent(event);
    }

    @Override
    public Optional<String> sound() {
        return Optional.ofNullable(sound);
    }

    @Override
    public int viewRange() {
        return viewRange;
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
        cooldownAbilities.forEach(a -> a.cooldown(delta));
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

    public boolean needToEscape() {
        return findAbility(HurtAbility.class).map(hurtAbility -> hurtAbility.currentLife() <= escapeLife)
                .orElse(false);
    }

    @Override
    public I2ClientMessage captureSnapshot() {
        return ai.captureSnapshot();
    }
}
