package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.objects.*;
import org.y1000.entities.players.Player;
import org.y1000.event.EntityEvent;
import org.y1000.sdb.CreateDynamicObjectSdb;
import org.y1000.sdb.CreateEntitySdbRepository;
import org.y1000.util.Coordinate;

import java.util.Set;

@Slf4j
public final class DynamicObjectManagerImpl extends AbstractEntityManager<DynamicObject> implements DynamicObjectManager {

    private final DynamicObjectFactory factory;

    private final CreateEntitySdbRepository createEntitySdbRepository;

    private final EntityIdGenerator entityIdGenerator;

    private final EntityEventSender eventSender;

    private final RespawningEntityManager<RespawnDynamicObject> respawningEntityManager;

    public DynamicObjectManagerImpl(DynamicObjectFactory factory,
                                    CreateEntitySdbRepository createEntitySdbRepository,
                                    EntityIdGenerator entityIdGenerator,
                                    EntityEventSender eventSender) {
        this.factory = factory;
        this.createEntitySdbRepository = createEntitySdbRepository;
        this.entityIdGenerator = entityIdGenerator;
        this.eventSender = eventSender;
        respawningEntityManager = new RespawningEntityManager<>();
    }

    @Override
    public void onEvent(EntityEvent entityEvent) {
        if (!(entityEvent.source() instanceof DynamicObject object))  {
            return;
        }
        if (entityEvent instanceof RemoveEntityEvent) {
            delete(object);
        } else if (entityEvent instanceof UpdateDynamicObjectEvent updateDynamicObjectEvent) {
            eventSender.notifyVisiblePlayers(entityEvent.source(), updateDynamicObjectEvent);
        }
    }

    @Override
    protected Logger log() {
        return log;
    }

    @Override
    protected void onAdded(DynamicObject entity) {
        eventSender.add(entity);
        eventSender.notifyVisiblePlayers(entity, entity.captureInterpolation());
        entity.registerEventListener(this);
        log.debug("Added object {}.", entity.id());
    }

    @Override
    protected void onDeleted(DynamicObject entity) {
        eventSender.remove(entity);
        entity.deregisterEventListener(this);
        if (entity instanceof RespawnDynamicObject respawnDynamicObject) {
            respawningEntityManager.add(respawnDynamicObject, respawnDynamicObject.respawnTime());
        }
    }

    private void tryRespawn(RespawnDynamicObject respawnDynamicObject) {
        respawnDynamicObject.respawn();
        add(respawnDynamicObject);
    }

    @Override
    public void update(long delta) {
        updateManagedEntities(delta);
        respawningEntityManager.update(delta).forEach(this::tryRespawn);
    }

    public void init(RealmMap map, int id) {
        if (!createEntitySdbRepository.objectSdbExists(id)) {
            return;
        }
        CreateDynamicObjectSdb createDynamicObjectSdb = createEntitySdbRepository.loadObject(id);
        Set<String> numbers = createDynamicObjectSdb.getNumbers();
        for (String number : numbers) {
            String name = createDynamicObjectSdb.getName(number);
            /*if (!name.contains("狐狸")) {
                continue;
            }*/
            var obj = factory.createDynamicObject(name, entityIdGenerator.next(), map, Coordinate.xy(createDynamicObjectSdb.getX(number), createDynamicObjectSdb.getY(number)));
            add(obj);
        }
    }

    @Override
    public void triggerDynamicObject(long id, Player player, int useSlot) {
        Validate.notNull(player);
        find(id, TriggerDynamicObject.class).ifPresent(obj -> obj.trigger(player, useSlot));
    }
}
