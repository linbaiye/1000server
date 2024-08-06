package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.creatures.npc.NpcFactory;
import org.y1000.event.EntityEvent;
import org.y1000.sdb.CreateEntitySdbRepository;
import org.y1000.sdb.MonstersSdb;

@Slf4j
final class DungeonNpcManager extends AbstractNpcManager {

    public DungeonNpcManager(EntityEventSender sender,
                             EntityIdGenerator idGenerator,
                             NpcFactory npcFactory,
                             GroundItemManager itemManager,
                             CreateEntitySdbRepository createEntitySdbRepository,
                             MonstersSdb monstersSdb,
                             AOIManager aoiManager) {
        super(sender, idGenerator, npcFactory, itemManager, createEntitySdbRepository, monstersSdb, aoiManager);
    }


    @Override
    protected Logger log() {
        return log;
    }


    @Override
    public void update(long delta) {
        super.update(delta);
    }


    private void handleRemoveEvent(RemoveEntityEvent removeEntityEvent) {
        if (removeEntityEvent.source() instanceof Npc npc) {
            removeNpc(npc);
            removeFromCloned(npc);
        }
    }


    @Override
    void onUnhandledEvent(EntityEvent entityEvent) {
        if (entityEvent instanceof RemoveEntityEvent removeEntityEvent) {
            handleRemoveEvent(removeEntityEvent);
        }
    }
}
