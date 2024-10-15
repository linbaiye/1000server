package org.y1000.realm;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.creatures.event.*;
import org.y1000.entities.creatures.npc.AggressiveNpc;
import org.y1000.entities.creatures.npc.NineTailFoxHuman;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.creatures.npc.NpcFactory;
import org.y1000.entities.players.Player;
import org.y1000.event.EntityEvent;
import org.y1000.event.EntityEventListener;
import org.y1000.message.RemoveEntityMessage;
import org.y1000.realm.event.RealmEvent;
import org.y1000.realm.event.RealmTriggerEvent;
import org.y1000.sdb.*;
import org.y1000.util.Coordinate;
import org.y1000.util.Rectangle;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

abstract class AbstractNpcManager extends AbstractActiveEntityManager<Npc> implements EntityEventListener, NpcManager {

    private final EntityEventSender sender;

    private final EntityIdGenerator idGenerator;

    private final NpcFactory npcFactory;

    private final ProjectileManager projectileManager;

    private final GroundItemManager itemManager;

    private final MonstersSdb monstersSdb;

    private final Map<Npc, Set<Npc>> linked;

    private final Set<Long> cloned;

    private final AOIManager aoiManager;

    private final CreateNpcSdb createMonsterSdb;

    private final CreateNonMonsterSdb createNpcSdb;

    private final RealmMap realmMap;

    private final HaveItemSdb haveItemSdb;

    public AbstractNpcManager(EntityEventSender sender,
                              EntityIdGenerator idGenerator,
                              NpcFactory npcFactory,
                              GroundItemManager itemManager,
                              MonstersSdb monstersSdb,
                              AOIManager aoiManager,
                              CreateNpcSdb createMonsterSdb,
                              CreateNonMonsterSdb createNpcSdb,
                              RealmMap realmMap,
                              HaveItemSdb haveItemSdb) {
        Validate.notNull(sender);
        Validate.notNull(idGenerator);
        Validate.notNull(itemManager);
        Validate.notNull(npcFactory);
        Validate.notNull(monstersSdb);
        Validate.notNull(aoiManager);
        Validate.isTrue(createMonsterSdb != null || createNpcSdb != null);
        Validate.notNull(realmMap);
        Validate.notNull(haveItemSdb);
        this.createMonsterSdb = createMonsterSdb;
        this.createNpcSdb = createNpcSdb;
        this.sender = sender;
        this.idGenerator = idGenerator;
        this.npcFactory = npcFactory;
        this.itemManager = itemManager;
        this.monstersSdb = monstersSdb;
        this.aoiManager = aoiManager;
        this.haveItemSdb = haveItemSdb;
        this.realmMap = realmMap;
        projectileManager = new ProjectileManager();
        linked = new HashMap<>();
        cloned = new HashSet<>();
    }


    protected Npc createNpc(String name, Coordinate coordinate) {
        return createNpcSdb != null && createNpcSdb.containsNpc(name)?
                npcFactory.createNonMonsterNpc(name, idGenerator.next(), realmMap, coordinate, createNpcSdb) :
                npcFactory.createNpc(name, idGenerator.next(), realmMap, coordinate);
    }

    void spawnNPCs(CreateNpcSdb createNpcSdb) {
        List<NpcSpawnSetting> allSettings = createNpcSdb.getAllSettings();
        int total = 0;
        for (NpcSpawnSetting setting : allSettings) {
            var name = setting.viewName();
            for (int i = 0; i < setting.number(); i++) {
                try {
                    total += setting.number();
                    Rectangle range = setting.range();
                    Coordinate coordinate = range.random(realmMap::movable)
                            .or(() -> range.findFirst(realmMap::movable))
                            .orElse(range.start());
                    addNpc(createNpc(name, coordinate));
                } catch (Exception e) {
                    log().error("Failed to create npc {}.", name, e);
                    throw new RuntimeException(e);
                }
            }
        }
        log().debug("Created {} npc in total.", total);
    }

    Optional<CreateNpcSdb> createMonsterSdb() {
        return Optional.ofNullable(createMonsterSdb);
    }

    Optional<CreateNpcSdb> createNpcSdb() {
        return Optional.ofNullable(createNpcSdb);
    }


    void doUpdateEntities(long delta) {
        updateManagedEntities(delta);
        projectileManager.update(delta);
    }

    protected void removeNpc(Npc npc) {
        sender.notifyVisiblePlayers(npc, new RemoveEntityMessage(npc.id()));
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
        String dropItems;
        if (haveItemSdb.containsMonster(npc.idName())) {
            dropItems = haveItemSdb.getHaveItem(npc.idName()).orElse(null);
        } else {
            dropItems = monstersSdb.getHaveItem(npc.idName());
        }
        if (!StringUtils.isEmpty(dropItems)) {
            itemManager.dropItem(dropItems, event.source().coordinate());
        }
        if (linked.containsKey(npc)) {
            linked.get(npc).forEach(Npc::die);
        }
    }

    protected int getRespawnMillis(String idName) {
        return monstersSdb.getRegenInterval(idName) * 10;
    }

    @Override
    public void handleCrossRealmEvent(RealmEvent crossRealmEvent) {
        if (!(crossRealmEvent instanceof RealmTriggerEvent letterEvent)) {
            return;
        }
        find(npc -> npc.idName().equals(letterEvent.toName()) && NineTailFoxHuman.class.isAssignableFrom(npc.getClass()))
                .stream().map(NineTailFoxHuman.class::cast)
                .forEach(NineTailFoxHuman::shift);
    }

    abstract void onUnhandledEvent(EntityEvent entityEvent) ;

    Npc replaceNpc(NpcShiftEvent shiftEvent) {
        Npc npc = shiftEvent.npc();
        removeNpc(npc);
        Npc newNpc = createNpc(shiftEvent.shiftToName(), npc.coordinate());
        addNpc(newNpc);
        return newNpc;
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
        event.setPlayers(players);
    }

    @Override
    public void onEvent(EntityEvent entityEvent) {
        if (entityEvent instanceof MonsterShootEvent shootEvent) {
            projectileManager.add(shootEvent.projectile());
            sender.notifyVisiblePlayers(shootEvent.source(), shootEvent);
        } else if (entityEvent instanceof CreatureDieEvent dieEvent) {
            handleDieEvent(dieEvent);
        } else if (entityEvent instanceof NpcCastCloneEvent cloneEvent) {
            handleCloneEvent(cloneEvent);
        } else if (entityEvent instanceof SeekPlayerEvent seekPlayerEvent) {
            handleSeekPlayerEvent(seekPlayerEvent);
        } else if (entityEvent instanceof SeekAggressiveMonsterEvent seekAggressiveMonsterEvent) {
            seekAggressiveMonsterEvent.handle(getEntities().stream());
        } else if (entityEvent instanceof Npc2ClientEvent clientEvent) {
            sender.notifyVisiblePlayers(clientEvent.source(), clientEvent);
        } else {
            onUnhandledEvent(entityEvent);
        }
    }
}
