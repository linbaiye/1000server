package org.y1000.realm;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.creatures.npc.NpcFactory;
import org.y1000.event.EntityEvent;
import org.y1000.sdb.CreateNpcSdb;
import org.y1000.sdb.MonstersSdb;
import org.y1000.sdb.NpcSpawnSetting;
import org.y1000.util.Coordinate;

import java.util.*;

@Slf4j
final class NpcManagerImpl extends AbstractNpcManager implements NpcManager {

    private final Map<String, List<NpcSpawnSetting>> npcSpawnSettings;

    private final RespawningEntityManager<Npc> respawningEntityManager;

    @Builder
    public NpcManagerImpl(EntityEventSender sender,
                          EntityIdGenerator idGenerator,
                          NpcFactory npcFactory,
                          GroundItemManager itemManager,
                          MonstersSdb monstersSdb,
                          AOIManager aoiManager,
                          CreateNpcSdb createMonsterSdb,
                          CreateNpcSdb createNpcSdb,
                          RealmMap realmMap) {
        super(sender, idGenerator, npcFactory, itemManager, monstersSdb, aoiManager, createMonsterSdb, createNpcSdb, realmMap);
        this.respawningEntityManager = new RespawningEntityManager<>();
        this.npcSpawnSettings = new HashMap<>();
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
            addNpc(npc);
            return;
        }
        log.error("Failed to respawn {}.", npc.id());
    }

    @Override
    protected Logger log() {
        return log;
    }

    private void updateRespawning(long delta) {
        Set<Npc> respawningNpcs = respawningEntityManager.update(delta);
        respawningNpcs.forEach(this::respawn);
    }

    private void init(CreateNpcSdb createNpcSdb) {
        spawnNPCs(createNpcSdb);
        for (NpcSpawnSetting setting: createNpcSdb.getAllSettings()) {
            npcSpawnSettings.put(setting.idName(), createNpcSdb.getSettings(setting.idName()));
        }
    }


    @Override
    public void update(long delta) {
        doUpdateEntities(delta);
        updateRespawning(delta);
    }

    private void handleRemoveEvent(RemoveEntityEvent removeEntityEvent) {
        if (removeEntityEvent.source() instanceof Npc npc) {
            removeNpc(npc);
            if (!isCloned(npc)) {
                respawningEntityManager.add(npc, 8000);
            } else {
                removeFromCloned(npc);
            }
        }
    }

    @Override
    void onUnhandledEvent(EntityEvent entityEvent) {
        if (entityEvent instanceof RemoveEntityEvent removeEntityEvent) {
            handleRemoveEvent(removeEntityEvent);
        }
    }

    @Override
    public void init() {
        createMonsterSdb().ifPresent(this::init);
        createNpcSdb().ifPresent(this::init);
    }
}
