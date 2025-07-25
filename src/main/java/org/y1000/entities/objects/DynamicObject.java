package org.y1000.entities.objects;

import org.y1000.entities.AbstractActiveEntity;
import org.y1000.message.I2ClientMessage;
import org.y1000.realm.DynamicObjectEventListener;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class DynamicObject extends AbstractActiveEntity  {

    private final String viewName;


    private final List<Object> abilities;

    private final Set<Coordinate> occupiedCoordinates;

    private final Coordinate coordinate;

    private DynamicAbility currentAbility;

    private final DynamicObjectEventListener listener;

    private final String shape;


    protected DynamicObject(long id,
                            String viewName,
                            List<Object> abilities,
                            Set<Coordinate> guardCoordinates,
                            Coordinate coordinate,
                            DynamicObjectEventListener listener, String shape) {
        super(id);
        this.viewName = viewName;
        this.abilities = abilities;
        this.occupiedCoordinates = guardCoordinates;
        this.coordinate = coordinate;
        this.listener = listener;
        this.shape = shape;
        currentAbility = findAbility(StaticAbility.class).orElseThrow();
    }

    public Set<Coordinate> occupiedCoordinates() {
        return occupiedCoordinates;
    }

    public void join(RealmMap realmMap) {
        realmMap.occupy(this);
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


    private void onHurt(DynamicObjectHurtAbility hurtAbility) {
        if (hurtAbility.currentLife() <= 0)
            triggered();
//            sentEvent();
    }

    public String shape() {
        return shape;
    }

    @Override
    public Coordinate coordinate() {
        return coordinate;
    }


    @Override
    public I2ClientMessage captureSnapshot() {
        List<Animation> animations = new ArrayList<>();
        for (Object ability : abilities) {
            if (ability instanceof DynamicAbility a) {
                a.collectAnimations(animations);
            }
        }
        return DynamicObjectSnapshot.of(this, animations, currentAbility.currentAnimation());
    }
}
