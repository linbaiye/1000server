package org.y1000.realm;

import jakarta.persistence.EntityManagerFactory;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.creatures.npc.NpcFactory;
import org.y1000.entities.objects.DynamicObjectFactory;
import org.y1000.item.ItemFactory;
import org.y1000.item.ItemSdb;
import org.y1000.kungfu.KungFuSdb;
import org.y1000.repository.*;
import org.y1000.sdb.*;
import org.y1000.util.Coordinate;

import java.util.*;

@Slf4j
public final class RealmFactoryImpl implements RealmFactory {
    private final ItemFactory itemFactory;
    private final NpcFactory npcFactory;
    private final ItemSdb itemSdb;
    private final MonstersSdb monstersSdb;
    private final MapSdb mapSdb;
    private final RealmSpecificSdbRepository realmSpecificSdbRepository;
    private final DynamicObjectFactory dynamicObjectFactory;
    private final CreateGateSdb createGateSdb;
    private final PlayerRepository playerRepository;

    private final BankRepository bankRepository;

    private final EntityManagerFactory entityManagerFactory;

    private final PosByDieSdb posByDieSdb;

    private final GuildRepository guildRepository;

    private final ItemRepository itemRepository;

    private final KungFuBookRepository kungFuBookRepository;

    private static final Set<Integer> CONJUNCTION_IDS = Set.of(4, 20);

    private static final Map<Integer, Set<Integer>> DUNGEON_WHITELIST_IDS = Map.of(19, Set.of(20), 3, Set.of(4));

    @Builder
    public RealmFactoryImpl(ItemFactory itemFactory,
                            NpcFactory npcFactory,
                            ItemSdb itemSdb,
                            MonstersSdb monstersSdb,
                            MapSdb mapSdb,
                            RealmSpecificSdbRepository realmSpecificSdbRepository,
                            DynamicObjectFactory dynamicObjectFactory,
                            CreateGateSdb createGateSdb,
                            EntityManagerFactory entityManagerFactory,
                            PlayerRepository playerRepository,
                            BankRepository bankRepository,
                            PosByDieSdb posByDieSdb,
                            GuildRepository guildRepository,
                            ItemRepository itemRepository,
                            KungFuBookRepository kungFuBookRepository) {
        Validate.notNull(itemFactory);
        Validate.notNull(npcFactory);
        Validate.notNull(itemSdb);
        Validate.notNull(monstersSdb);
        Validate.notNull(mapSdb);
        Validate.notNull(realmSpecificSdbRepository);
        Validate.notNull(dynamicObjectFactory);
        Validate.notNull(createGateSdb);
        Validate.notNull(entityManagerFactory);
        Validate.notNull(playerRepository);
        Validate.notNull(bankRepository);
        Validate.notNull(posByDieSdb);
        Validate.notNull(guildRepository);
        this.guildRepository = guildRepository;
        this.itemFactory = itemFactory;
        this.npcFactory = npcFactory;
        this.itemSdb = itemSdb;
        this.monstersSdb = monstersSdb;
        this.mapSdb = mapSdb;
        this.realmSpecificSdbRepository = realmSpecificSdbRepository;
        this.dynamicObjectFactory = dynamicObjectFactory;
        this.createGateSdb = createGateSdb;
        this.playerRepository = playerRepository;
        this.bankRepository = bankRepository;
        this.posByDieSdb = posByDieSdb;
        this.entityManagerFactory = entityManagerFactory;
        this.itemRepository = itemRepository;
        this.kungFuBookRepository = kungFuBookRepository;
    }

    private NpcManager createNpcManager(int id,
                                                AOIManager aoiManager,
                                                EntityIdGenerator idGenerator,
                                                GroundItemManager itemManager,
                                                EntityEventSender entityEventSender,
                                                RealmMap realmMap) {
        if (!realmSpecificSdbRepository.monsterSdbExists(id) && !realmSpecificSdbRepository.npcSdbExists(id)) {
            return NpcManager.EMPTY;
        }
        var monsterSdb = realmSpecificSdbRepository.monsterSdbExists(id) ? realmSpecificSdbRepository.loadCreateMonster(id) : null;
        var npcSdb = realmSpecificSdbRepository.npcSdbExists(id) ? realmSpecificSdbRepository.loadCreateNpc(id) : null;
        var haveItemSdb = realmSpecificSdbRepository.loadHaveItem(id);
        return mapSdb.getRegenInterval(id).isPresent() ?
                new DungeonNpcManager(entityEventSender, idGenerator,  npcFactory, itemManager, monstersSdb, aoiManager,  monsterSdb, npcSdb, realmMap, haveItemSdb) :
                new NpcManagerImpl(entityEventSender, idGenerator,  npcFactory, itemManager, monstersSdb, aoiManager,  monsterSdb, npcSdb, realmMap, haveItemSdb);
    }

