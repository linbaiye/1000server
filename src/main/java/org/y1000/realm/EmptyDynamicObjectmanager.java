package org.y1000.realm;

import org.y1000.entities.objects.DynamicObject;
import org.y1000.entities.players.Player;
import org.y1000.event.EntityEvent;

import java.util.Optional;

public final class EmptyDynamicObjectmanager implements DynamicObjectManager {

    public static final EmptyDynamicObjectmanager INSTANCE = new EmptyDynamicObjectmanager();

    @Override
    public void onEvent(EntityEvent entityEvent) {

    }

    @Override
    public void update(long delta) {

    }

    @Override
    public Optional<DynamicObject> find(long id) {
        return Optional.empty();
    }

    @Override
    public boolean contains(DynamicObject entity) {
        return false;
    }

    @Override
    public void init() {

    }

    @Override
    public void triggerDynamicObject(long id, Player player, int useSlot) {

    }
}
