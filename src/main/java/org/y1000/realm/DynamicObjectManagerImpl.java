package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.y1000.entities.EntityLifebarEvent;
import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.objects.*;
import org.y1000.entities.players.Player;
import org.y1000.event.EntityEvent;
import org.y1000.sdb.CreateDynamicObjectSdb;
import org.y1000.util.Coordinate;

import java.util.Set;

@Slf4j
public final class DynamicObjectManagerImpl extends AbstractActiveEntityManager<DynamicObject> implements DynamicObjectManager {

    private final DynamicObjectFactory factory;

    private final EntityIdGenerator entityIdGenerator;

    private final EntityEventSender eventSender;

    private final GroundItemManager itemManager;

    private final RespawningEntityManager<RespawnDynamicObject> respawningEntityManager;

    private final CreateDynamicObjectSdb createDynamicObjectSdb;

    public DynamicObjectManagerImpl(DynamicObjectFactory factory,
                                    EntityIdGenerator entityIdGenerator,
                                    EntityEventSender eventSender,
                                    GroundItemManager itemManager,
                                    CreateDynamicObjectSdb dynamicObjectSdb) {
        this.factory = factory;
        this.entityIdGenerator = entityIdGenerator;
        this.eventSender = eventSender;
        this.itemManager = itemManager;
        this.createDynamicObjectSdb = dynamicObjectSdb;
        respawningEntityManager = new RespawningEntityManager<>();
    }

    @Override
    public void onEvent(EntityEvent entityEvent) {
        if (!(entityEvent.source() instanceof DynamicObject object))  {
            return;
        }
        if (entityEvent instanceof RemoveEntityEvent) {
            var entity = entityEvent.source();
            eventSender.remove(entity);
            if (entity instanceof RespawnDynamicObject respawnDynamicObject) {
                respawningEntityManager.add(respawnDynamicObject, respawnDynamicObject.respawnTime());
            }
            remove(object);
        } else if (entityEvent instanceof UpdateDynamicObjectEvent updateDynamicObjectEvent) {
            eventSender.notifyVisiblePlayers(entityEvent.source(), updateDynamicObjectEvent);
        } else if (entityEvent instanceof EntityLifebarEvent entityLifebarEvent) {
            eventSender.notifyVisiblePlayers(entityEvent.source(), entityLifebarEvent);
        } else if (entityEvent instanceof DynamicObjectDieEvent dieEvent) {
            createDynamicObjectSdb.getFirstNo(dieEvent.object().idName())
                            .flatMap(createDynamicObjectSdb::getDropItem)
                            .ifPresent(items -> itemManager.dropItem(items, dieEvent.object().coordinate()));
        }
    }

    @Override
    protected Logger log() {
        return log;
    }

    @Override
    protected void onAdded(DynamicObject entity) {

    }

    @Override
    protected void onDeleted(DynamicObject entity) {

    }

    private void addObject(DynamicObject entity) {
        eventSender.add(entity);
        entity.registerEventListener(this);
        eventSender.notifyVisiblePlayers(entity, entity.captureInterpolation());
        add(entity);
        log.debug("Added object {}.", entity.id());
    }

    private void tryRespawn(RespawnDynamicObject respawnDynamicObject) {
        respawnDynamicObject.respawn();
        addObject(respawnDynamicObject);
    }

    @Override
    public void update(long delta) {
        updateManagedEntities(delta);
        respawningEntityManager.update(delta).forEach(this::tryRespawn);
    }

    @Override
    public void init(RealmMap map) {
        Set<String> numbers = createDynamicObjectSdb.getNumbers();
        for (String number : numbers) {
            String name = createDynamicObjectSdb.getName(number);
            var obj = factory.createDynamicObject(name, entityIdGenerator.next(), map, Coordinate.xy(createDynamicObjectSdb.getX(number), createDynamicObjectSdb.getY(number)));
            if (obj != null)
                addObject(obj);
        }
    }

    @Override
    public void triggerDynamicObject(long id, Player player, int useSlot) {
        Validate.notNull(player);
        find(id, TriggerDynamicObject.class).ifPresent(obj -> obj.trigger(player, useSlot));
    }
}
