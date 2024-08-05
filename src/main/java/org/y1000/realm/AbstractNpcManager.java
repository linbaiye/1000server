package org.y1000.realm;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.creatures.event.*;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.creatures.npc.NpcFactory;
import org.y1000.event.EntityEvent;
import org.y1000.event.EntityEventListener;
import org.y1000.sdb.CreateEntitySdbRepository;
import org.y1000.sdb.CreateNpcSdb;
import org.y1000.sdb.MonstersSdb;
import org.y1000.sdb.NpcSpawnSetting;
import org.y1000.util.Coordinate;

import java.util.*;

public abstract class AbstractNpcManager extends AbstractActiveEntityManager<Npc> implements EntityEventListener {

    private final EntityEventSender sender;

    private final EntityIdGenerator idGenerator;

    private final NpcFactory npcFactory;

    private final ProjectileManager projectileManager;

    private final GroundItemManager itemManager;

    private final CreateEntitySdbRepository createEntitySdbRepository;

    private final MonstersSdb monstersSdb;

    private final Map<Npc, Set<Npc>> cloned;

    public AbstractNpcManager(EntityEventSender sender,
                              EntityIdGenerator idGenerator,
                              NpcFactory npcFactory,
                              GroundItemManager itemManager,
                              CreateEntitySdbRepository createEntitySdbRepository,
                              MonstersSdb monstersSdb) {
        Validate.notNull(sender);
        Validate.notNull(idGenerator);
        Validate.notNull(itemManager);
        Validate.notNull(createEntitySdbRepository);
        this.sender = sender;
        this.idGenerator = idGenerator;
        this.npcFactory = npcFactory;
        this.itemManager = itemManager;
        this.monstersSdb = monstersSdb;
        projectileManager = new ProjectileManager();
        this.createEntitySdbRepository = createEntitySdbRepository;
        cloned = new HashMap<>();
    }

    NpcFactory npcFactory() {
        return npcFactory;
    }

    void spawnNPCs(CreateNpcSdb createNpcSdb, RealmMap map) {
        List<NpcSpawnSetting> allSettings = createNpcSdb.getAllSettings();
        for (NpcSpawnSetting setting : allSettings) {
            var name = setting.idName();
            for (int i = 0; i < setting.number(); i++) {
                try {
                    Optional<Coordinate> random = setting.range().random(map::movable);
                    random.ifPresentOrElse(p -> addNpc(npcFactory.createNpc(name, idGenerator.next(), map, p)),
                            () -> log().warn("Not able to spawn monster {} within range {} on map {}..", name, setting.range(), map.mapFile()));
                } catch (Exception e) {
                    log().error("Failed to create npc {}.", name, e);
                }
            }
        }
    }

    Optional<CreateNpcSdb> createMonsterSdb(int realmId) {
        return createEntitySdbRepository.monsterSdbExists(realmId) ?
            Optional.of(createEntitySdbRepository.loadMonster(realmId)) : Optional.empty();
    }

    Optional<CreateNpcSdb> createNpcSdb(int realmId) {
        return createEntitySdbRepository.npcSdbExists(realmId) ?
            Optional.of(createEntitySdbRepository.loadNpc(realmId)) : Optional.empty();
    }


    @Override
    public void update(long delta) {
        updateManagedEntities(delta);
        projectileManager.update(delta);
    }


    protected void removeNpc(Npc npc) {
        sender.remove(npc);
        npc.deregisterEventListener(this);
        remove(npc);
    }

    protected void addNpc(Npc npc) {
        sender.add(npc);
        sender.notifyVisiblePlayers(npc, new NpcJoinedEvent(npc));
        npc.registerEventListener(this);
        npc.start();
        add(npc);
    }


    private void handleDieEvent(CreatureDieEvent event) {
        if (!(event.source() instanceof Npc npc)) {
            return;
        }
        var dropItems = monstersSdb.getHaveItem(npc.idName());
        if (!StringUtils.isEmpty(dropItems)) {
            itemManager.dropItem(dropItems, event.source().coordinate());
        }
        if (cloned.containsKey(npc)) {
            cloned.get(npc).forEach(Npc::die);
            cloned.remove(npc);
        }
    }

    abstract void onUnhandledEvent(EntityEvent entityEvent) ;

    private void handleShiftEvent(NpcShiftEvent shiftEvent) {
        Npc npc = shiftEvent.npc();
        sender.notifyVisiblePlayers(npc, shiftEvent.createRemoveEvent());
        removeNpc(npc);
        Npc newNpc = npcFactory.createNpc(shiftEvent.shiftToName(), idGenerator.next(), npc.realmMap(), npc.coordinate());
        addNpc(newNpc);
    }

    private void handleCloneEvent(NpcCastCloneEvent event) {
        var set =  new HashSet<Npc>();
        for (int i = 0; i < event.number(); i++) {
            var newNpc = npcFactory.createClonedNpc(event.npc(), idGenerator.next());
            addNpc(newNpc);
            set.add(newNpc);
        }
        cloned.put(event.npc(), set);
    }

    @Override
    public void onEvent(EntityEvent entityEvent) {
        if (entityEvent instanceof MonsterShootEvent shootEvent) {
            projectileManager.add(shootEvent.projectile());
            sender.notifyVisiblePlayers(shootEvent.source(), shootEvent);
        } else if (entityEvent instanceof CreatureDieEvent dieEvent) {
            handleDieEvent(dieEvent);
        } else if (entityEvent instanceof NpcShiftEvent shiftEvent) {
            handleShiftEvent(shiftEvent);
        } else if (entityEvent instanceof NpcCastCloneEvent cloneEvent) {
            handleCloneEvent(cloneEvent);
        } else {
            onUnhandledEvent(entityEvent);
        }
    }
}
