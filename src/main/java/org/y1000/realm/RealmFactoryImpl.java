package org.y1000.realm;

import lombok.Builder;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.creatures.npc.NpcFactory;
import org.y1000.entities.objects.DynamicObjectFactory;
import org.y1000.item.ItemFactory;
import org.y1000.item.ItemSdb;
import org.y1000.repository.ItemRepository;
import org.y1000.sdb.CreateEntitySdbRepository;
import org.y1000.sdb.CreateGateSdb;
import org.y1000.sdb.MapSdb;
import org.y1000.sdb.MonstersSdb;

public final class RealmFactoryImpl implements RealmFactory {
    private final ItemFactory itemFactory;
    private final ItemRepository itemRepository;
    private final NpcFactory npcFactory;
    private final ItemSdb itemSdb;
    private final MonstersSdb monstersSdb;
    private final MapSdb mapSdb;
    private final CreateEntitySdbRepository createEntitySdbRepository;
    private final DynamicObjectFactory dynamicObjectFactory;
    private final CreateGateSdb createGateSdb;

    @Builder
    public RealmFactoryImpl(ItemFactory itemFactory,
                            ItemRepository itemRepository,
                            NpcFactory npcFactory,
                            ItemSdb itemSdb,
                            MonstersSdb monstersSdb,
                            MapSdb mapSdb,
                            CreateEntitySdbRepository createEntitySdbRepository,
                            DynamicObjectFactory dynamicObjectFactory,
                            CreateGateSdb createGateSdb) {
        Validate.notNull(itemFactory);
        Validate.notNull(npcFactory);
        Validate.notNull(itemSdb);
        Validate.notNull(monstersSdb);
        Validate.notNull(mapSdb);
        Validate.notNull(createEntitySdbRepository);
        Validate.notNull(dynamicObjectFactory);
        Validate.notNull(createGateSdb);
        this.itemFactory = itemFactory;
        this.itemRepository = itemRepository;
        this.npcFactory = npcFactory;
        this.itemSdb = itemSdb;
        this.monstersSdb = monstersSdb;
        this.mapSdb = mapSdb;
        this.createEntitySdbRepository = createEntitySdbRepository;
        this.dynamicObjectFactory = dynamicObjectFactory;
        this.createGateSdb = createGateSdb;
    }

    private AbstractNpcManager createNpcManager(int id,
                                                AOIManager aoiManager,
                                                EntityIdGenerator idGenerator,
                                                GroundItemManager itemManager,
                                                RealmMap realmMap) {
        if (!createEntitySdbRepository.monsterSdbExists(id) && !createEntitySdbRepository.npcSdbExists(id)) {
            return null;
        }
        var monsterSdb = createEntitySdbRepository.monsterSdbExists(id) ? createEntitySdbRepository.loadMonster(id) : null;
        var npcSdb = createEntitySdbRepository.npcSdbExists(id) ? createEntitySdbRepository.loadNpc(id) : null;
        return mapSdb.getRegenInterval(id).isPresent() ?
                new DungeonNpcManager(new RealmEntityEventSender(aoiManager), idGenerator,  npcFactory, itemManager, monstersSdb, aoiManager,  monsterSdb, npcSdb, realmMap) :
                new NpcManager(new RealmEntityEventSender(aoiManager), idGenerator,  npcFactory, itemManager, monstersSdb, aoiManager,  monsterSdb, npcSdb, realmMap);
    }


    @Override
    public Realm createRealm(int id) {
        var realmMap = RealmMap.Load()
        var entityIdGenerator = new EntityIdGenerator();
        AOIManager aoiManager = new RelevantScopeManager();
        var eventSender = new RealmEntityEventSender(aoiManager);
        var itemManager = new ItemManagerImpl(eventSender, itemSdb, entityIdGenerator, itemFactory);
        var npcManager = createNpcManager(id, aoiManager, entityIdGenerator, itemManager);
        var dynamicObjectManager = !createEntitySdbRepository.objectSdbExists(id) ?  DynamicObjectManager.EMPTY :
                new DynamicObjectManagerImpl(dynamicObjectFactory, entityIdGenerator, eventSender, itemManager, createEntitySdbRepository.loadObject(id));
        var playerManager = new PlayerManager(eventSender, itemManager, itemFactory, dynamicObjectManager);
        var teleportManager = new TeleportManager(createGateSdb, entityIdGenerator);
        return null;
    }
}
