package org.y1000.realm;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.creatures.event.*;
import org.y1000.entities.creatures.npc.AggressiveNpc;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.creatures.npc.NpcFactory;
import org.y1000.entities.players.Player;
import org.y1000.event.EntityEvent;
import org.y1000.event.EntityEventListener;
import org.y1000.sdb.CreateEntitySdbRepository;
import org.y1000.sdb.CreateNpcSdb;
import org.y1000.sdb.MonstersSdb;
import org.y1000.sdb.NpcSpawnSetting;
import org.y1000.util.Coordinate;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

abstract class AbstractNpcManager extends AbstractActiveEntityManager<Npc> implements EntityEventListener {

    private final EntityEventSender sender;

    private final EntityIdGenerator idGenerator;

    private final NpcFactory npcFactory;

    private final ProjectileManager projectileManager;

    private final GroundItemManager itemManager;

    private final CreateEntitySdbRepository createEntitySdbRepository;

    private final MonstersSdb monstersSdb;

    private final Map<Npc, Set<Npc>> linked;

    private final Set<Long> cloned;

    private final AOIManager aoiManager;

    public AbstractNpcManager(EntityEventSender sender,
                              EntityIdGenerator idGenerator,
                              NpcFactory npcFactory,
                              GroundItemManager itemManager,
                              CreateEntitySdbRepository createEntitySdbRepository,
                              MonstersSdb monstersSdb,
                              AOIManager aoiManager) {
        Validate.notNull(sender);
        Validate.notNull(idGenerator);
        Validate.notNull(itemManager);
        Validate.notNull(npcFactory);
        Validate.notNull(createEntitySdbRepository);
        Validate.notNull(monstersSdb);
        Validate.notNull(aoiManager);
        this.sender = sender;
        this.idGenerator = idGenerator;
        this.npcFactory = npcFactory;
        this.itemManager = itemManager;
        this.monstersSdb = monstersSdb;
        projectileManager = new ProjectileManager();
        this.createEntitySdbRepository = createEntitySdbRepository;
        linked = new HashMap<>();
        cloned = new HashSet<>();
        this.aoiManager = aoiManager;
    }


    void spawnNPCs(CreateNpcSdb createNpcSdb, RealmMap map) {
        List<NpcSpawnSetting> allSettings = createNpcSdb.getAllSettings();
        int total = 0;
        for (NpcSpawnSetting setting : allSettings) {
            var name = setting.idName();
            for (int i = 0; i < setting.number(); i++) {
                try {
                    total += setting.number();
                    Optional<Coordinate> random = setting.range().random(map::movable);
                    random.ifPresentOrElse(p -> addNpc(npcFactory.createNpc(name, idGenerator.next(), map, p)),
                            () -> log().warn("Not able to spawn monster {} within range {} on map {}..", name, setting.range(), map.mapFile()));
                } catch (Exception e) {
                    log().error("Failed to create npc {}.", name, e);
                }
            }
        }
        log().debug("Created {} npc in total.", total);
    }

    Optional<CreateNpcSdb> createMonsterSdb(int realmId) {
        return createEntitySdbRepository.monsterSdbExists(realmId) ?
            Optional.of(createEntitySdbRepository.loadMonster(realmId)) : Optional.empty();
    }

    Optional<CreateNpcSdb> createNpcSdb(int realmId) {
        return createEntitySdbRepository.npcSdbExists(realmId) ?
            Optional.of(createEntitySdbRepository.loadNpc(realmId)) : Optional.empty();
    }


    @Override
    public void update(long delta) {
        updateManagedEntities(delta);
        projectileManager.update(delta);
    }


    protected void removeNpc(Npc npc) {
        sender.remove(npc);
        npc.deregisterEventListener(this);
        remove(npc);
    }

    protected void addNpc(Npc npc) {
        sender.add(npc);
        sender.notifyVisiblePlayers(npc, new NpcJoinedEvent(npc));
        npc.registerEventListener(this);
        npc.start();
        add(npc);
    }


    private void handleDieEvent(CreatureDieEvent event) {
        if (!(event.source() instanceof Npc npc)) {
            return;
        }
        var dropItems = monstersSdb.getHaveItem(npc.idName());
        if (!StringUtils.isEmpty(dropItems)) {
            itemManager.dropItem(dropItems, event.source().coordinate());
        }
        if (linked.containsKey(npc)) {
            linked.get(npc).forEach(Npc::die);
        }
    }

    abstract void onUnhandledEvent(EntityEvent entityEvent) ;

    private void handleShiftEvent(NpcShiftEvent shiftEvent) {
        Npc npc = shiftEvent.npc();
        sender.notifyVisiblePlayers(npc, shiftEvent.createRemoveEvent());
        removeNpc(npc);
        Npc newNpc = npcFactory.createNpc(shiftEvent.shiftToName(), idGenerator.next(), npc.realmMap(), npc.coordinate());
        addNpc(newNpc);
    }

    boolean isCloned(Npc npc) {
        return cloned.contains(npc.id());
    }

    void removeFromCloned(Npc npc) {
        cloned.remove(npc.id());
    }

    private void handleCloneEvent(NpcCastCloneEvent event) {
        var set =  new HashSet<Npc>();
        var random = ThreadLocalRandom.current();
        for (int i = 0; i < event.number(); i++) {
            Coordinate coordinate = event.npc().coordinate();
            int x = coordinate.x() - 2;
            x += random.nextInt(0, 4);
            int y = coordinate.y() - 2;
            y += random.nextInt(0, 4);
            Coordinate coordinate1 = Coordinate.xy(x, y);
            if (event.npc().realmMap().movable(coordinate1)) {
                var newNpc = npcFactory.createClonedNpc(event.npc(), idGenerator.next(), coordinate1);
                if (newNpc instanceof AggressiveNpc aggressiveNpc) {
                    aggressiveNpc.actAggressively(event.enemy());
                }
                addNpc(newNpc);
                set.add(newNpc);
                cloned.add(newNpc.id());
            }
        }
        linked.put(event.npc(), set);
    }

    private void handleSeekPlayerEvent(SeekPlayerEvent event) {
        Set<Player> players = aoiManager.filterVisibleEntities(event.source(), Player.class);
        AggressiveNpc npc = event.aggressiveNpc();
        players.stream().filter(npc::canActAggressively).findFirst().ifPresent(npc::actAggressively);
    }

    @Override
    public void onEvent(EntityEvent entityEvent) {
        if (entityEvent instanceof MonsterShootEvent shootEvent) {
            projectileManager.add(shootEvent.projectile());
            sender.notifyVisiblePlayers(shootEvent.source(), shootEvent);
        } else if (entityEvent instanceof CreatureDieEvent dieEvent) {
            handleDieEvent(dieEvent);
        } else if (entityEvent instanceof NpcShiftEvent shiftEvent) {
            handleShiftEvent(shiftEvent);
        } else if (entityEvent instanceof NpcCastCloneEvent cloneEvent) {
            handleCloneEvent(cloneEvent);
        } else if (entityEvent instanceof SeekPlayerEvent seekPlayerEvent) {
            handleSeekPlayerEvent(seekPlayerEvent);
        } else {
            onUnhandledEvent(entityEvent);
        }
    }
}
