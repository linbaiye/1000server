package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.creatures.event.NpcShiftEvent;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.creatures.npc.NpcFactory;
import org.y1000.event.EntityEvent;
import org.y1000.message.I2ClientMessage;
import org.y1000.sdb.CreateNonMonsterSdb;
import org.y1000.sdb.CreateNpcSdb;
import org.y1000.sdb.HaveItemSdb;
import org.y1000.sdb.MonstersSdb;

@Slf4j
final class DungeonNpcManager extends AbstractNpcManager {

    public DungeonNpcManager(MessageSender sender,
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
    }

    @Override
    protected Logger log() {
        return log;
    }


    @Override
    void onUnhandledEvent(EntityEvent entityEvent) {
        if (entityEvent instanceof RemoveEntityEvent removeEntityEvent) {
            //handleRemoveEvent(removeEntityEvent);
        } else if (entityEvent instanceof NpcShiftEvent shiftEvent) {
//            replaceNpc(shiftEvent);
        }
    }

    @Override
    public void onRemove(Npc npc, I2ClientMessage message) {
        syncAndRemove(npc ,message);
    }

    private void initializeNPCs(CreateNpcSdb sdb) {
        sdb.getAllSettings().forEach(this::spawnNPCs);
    }

    @Override
    public void init() {
        createMonsterSdb().ifPresent(this::initializeNPCs);
        createNpcSdb().ifPresent(this::initializeNPCs);
    }

    @Override
    public void update(long delta) {
        doUpdateEntities(delta);
    }

}
