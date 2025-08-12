package org.y1000.realm;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.creatures.npc.NpcFactory;
import org.y1000.entities.creatures.npc.NpcRespawnAbility;
import org.y1000.network.I2ClientMessage;
import org.y1000.sdb.*;

import java.util.*;

@Slf4j
final class NpcManagerImpl extends AbstractNpcManager implements NpcManager {

    private final Map<Long, NpcSpawnSetting> npcSpawnSettings;
    private final EntityTimerManager<Npc> respawningEntityManager;

    private boolean initialized = false;

    @Builder
    public NpcManagerImpl(MessageSender sender,
                          EntityIdGenerator idGenerator,
                          NpcFactory npcFactory,
                          GroundItemManager itemManager,
                          MonstersSdb monstersSdb,
                          AOIManager aoiManager,
                          CreateNpcSdb createMonsterSdb,
                          CreateNonMonsterSdb createNpcSdb,
                          RealmMap realmMap,
                          HaveItemSdb haveItemSdb) {
        super(sender, idGenerator, npcFactory, itemManager, monstersSdb, aoiManager, createMonsterSdb, createNpcSdb, realmMap, haveItemSdb);
        this.respawningEntityManager = new EntityTimerManager<>();
        this.npcSpawnSettings = new HashMap<>();
    }

    private void respawn(Npc npc) {
        NpcSpawnSetting npcSpawnSetting = npcSpawnSettings.remove(npc.id());
        if (npcSpawnSetting == null) {
            return;
        }
        Npc newNpc = spawnNpc(npc.getIdName(), npcSpawnSetting.range());
        npcSpawnSettings.put(newNpc.id(), npcSpawnSetting);
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
        for (NpcSpawnSetting setting: createNpcSdb.getAllSettings()) {
            var ids = spawnNPCs(setting);
            ids.forEach(id -> npcSpawnSettings.put(id, setting));
        }
        log().debug("Created {} NPCs.", getEntities().size());
    }


    @Override
    public void update(long delta) {
        doUpdateEntities(delta);
        updateRespawning(delta);
    }

    public void onRemove(Npc source, I2ClientMessage message) {
        syncAndRemove(source, message);
        source.findAbility(NpcRespawnAbility.class)
                .ifPresent(a -> respawningEntityManager.add(source, a.respawnMillis()));
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
