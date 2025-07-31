package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.objects.*;
import org.y1000.entities.players.Player;
import org.y1000.event.EntityEvent;
import org.y1000.message.I2ClientMessage;
import org.y1000.sdb.CreateDynamicObjectSdb;
import org.y1000.util.Coordinate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@Slf4j
public final class DynamicObjectManagerImpl extends AbstractActiveEntityManager<DynamicObject>
        implements DynamicObjectManager, DynamicObjectEventListener, DynamicObjectEventHandler {

    private final DynamicObjectFactory factory;

    private final EntityIdGenerator entityIdGenerator;

    private final GroundItemManager itemManager;

    private final EntityTimerManager<DynamicObject> respawningEntityManager;

    private final CreateDynamicObjectSdb createDynamicObjectSdb;

    private final RealmEventSender crossRealmEventSender;

    private final Map<DynamicObject, String> objectNumberMap;

    private final RealmMap realmMap;

    private final NpcCaller npcCaller;

    public DynamicObjectManagerImpl(DynamicObjectFactory factory,
                                    EntityIdGenerator entityIdGenerator,
                                    MessageSender eventSender,
                                    GroundItemManager itemManager,
                                    CreateDynamicObjectSdb dynamicObjectSdb,
                                    RealmEventSender crossRealmEventSender,
                                    RealmMap realmMap,
                                    AOIManager aoiManager, NpcCaller npcCaller) {
        super(aoiManager, eventSender);
        this.factory = factory;
        this.entityIdGenerator = entityIdGenerator;
        this.itemManager = itemManager;
        this.createDynamicObjectSdb = dynamicObjectSdb;
        this.crossRealmEventSender = crossRealmEventSender;
        this.realmMap = realmMap;
        this.npcCaller = npcCaller;
        respawningEntityManager = new EntityTimerManager<>();
        objectNumberMap = new HashMap<>();
    }

    @Override
    public void onEvent(EntityEvent entityEvent) {

    }

    @Override
    protected Logger log() {
        return log;
    }

    private void tryRespawn(DynamicObject object) {
        var number = objectNumberMap.remove(object);
        if (number == null)
            return;
        DynamicObject dynamicObject = factory.create(entityIdGenerator.next(), number, this, createDynamicObjectSdb);
        addObject(dynamicObject);
        objectNumberMap.put(dynamicObject, number);
    }

    private void addObject(DynamicObject object) {
        object.join(realmMap);
        add(object);
        Set<Player> players = getAoiManager().filterVisibleEntities(object, Player.class);
        if (players.isEmpty())
            return;
        I2ClientMessage snapshot = object.captureSnapshot();
        players.forEach(p -> getMessageSender().sendTo(p, snapshot));
    }


    @Override
    public void update(long delta) {
        updateManagedEntities(delta);
        respawningEntityManager.update(delta).forEach(this::tryRespawn);
    }


    @Override
    public void init() {
        Set<String> numbers = createDynamicObjectSdb.getNumbers();
        for (String number : numbers) {
            DynamicObject dynamicObject = factory.create(entityIdGenerator.next(), number, this, createDynamicObjectSdb);
            addObject(dynamicObject);
            objectNumberMap.put(dynamicObject, number);
        }
    }

    public void onRemove(DynamicObjectRemoveEvent event) {
        var dynamicObject = event.source();
        sendToVisiblePlayers(event.source(), event);
        dynamicObject.free();
        remove(dynamicObject);
        if (event.getRespawnMillis() > 0)
            respawningEntityManager.add(dynamicObject, event.getRespawnMillis());
    }

    @Override
    public void onRespawn(DynamicObject object) {
        sendToVisiblePlayers(object, DynamicObjectRemoveEvent.of(object));
        remove(object);
        tryRespawn(object);
    }

    @Override
    public void dropItem(String name, int number, Coordinate dropAt) {
        itemManager.dropItem(name, number, dropAt);
    }

    @Override
    public void callNpc(String npcName, ActiveEntity enemy, Coordinate callAt) {
        npcCaller.call(npcName, enemy, callAt);
    }


    @Override
    public void onEvent(DynamicObjectEvent event) {
        event.accept(this);
    }
}
