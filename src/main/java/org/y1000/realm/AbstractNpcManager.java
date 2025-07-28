package org.y1000.realm;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.npc.*;
import org.y1000.entities.creatures.npc.event.NpcEvent;
import org.y1000.entities.creatures.npc.event.NpcShootEvent;
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

    private final CreateNpcSdb createMonsterSdb;

    private final CreateNonMonsterSdb createNpcSdb;

    private final RealmMap realmMap;

    private final HaveItemSdb haveItemSdb;

    private final Map<Npc, List<Npc>> copiedNpcMap;


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
        this.haveItemSdb = haveItemSdb;
        this.realmMap = realmMap;
        projectileManager = new ProjectileManager();
        copiedNpcMap = new HashMap<>();
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
        Npc npc = npcFactory.create(idGenerator.next(), idName, realmMap, coordinate, this);
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
    }


    protected void addNpc(Npc npc) {
        add(npc);
        sendToVisiblePlayers(npc, npc.captureSnapshot());
    }


    void syncAndRemove(Npc source, I2ClientMessage message) {
        sendToVisiblePlayers(source, message);
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
    public void shoot(NpcShootEvent event) {
        projectileManager.add(event.getNpcProjectile());
        sendToVisiblePlayers(event.source(), event);
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


    @Override
    public void onEvent(NpcEvent npcEvent) {
        npcEvent.accept(this);
    }

    @Override
    public void dropItem(String name, int number, Coordinate dropAt) {
        itemManager.dropItem(name, number, dropAt);
    }

    @Override
    public void call(String name, ActiveEntity enemy, Coordinate callAt) {
        var npc = npcFactory.createCalledNpc(idGenerator.next(), name, realmMap, callAt, this);
        npc.startAI(new CombatAI(npc, enemy));
        addNpc(npc);
    }

    @Override
    public void copy(Npc npc, int number, ActiveEntity enemy) {
        List<Npc> copied = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            var coordinate = npc.coordinate().neighbours()
                    .stream().filter(realmMap::movable).findFirst().orElse(npc.coordinate());
            var c = npcFactory.createCopied(idGenerator.next(), npc.getIdName(), realmMap, coordinate, this);
            copied.add(c);
            c.startAI(new CombatAI(c, enemy));
            addNpc(c);
        }
        copiedNpcMap.put(npc, copied);
    }

    @Override
    public void onDie(Npc npc, I2ClientMessage message) {
        sendToVisiblePlayers(npc, message);
        var copied = copiedNpcMap.remove(npc);
        if (copied != null)
            copied.forEach(Npc::instantKill);
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
