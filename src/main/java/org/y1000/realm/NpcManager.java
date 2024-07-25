package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.creatures.event.NpcJoinedEvent;
import org.y1000.entities.creatures.event.MonsterShootEvent;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.creatures.npc.NpcFactory;
import org.y1000.event.EntityEvent;
import org.y1000.event.EntityEventListener;
import org.y1000.sdb.CreateNpcSdb;
import org.y1000.sdb.CreateEntitySdbRepository;
import org.y1000.sdb.NpcSpawnSetting;
import org.y1000.util.Coordinate;

import java.util.*;

@Slf4j
final class NpcManager extends AbstractEntityManager<Npc> implements EntityEventListener {

    private final EntityEventSender sender;

    private final EntityIdGenerator idGenerator;

    private final NpcFactory npcFactory;

    private final Map<String, List<NpcSpawnSetting>> npcSpawnSettings;

    private final RespawningEntityManager<Npc> respawningEntityManager;

    private final ProjectileManager projectileManager;

    private final GroundItemManager itemManager;

    private final CreateEntitySdbRepository createEntitySdbRepository;


    public NpcManager(EntityEventSender sender,
                      EntityIdGenerator idGenerator,
                      NpcFactory npcFactory,
                      GroundItemManager itemManager,
                      CreateEntitySdbRepository createEntitySdbRepository) {
        Validate.notNull(sender);
        Validate.notNull(idGenerator);
        Validate.notNull(itemManager);
        Validate.notNull(createEntitySdbRepository);
        this.sender = sender;
        this.idGenerator = idGenerator;
        this.npcFactory = npcFactory;
        this.itemManager = itemManager;
        this.npcSpawnSettings = new HashMap<>();
        respawningEntityManager = new RespawningEntityManager<>();
        projectileManager = new ProjectileManager();
        this.createEntitySdbRepository = createEntitySdbRepository;
    }

    private void spawnMonsters(String name, RealmMap map, NpcSpawnSetting setting) {
        for (int i = 0; i < setting.number(); i++) {
            try {
                Optional<Coordinate> random = setting.range().random(map::movable);
                random.ifPresentOrElse(p -> add(npcFactory.createNpc(name, idGenerator.next(), map, p)),
                        () -> log.warn("Not able to spawn monster {} within range {} on map {}..", name, setting.range(), map.name()));
            } catch (Exception e) {
                log.error("Failed to create npc {}.", name, e);
            }
        }
    }

    private void respawn(Npc npc) {
        List<NpcSpawnSetting> settings = npcSpawnSettings.getOrDefault(npc.idName(), Collections.emptyList());
        for (NpcSpawnSetting setting : settings) {
            if (!setting.range().contains(npc.spawnCoordinate())) {
                continue;
            }
            Coordinate coordinate = setting.range()
                    .random(npc.realmMap()::movable)
                    .orElse(npc.spawnCoordinate());
            npc.respawn(coordinate);
            add(npc);
            return;
        }
        log.error("Failed to respawn {}.", npc.id());
    }

    private void spawn(CreateNpcSdb createNpcSdb, RealmMap realmMap) {
        List<NpcSpawnSetting> allSettings = createNpcSdb.getAllSettings();
        for (NpcSpawnSetting setting : allSettings) {
            npcSpawnSettings.put(setting.idName(), createNpcSdb.getSettings(setting.idName()));
            spawnMonsters(setting.idName(), realmMap, setting);
        }
    }

    public void init(RealmMap realmMap, int realmId) {
        if (createEntitySdbRepository.monsterSdbExists(realmId)) {
            spawn(createEntitySdbRepository.loadMonster(realmId), realmMap);
        }
        if (createEntitySdbRepository.npcSdbExists(realmId)) {
            spawn(createEntitySdbRepository.loadNpc(realmId), realmMap);
        }
    }

    @Override
    protected Logger log() {
        return log;
    }

    private void updateRespawning(long delta) {
        Set<Npc> respawningNpcs = respawningEntityManager.update(delta);
        respawningNpcs.forEach(this::respawn);
    }

    @Override
    public void update(long delta) {
        updateManagedEntities(delta);
        updateRespawning(delta);
        projectileManager.update(delta);
    }

    @Override
    protected void onAdded(Npc entity) {
        sender.add(entity);
        sender.notifyVisiblePlayers(entity, new NpcJoinedEvent(entity));
        entity.registerEventListener(this);
        entity.registerEventListener(itemManager);
        entity.start();
    }

    @Override
    protected void onDeleted(Npc entity) {
        sender.remove(entity);
        respawningEntityManager.add(entity, 8000);
        entity.deregisterEventListener(this);
        entity.deregisterEventListener(itemManager);
    }

    private void onRemoveEntity(RemoveEntityEvent removeEntityEvent) {
        if (!(removeEntityEvent.source() instanceof Npc npc)) {
            return;
        }
        delete(npc);
    }

    @Override
    public void onEvent(EntityEvent entityEvent) {
        if (entityEvent instanceof RemoveEntityEvent removeEntityEvent) {
            onRemoveEntity(removeEntityEvent);
        } else if (entityEvent instanceof MonsterShootEvent shootEvent) {
            projectileManager.add(shootEvent.projectile());
            sender.notifyVisiblePlayers(shootEvent.source(), shootEvent);
        }
    }
}