    private DeadPlayerTeleportManager deadPlayerTeleportManager(int id) {
        return posByDieSdb.findIdByRealmId(id)
                .map(server -> new DeadPlayerTeleportManagerImpl(posByDieSdb.getDestServer(server), Coordinate.xy(posByDieSdb.getDestX(server), posByDieSdb.getDestY(server))))
                .orElse(null);
    }


    private boolean allowGuildCreation(int id) {
        return 1 == id;
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
            var dynamicObjectManager = !realmSpecificSdbRepository.objectSdbExists(id) ? DynamicObjectManager.EMPTY:
                    new DynamicObjectManagerImpl(dynamicObjectFactory, entityIdGenerator, eventSender, itemManager, realmSpecificSdbRepository.loadCreateObject(id), crossRealmEventSender, realmMap);
            var playerManager = new PlayerManagerImpl(eventSender, itemManager, itemFactory, dynamicObjectManager,
                    new BankManagerImpl(eventSender, npcManager, bankRepository), playerRepository, deadPlayerTeleportManager(id),
                    crossRealmEventSender);
            var teleportManager = new TeleportManager(id, realmMap, createGateSdb, entityIdGenerator, aoiManager);
            var builder = RealmBuilder.builder()
                    .id(id)
                    .crossRealmEventHandler(crossRealmEventSender)
                    .dynamicObjectManager(dynamicObjectManager)
                    .eventSender(eventSender)
                    .itemManager(itemManager)
                    .mapSdb(mapSdb)
                    .whitelistIds(DUNGEON_WHITELIST_IDS.getOrDefault(id, Collections.emptySet()))
                    .conjunction(CONJUNCTION_IDS.contains(id))
                    .npcManager(npcManager)
                    .playerManager(playerManager)
                    .realmMap(realmMap)
                    .teleportManager(teleportManager)
                    .chatManager(new ChatManagerImpl(playerManager, eventSender, crossRealmEventSender))
                    ;
            if (allowGuildCreation(id)) {
                GuildManager guildManager = new GuildManagerImpl(dynamicObjectFactory, entityIdGenerator, eventSender, crossRealmEventSender, realmMap, guildRepository, itemRepository,
                        entityManagerFactory, id, KungFuSdb.INSTANCE, kungFuBookRepository);
                builder.guildManager(guildManager);
            }
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

        private boolean conjunction;

        private Set<Integer> whitelistIds;

        private GuildManager guildManager;

        private RealmBuilder() {
        }

        public static RealmBuilder builder() {
            return new RealmBuilder();
        }

        public RealmBuilder realmMap(RealmMap realmMap) {
            this.realmMap = realmMap;
            return this;
        }

        public RealmBuilder whitelistIds(Set<Integer> ids) {
            this.whitelistIds = ids;
            return this;
        }

        public RealmBuilder guildManager(GuildManager guildManager) {
            this.guildManager = guildManager;
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

        public RealmBuilder conjunction(boolean c) {
            this.conjunction = c;
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
            if (guildManager == null)
                return new RealmImpl(id, realmMap, eventSender, itemManager, npcManager, playerManager, dynamicObjectManager, teleportManager, crossRealmEventSender, mapSdb, chatManager);
            else
                return new GuildableRealm(id, realmMap, eventSender, itemManager, npcManager, playerManager, dynamicObjectManager, teleportManager, crossRealmEventSender, mapSdb, chatManager, guildManager);
        }

        public Realm buildDungeon(int interval) {
            if (conjunction) {
                return new ConjunctionDungeonRealm(id, realmMap, eventSender, itemManager, npcManager, playerManager, dynamicObjectManager, teleportManager, crossRealmEventSender, mapSdb, interval, chatManager);
            } else {
                return new EntranceDungeonRealm(id, realmMap, eventSender, itemManager, npcManager, playerManager, dynamicObjectManager, teleportManager, crossRealmEventSender, mapSdb, interval, chatManager, whitelistIds);
            }
        }
    }
}
