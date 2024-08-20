package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.creatures.event.NpcShiftEvent;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.creatures.npc.NpcFactory;
import org.y1000.event.EntityEvent;
import org.y1000.sdb.CreateNpcSdb;
import org.y1000.sdb.MonstersSdb;

@Slf4j
final class DungeonNpcManager extends AbstractNpcManager {

    public DungeonNpcManager(EntityEventSender sender,
                             EntityIdGenerator idGenerator,
                             NpcFactory npcFactory,
                             GroundItemManager itemManager,
                             MonstersSdb monstersSdb,
                             AOIManager aoiManager,
                             CreateNpcSdb createMonsterSdb,
                             CreateNpcSdb createNpcSdb,
                             RealmMap realmMap) {
        super(sender, idGenerator, npcFactory, itemManager, monstersSdb, aoiManager, createMonsterSdb, createNpcSdb, realmMap);
    }

    @Override
    protected Logger log() {
        return log;
    }


    private void handleRemoveEvent(RemoveEntityEvent removeEntityEvent) {
        if (removeEntityEvent.source() instanceof Npc npc) {
            removeNpc(npc);
            removeFromCloned(npc);
            log.debug("Removed npc {}.", npc.id());
        }
    }


    @Override
    void onUnhandledEvent(EntityEvent entityEvent) {
        if (entityEvent instanceof RemoveEntityEvent removeEntityEvent) {
            handleRemoveEvent(removeEntityEvent);
        } else if (entityEvent instanceof NpcShiftEvent shiftEvent) {
            replaceNpc(shiftEvent);
        }
    }

    @Override
    public void init() {
        createMonsterSdb().ifPresent(this::spawnNPCs);
        createNpcSdb().ifPresent(this::spawnNPCs);
    }

    @Override
    public void update(long delta) {
        doUpdateEntities(delta);
    }
}
