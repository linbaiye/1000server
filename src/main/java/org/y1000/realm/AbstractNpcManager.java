package org.y1000.realm;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.event.*;
import org.y1000.entities.creatures.npc.*;
import org.y1000.entities.creatures.npc.event.FilterVisibleEntityEvent;
import org.y1000.entities.creatures.npc.event.NpcEvent;
import org.y1000.entities.players.Player;
import org.y1000.event.EntityEvent;
import org.y1000.message.I2ClientMessage;
import org.y1000.message.RemoveEntityMessage;
import org.y1000.realm.event.RealmEvent;
import org.y1000.realm.event.RealmTriggerEvent;
import org.y1000.sdb.*;
import org.y1000.util.Coordinate;
import org.y1000.util.Rectangle;

import java.util.*;

abstract class AbstractNpcManager extends AbstractMovableEntityManager<Npc>
        implements NpcManager, NpcEventListener, NpcEventHandler {

    private final EntityIdGenerator idGenerator;

    private final NpcFactory npcFactory;

    private final ProjectileManager projectileManager;

    private final GroundItemManager itemManager;
    private final MonstersSdb monstersSdb;

    private final Set<Long> cloned;

    private final AOIManager aoiManager;

    private final CreateNpcSdb createMonsterSdb;

    private final CreateNonMonsterSdb createNpcSdb;

    private final RealmMap realmMap;

    private final HaveItemSdb haveItemSdb;


    public AbstractNpcManager(MessageSender sender,
                              EntityIdGenerator idGenerator,
                              NpcFactory npcFactory,
                              GroundItemManager itemManager,
                              MonstersSdb monstersSdb,
                              AOIManager aoiManager,
                              CreateNpcSdb createMonsterSdb,
                              CreateNonMonsterSdb createNpcSdb,
                              RealmMap realmMap,
                              HaveItemSdb haveItemSdb) {
        super(aoiManager, sender);
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
        this.idGenerator = idGenerator;
        this.npcFactory = npcFactory;
        this.itemManager = itemManager;
        this.monstersSdb = monstersSdb;
        this.aoiManager = aoiManager;
        this.haveItemSdb = haveItemSdb;
        this.realmMap = realmMap;
        projectileManager = new ProjectileManager();
        cloned = new HashSet<>();
    }


    protected Npc createNpc(String name, Coordinate coordinate) {
        return npcFactory.create(idGenerator.next(), name, realmMap, coordinate, this);
    }

    Set<Long> spawnNPCs(NpcSpawnSetting setting) {
        var name = setting.idName();
        Set<Long> ids = new HashSet<>();
        for (int i = 0; i < setting.number(); i++) {
            try {
                var npc = spawnNpc(name, setting.range());
                ids.add(npc.id());
            } catch (Exception e) {
                log().error("Failed to create npc {}.", name, e);
                throw new RuntimeException(e);
            }
        }
        return ids;
    }

    Npc spawnNpc(String idName, Rectangle range) {
        Coordinate coordinate = range.random(realmMap::movable)
                .or(() -> range.findFirst(realmMap::movable))
                .orElse(range.start());
        Npc npc = createNpc(idName, coordinate);
        npc.startAI();
        addNpc(npc);
        return npc;
    }

    Optional<CreateNpcSdb> createMonsterSdb() {
        return Optional.ofNullable(createMonsterSdb);
    }

    Optional<CreateNpcSdb> createNpcSdb() {
        return Optional.ofNullable(createNpcSdb);
    }


    void doUpdateEntities(long delta) {
        updateManagedEntities(delta);
//        projectileManager.update(delta);
    }


    protected void addNpc(Npc npc) {
        add(npc);
        sendToVisiblePlayers(npc, npc.captureSnapshot());
    }


    void removeAndSync(Npc source, I2ClientMessage message) {
        sendToVisiblePlayers(source, message);
        getAoiManager().remove(source);
        remove(source);
        source.free();
    }


    public void onMoved(Npc npc, I2ClientMessage message) {
        Set<Entity> visibleOrInvisible = getAoiManager().update(npc);
        visibleOrInvisible.forEach(entity -> {
            if (entity instanceof Player player) {
                if (npc.canBeSeenAt(player.coordinate())) {
                    getMessageSender().sendTo(player, npc.captureSnapshot());
                } else {
                    getMessageSender().sendTo(player, new RemoveEntityMessage(npc.id()));
                }
            }
        });
        sendToVisiblePlayers(npc, message);
    }

    private void handleDieEvent(CreatureDieEvent event) {
        if (!(event.source() instanceof INpc npc)) {
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
//        if (linked.containsKey(npc)) {
//            linked.get(npc).forEach(INpc::die);
//        }
    }

    protected int getRespawnMillis(String idName) {
        return monstersSdb.getRegenInterval(idName) * 10;
    }

    @Override
    public void handleCrossRealmEvent(RealmEvent crossRealmEvent) {
        if (!(crossRealmEvent instanceof RealmTriggerEvent letterEvent)) {
            return;
        }
//        find(npc -> npc.idName().equals(letterEvent.toName()) && NineTailFoxHuman.class.isAssignableFrom(npc.getClass()))
//                .stream().map(NineTailFoxHuman.class::cast)
//                .forEach(NineTailFoxHuman::shift);
    }

    @Override
    public void filter(FilterVisibleEntityEvent event) {
        event.filter(getAoiManager().filterVisibleEntities(event.source(), Entity.class));
    }

    abstract void onUnhandledEvent(EntityEvent entityEvent) ;

//    Npc replaceNpc(NpcShiftEvent shiftEvent) {
//        Npc npc = shiftEvent.npc();
//        removeNpc(npc);
//        Npc newNpc = createNpc(shiftEvent.shiftToName(), npc.coordinate());
//        addNpc(newNpc);
//        return newNpc;
//        return null;
//    }

    boolean isCloned(Npc npc) {
        return cloned.contains(npc.id());
    }

    void removeFromCloned(Npc npc) {
        cloned.remove(npc.id());
    }

//    private void handleCloneEvent(NpcCastCloneEvent event) {
//        var set =  new HashSet<INpc>();
//        var random = ThreadLocalRandom.current();
//        for (int i = 0; i < event.number(); i++) {
//            Coordinate coordinate = event.npc().coordinate();
//            int x = coordinate.x() - 2;
//            x += random.nextInt(0, 4);
//            int y = coordinate.y() - 2;
//            y += random.nextInt(0, 4);
//            Coordinate coordinate1 = Coordinate.xy(x, y);
//            if (event.npc().realmMap().movable(coordinate1)) {
//                var newNpc = npcFactory.createClonedNpc(event.npc(), idGenerator.next(), coordinate1);
//                if (newNpc instanceof AggressiveNpc aggressiveNpc) {
//                    aggressiveNpc.actAggressively(event.enemy());
//                }
//                addNpc(newNpc);
//                set.add(newNpc);
//                cloned.add(newNpc.id());
//            }
//        }
//        linked.put(event.npc(), set);
//    }

    private void handleSeekPlayerEvent(SeekPlayerEvent event) {
        Set<Player> players = aoiManager.filterVisibleEntities(event.source(), Player.class);
        event.setPlayers(players);
    }

    @Override
    public void onEvent(NpcEvent npcEvent) {
        npcEvent.accept(this);
    }

    @Override
    public void onEvent(EntityEvent entityEvent) {
//        if (entityEvent instanceof MonsterShootEvent shootEvent) {
//            projectileManager.add(shootEvent.projectile());
//            sender.notifyVisiblePlayers(shootEvent.source(), shootEvent);
//        } else if (entityEvent instanceof CreatureDieEvent dieEvent) {
//            handleDieEvent(dieEvent);
//        } else if (entityEvent instanceof NpcCastCloneEvent cloneEvent) {
//            handleCloneEvent(cloneEvent);
//        } else if (entityEvent instanceof SeekPlayerEvent seekPlayerEvent) {
//            handleSeekPlayerEvent(seekPlayerEvent);
//        } else if (entityEvent instanceof SeekAggressiveMonsterEvent seekAggressiveMonsterEvent) {
//            seekAggressiveMonsterEvent.handle(getEntities().stream());
//        } else if (entityEvent instanceof Npc2ClientEvent clientEvent) {
//            sender.notifyVisiblePlayers(clientEvent.source(), clientEvent);
//        } else {
//            onUnhandledEvent(entityEvent);
//        }
    }
}
