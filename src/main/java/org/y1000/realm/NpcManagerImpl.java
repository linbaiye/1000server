package org.y1000.realm;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.creatures.event.NpcShiftEvent;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.creatures.npc.NpcFactory;
import org.y1000.event.EntityEvent;
import org.y1000.sdb.CreateNpcSdb;
import org.y1000.sdb.MonstersSdb;
import org.y1000.sdb.NpcSpawnSetting;
import org.y1000.util.Coordinate;
import org.y1000.util.Rectangle;

import java.util.*;

@Slf4j
final class NpcManagerImpl extends AbstractNpcManager implements NpcManager {

    private final Map<String, List<NpcSpawnSetting>> npcSpawnSettings;

    private final EntityTimerManager<Npc> respawningEntityManager;

    private boolean initialized = false;

    private final Map<Npc, Npc> shiftedNpcs;
    private static final int RESPAWN_MILLIS = 8000;

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
        this.respawningEntityManager = new EntityTimerManager<>();
        this.npcSpawnSettings = new HashMap<>();
        this.shiftedNpcs = new HashMap<>();
    }

    private void respawn(Npc npc) {
        List<NpcSpawnSetting> settings = npcSpawnSettings.getOrDefault(npc.idName(), Collections.emptyList());
        for (NpcSpawnSetting setting : settings) {
            Rectangle range = setting.range();
            if (!range.contains(npc.spawnCoordinate())) {
                continue;
            }
            Coordinate coordinate = range
                    .random(npc.realmMap()::movable)
                    .or(() -> range.findFirst(npc.realmMap()::movable))
                    .orElse(npc.spawnCoordinate());
            var newNpc = getNpcFactory().createNpc(npc.idName(), getIdGenerator().next(), npc.realmMap(), coordinate);
            addNpc(newNpc);
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
            if (isCloned(npc)) {
                removeFromCloned(npc);
                return;
            }
            Npc shiftFrom = shiftedNpcs.remove(npc);
            respawningEntityManager.add(Objects.requireNonNullElse(shiftFrom, npc),
                    RESPAWN_MILLIS);
        }
    }

    private void handleShiftEvent(NpcShiftEvent shiftEvent) {
        if (shiftedNpcs.containsKey(shiftEvent.npc()))
            return;
        Npc newNpc = replaceNpc(shiftEvent);
        shiftedNpcs.put(newNpc, shiftEvent.npc());
    }

    @Override
    void onUnhandledEvent(EntityEvent entityEvent) {
        if (entityEvent instanceof RemoveEntityEvent removeEntityEvent) {
            handleRemoveEvent(removeEntityEvent);
        } else if (entityEvent instanceof NpcShiftEvent shiftEvent) {
            handleShiftEvent(shiftEvent);
        }
    }

    @Override
    public void init() {
        if (initialized)
            throw new IllegalStateException();
        createMonsterSdb().ifPresent(this::init);
        createNpcSdb().ifPresent(this::init);
        initialized = true;
    }
}
