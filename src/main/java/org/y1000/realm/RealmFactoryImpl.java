package org.y1000.realm;

import jakarta.persistence.EntityManagerFactory;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.creatures.npc.NpcFactory;
import org.y1000.entities.objects.DynamicObjectFactory;
import org.y1000.item.ItemFactory;
import org.y1000.item.ItemSdb;
import org.y1000.repository.BankRepositoryImpl;
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

    private final EntityManagerFactory entityManagerFactory;

    @Builder
    public RealmFactoryImpl(ItemFactory itemFactory,
                            NpcFactory npcFactory,
                            ItemSdb itemSdb,
                            MonstersSdb monstersSdb,
                            MapSdb mapSdb,
                            CreateEntitySdbRepository createEntitySdbRepository,
                            DynamicObjectFactory dynamicObjectFactory,
                            CreateGateSdb createGateSdb,
                            EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
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

    private NpcManager createNpcManager(int id,
                                                AOIManager aoiManager,
                                                EntityIdGenerator idGenerator,
                                                GroundItemManager itemManager,
                                                EntityEventSender entityEventSender,
                                                RealmMap realmMap) {
        if (!createEntitySdbRepository.monsterSdbExists(id) && !createEntitySdbRepository.npcSdbExists(id)) {
            return NpcManager.EMPTY;
        }
        var monsterSdb = createEntitySdbRepository.monsterSdbExists(id) ? createEntitySdbRepository.loadMonster(id) : null;
        var npcSdb = createEntitySdbRepository.npcSdbExists(id) ? createEntitySdbRepository.loadNpc(id) : null;
        return mapSdb.getRegenInterval(id).isPresent() ?
                new DungeonNpcManager(entityEventSender, idGenerator,  npcFactory, itemManager, monstersSdb, aoiManager,  monsterSdb, npcSdb, realmMap) :
                new NpcManagerImpl(entityEventSender, idGenerator,  npcFactory, itemManager, monstersSdb, aoiManager,  monsterSdb, npcSdb, realmMap);
    }


    @Override
    public Realm createRealm(int id,
                             CrossRealmEventSender crossRealmEventSender) {
        try {
            Validate.notNull(crossRealmEventSender);
            var realmMap = RealmMap.Load(mapSdb.getMapName(id), mapSdb.getTilName(id), mapSdb.getObjName(id), mapSdb.getRofName(id))
                    .orElseThrow(() -> new IllegalArgumentException("No map for " + id));
            var entityIdGenerator = new EntityIdGenerator();
            AOIManager aoiManager = new RelevantScopeManager();
            var eventSender = new RealmEntityEventSender(aoiManager);
            var itemManager = new ItemManagerImpl(eventSender, itemSdb, entityIdGenerator, itemFactory);
            var npcManager = createNpcManager(id, aoiManager, entityIdGenerator, itemManager, eventSender, realmMap);
            var dynamicObjectManager = !createEntitySdbRepository.objectSdbExists(id) ? DynamicObjectManager.EMPTY:
                    new DynamicObjectManagerImpl(dynamicObjectFactory, entityIdGenerator, eventSender, itemManager, createEntitySdbRepository.loadObject(id), crossRealmEventSender, realmMap);
            var playerManager = new PlayerManagerImpl(eventSender, itemManager, itemFactory, dynamicObjectManager, new BankManagerImpl(eventSender, npcManager, new BankRepositoryImpl()));
            var teleportManager = new TeleportManager(id, realmMap, createGateSdb, entityIdGenerator, aoiManager);
            var builder = RealmBuilder.builder()
                    .id(id)
                    .crossRealmEventHandler(crossRealmEventSender)
                    .dynamicObjectManager(dynamicObjectManager)
                    .eventSender(eventSender)
                    .itemManager(itemManager)
                    .mapSdb(mapSdb)
                    .npcManager(npcManager)
                    .playerManager(playerManager)
                    .realmMap(realmMap)
                    .teleportManager(teleportManager)
                    .chatManager(new ChatManagerImpl(playerManager, eventSender, crossRealmEventSender))
                    ;
            return mapSdb.getRegenInterval(id)
                    .map(builder::buildDungeon)
                    .orElseGet(builder::buildNormal);
        } catch (RuntimeException e) {
            log.error("Failed to init realm {}.", id, e);
            throw e;
        }
    }

    @Slf4j
    private static final class RealmBuilder {
        private RealmMap realmMap;
        private RealmEntityEventSender eventSender;
        private ItemManagerImpl itemManager;
        private NpcManager npcManager;
        private PlayerManagerImpl playerManager;
        private DynamicObjectManager dynamicObjectManager;
        private TeleportManager teleportManager;
        private int id;
        private CrossRealmEventSender crossRealmEventSender;
        private MapSdb mapSdb;

        private ChatManager chatManager;

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

        public RealmBuilder chatManager(ChatManager chatManager) {
            this.chatManager = chatManager;
            return this;
        }


        public RealmBuilder itemManager(ItemManagerImpl itemManager) {
            this.itemManager = itemManager;
            return this;
        }

        public RealmBuilder npcManager(NpcManager npcManager) {
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

        public RealmBuilder crossRealmEventHandler(CrossRealmEventSender crossRealmEventSender) {
            this.crossRealmEventSender = crossRealmEventSender;
            return this;
        }

        public RealmBuilder mapSdb(MapSdb mapSdb) {
            this.mapSdb = mapSdb;
            return this;
        }

        public Realm buildNormal() {
            log.debug("Creating normal realm {}.", id);
            return new RealmImpl(id, realmMap, eventSender, itemManager, npcManager, playerManager, dynamicObjectManager, teleportManager, crossRealmEventSender, mapSdb, chatManager);
        }

        public Realm buildDungeon(int interval) {
            log.debug("Creating dungeon realm {}.", id);
            return new DungeonRealm(id, realmMap, eventSender, itemManager, npcManager, playerManager, dynamicObjectManager, teleportManager, crossRealmEventSender, mapSdb, interval, chatManager);
        }
    }
}
