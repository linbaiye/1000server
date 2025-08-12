package org.y1000.realm;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.npc.*;
import org.y1000.entities.creatures.npc.event.NpcEvent;
import org.y1000.entities.creatures.npc.event.NpcShootEvent;
import org.y1000.entities.players.Player;
import org.y1000.network.I2ClientMessage;
import org.y1000.entities.RemoveEntityMessage;
import org.y1000.sdb.*;
import org.y1000.util.Coordinate;
import org.y1000.util.Rectangle;

import java.util.*;

abstract class AbstractNpcManager extends AbstractActiveEntityManager<Npc>
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
        projectileManager.update(delta);
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
        if (visibleOrInvisible.isEmpty())
            return;
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
    public void shoot(NpcShootEvent event) {
        projectileManager.add(event.getNpcProjectile());
        sendToVisiblePlayers(event.source(), event);
    }


    @Override
    public void onEvent(NpcEvent npcEvent) {
        npcEvent.accept(this);
    }

    @Override
    public void dropItem(String name, int number, Coordinate dropAt) {
        itemManager.dropItem(name, number, dropAt);
    }

    private Coordinate findSpawnCoordinate(Coordinate origin) {
        return origin.neighbours().stream()
                .filter(realmMap::movable).findFirst().orElse(origin);
    }

    @Override
    public void call(String name, ActiveEntity enemy, Coordinate callAt) {
        var npc = npcFactory.createCalledNpc(idGenerator.next(), name, realmMap, findSpawnCoordinate(callAt), this);
        npc.startAI(new CombatAI(npc, enemy));
        addNpc(npc);
    }

    @Override
    public void copy(Npc npc, int number, ActiveEntity enemy) {
        List<Npc> copied = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            Coordinate spawnCoordinate = findSpawnCoordinate(npc.coordinate());
            if (spawnCoordinate.equals(npc.coordinate()))
                continue;
            var c = npcFactory.createCopied(idGenerator.next(), npc.getIdName(), realmMap, npc.getSpawnCoordinate(), this);
            c.changeCoordinate(spawnCoordinate);
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
}
