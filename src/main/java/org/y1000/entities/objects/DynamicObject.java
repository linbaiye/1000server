package org.y1000.entities.objects;

import org.y1000.entities.AbstractActiveEntity;
import org.y1000.entities.ActiveEntity;
import org.y1000.message.I2ClientMessage;
import org.y1000.realm.DynamicObjectEventListener;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.*;

public class DynamicObject extends AbstractActiveEntity {

    private final String viewName;

    private final List<Object> abilities;

    private final Set<Coordinate> occupiedCoordinates;

    private final Coordinate coordinate;

    private DynamicAbility currentAbility;

    private final DynamicObjectEventListener listener;

    private final String shape;

    private RealmMap realmMap;

    private boolean occupying;

    protected DynamicObject(long id,
                            String viewName,
                            List<Object> abilities,
                            Set<Coordinate> guardCoordinates,
                            Coordinate coordinate,
                            DynamicObjectEventListener listener,
                            String shape) {
        super(id);
        this.viewName = viewName;
        this.abilities = abilities;
        this.occupiedCoordinates = guardCoordinates;
        this.coordinate = coordinate;
        this.listener = listener;
        this.shape = shape;
        currentAbility = findAbility(StaticAbility.class).orElseThrow();
        findAbility(DynamicObjectHurtAbility.class).ifPresent(h -> h.setOnHurt(this::onHurt));
        occupying = true;
    }

    public Set<Coordinate> occupiedCoordinates() {
        return occupiedCoordinates;
    }

    public void join(RealmMap realmMap) {
        realmMap.occupy(this);
        this.realmMap = realmMap;
    }

    public void free() {
        realmMap.free(this);
        occupying = false;
    }

    @Override
    public void update(int delta) {
        currentAbility.update(this, delta);
    }

    public Optional<String> viewName() {
        return Optional.ofNullable(viewName);
    }

    public void sentEvent(DynamicObjectEvent event) {
        listener.onEvent(event);
    }

    @Override
    public <AB> Optional<AB> findAbility(Class<AB> type) {
        return abilities.stream().filter(e -> type.isAssignableFrom(e.getClass()))
                .map(type::cast).findFirst();
    }

    public void triggered() {
        findAbility(OpenAbility.class)
                .ifPresent(o -> {
                    currentAbility = o;
                    o.triggered(this);
                });
    }


    private void onHurt(ActiveEntity attacker, DynamicObjectHurtAbility hurtAbility) {
        hurtAbility.apply(this, attacker);
        if (hurtAbility.isDead())
            triggered();
    }

    public String shape() {
        return shape;
    }

    @Override
    public Coordinate coordinate() {
        return coordinate;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DynamicObject object = (DynamicObject) o;
        return id() == object.id();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id());
    }

    @Override
    public I2ClientMessage captureSnapshot() {
        List<Animation> animations = new ArrayList<>();
        for (Object ability : abilities) {
            if (ability instanceof DynamicAbility a) {
                a.collectAnimations(animations);
            }
        }
        return DynamicObjectSnapshot.of(this, animations, currentAbility.currentAnimation(), occupying);
    }
}
