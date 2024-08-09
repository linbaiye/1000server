package org.y1000.realm;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.creatures.npc.NpcFactory;
import org.y1000.entities.objects.DynamicObjectFactory;
import org.y1000.item.ItemFactory;
import org.y1000.item.ItemSdb;
import org.y1000.sdb.CreateEntitySdbRepository;
import org.y1000.sdb.CreateGateSdb;
import org.y1000.sdb.MapSdb;
import org.y1000.sdb.MonstersSdb;

@Slf4j
public final class RealmFactoryImpl implements RealmFactory {
    private final ItemFactory itemFactory;
    private final NpcFactory npcFactory;
    private final ItemSdb itemSdb;
    private final MonstersSdb monstersSdb;
    private final MapSdb mapSdb;
    private final CreateEntitySdbRepository createEntitySdbRepository;
    private final DynamicObjectFactory dynamicObjectFactory;
    private final CreateGateSdb createGateSdb;

    @Builder
    public RealmFactoryImpl(ItemFactory itemFactory,
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
                                                EntityEventSender entityEventSender,
                                                RealmMap realmMap) {
        if (!createEntitySdbRepository.monsterSdbExists(id) && !createEntitySdbRepository.npcSdbExists(id)) {
            return null;
        }
        var monsterSdb = createEntitySdbRepository.monsterSdbExists(id) ? createEntitySdbRepository.loadMonster(id) : null;
        var npcSdb = createEntitySdbRepository.npcSdbExists(id) ? createEntitySdbRepository.loadNpc(id) : null;
        return mapSdb.getRegenInterval(id).isPresent() ?
                new DungeonNpcManager(entityEventSender, idGenerator,  npcFactory, itemManager, monstersSdb, aoiManager,  monsterSdb, npcSdb, realmMap) :
                new NpcManager(entityEventSender, idGenerator,  npcFactory, itemManager, monstersSdb, aoiManager,  monsterSdb, npcSdb, realmMap);
    }


    @Override
    public Realm createRealm(int id,
                             CrossRealmEventHandler crossRealmEventHandler) {
        Validate.notNull(crossRealmEventHandler);
        var realmMap = RealmMap.Load(mapSdb.getMapName(id), mapSdb.getTilName(id), mapSdb.getObjName(id), mapSdb.getRofName(id))
                .orElseThrow(() -> new IllegalArgumentException("No map for " + id));
        var entityIdGenerator = new EntityIdGenerator();
        AOIManager aoiManager = new RelevantScopeManager();
        //AOIManager aoiManager = new GridAOIManager(realmMap.width(), realmMap.height());
        var eventSender = new RealmEntityEventSender(aoiManager);
        var itemManager = new ItemManagerImpl(eventSender, itemSdb, entityIdGenerator, itemFactory);
        var npcManager = createNpcManager(id, aoiManager, entityIdGenerator, itemManager, eventSender, realmMap);
        var dynamicObjectManager = !createEntitySdbRepository.objectSdbExists(id) ? null :
                new DynamicObjectManagerImpl(dynamicObjectFactory, entityIdGenerator, eventSender, itemManager, createEntitySdbRepository.loadObject(id));
        var playerManager = new PlayerManagerImpl(eventSender, itemManager, itemFactory, dynamicObjectManager);
        var teleportManager = new TeleportManager(createGateSdb, entityIdGenerator);
        var builder = RealmBuilder.builder()
                .id(id)
                .crossRealmEventHandler(crossRealmEventHandler)
                .dynamicObjectManager(dynamicObjectManager)
                .eventSender(eventSender)
                .itemManager(itemManager)
                .mapSdb(mapSdb)
                .npcManager(npcManager)
                .playerManager(playerManager)
                .realmMap(realmMap)
                .teleportManager(teleportManager);
        return mapSdb.getRegenInterval(id)
                .map(builder::buildDungeon)
                .orElseGet(builder::buildNormal);
    }

    @Slf4j
    private static final class RealmBuilder {
        private RealmMap realmMap;
        private RealmEntityEventSender eventSender;
        private ItemManagerImpl itemManager;
        private AbstractNpcManager npcManager;
        private PlayerManagerImpl playerManager;
        private DynamicObjectManager dynamicObjectManager;
        private TeleportManager teleportManager;
        private int id;
        private CrossRealmEventHandler crossRealmEventHandler;
        private MapSdb mapSdb;

        private RealmBuilder() {
        }

        public static RealmBuilder builder() {
            return new RealmBuilder();
        }

        public RealmBuilder realmMap(RealmMap realmMap) {
            this.realmMap = realmMap;
            return this;
        }

        public RealmBuilder eventSender(RealmEntityEventSender eventSender) {
            this.eventSender = eventSender;
            return this;
        }

        public RealmBuilder itemManager(ItemManagerImpl itemManager) {
            this.itemManager = itemManager;
            return this;
        }

        public RealmBuilder npcManager(AbstractNpcManager npcManager) {
            this.npcManager = npcManager;
            return this;
        }

        public RealmBuilder playerManager(PlayerManagerImpl playerManager) {
            this.playerManager = playerManager;
            return this;
        }

        public RealmBuilder dynamicObjectManager(DynamicObjectManager dynamicObjectManager) {
            this.dynamicObjectManager = dynamicObjectManager;
            return this;
        }

        public RealmBuilder teleportManager(TeleportManager teleportManager) {
            this.teleportManager = teleportManager;
            return this;
        }

        public RealmBuilder id(int id) {
            this.id = id;
            return this;
        }

        public RealmBuilder crossRealmEventHandler(CrossRealmEventHandler crossRealmEventHandler) {
            this.crossRealmEventHandler = crossRealmEventHandler;
            return this;
        }

        public RealmBuilder mapSdb(MapSdb mapSdb) {
            this.mapSdb = mapSdb;
            return this;
        }

        public Realm buildNormal() {
            log.debug("Creating normal realm {}.", id);
            return new RealmImpl(id, realmMap, eventSender, itemManager, npcManager, playerManager, dynamicObjectManager, teleportManager, crossRealmEventHandler, mapSdb);
        }

        public Realm buildDungeon(int interval) {
            log.debug("Creating dungeon realm {}.", id);
            return new DungeonRealm(id, realmMap, eventSender, itemManager, npcManager, playerManager, dynamicObjectManager, teleportManager, crossRealmEventHandler, mapSdb, interval);
        }
    }
}
