package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.creatures.event.NpcJoinedEvent;
import org.y1000.entities.creatures.event.MonsterShootEvent;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.creatures.npc.NpcFactory;
import org.y1000.event.EntityEvent;
import org.y1000.event.EntityEventListener;
import org.y1000.sdb.MonsterSpawnSetting;
import org.y1000.util.Coordinate;
import org.y1000.util.Rectangle;

import java.util.*;

@Slf4j
final class NpcManager extends AbstractEntityManager<Npc> implements EntityEventListener {

    private final EntityEventSender sender;

    private final EntityIdGenerator idGenerator;

    private final NpcFactory npcFactory;

    private final Map<String, List<MonsterSpawnSetting>> npcSpawnSettings;

    private final List<RespawningMonster> respawningMonsters;

    private final ProjectileManager projectileManager;

    private final ItemManager itemManager;


    public NpcManager(EntityEventSender sender,
                      EntityIdGenerator idGenerator,
                      NpcFactory npcFactory,
                      ItemManager itemManager) {
        this.sender = sender;
        this.idGenerator = idGenerator;
        this.npcFactory = npcFactory;
        this.itemManager = itemManager;
        this.npcSpawnSettings = new HashMap<>();
        respawningMonsters = new ArrayList<>();
        projectileManager = new ProjectileManager();
    }


    private static class RespawningMonster {
        private final Npc npc;
        private int time;

        private RespawningMonster(Npc npc, int time) {
            this.npc = npc;
            this.time = time;
        }

        public RespawningMonster update(long delta) {
            this.time -= (int)delta;
            return this;
        }

        public boolean canRespawn() {
            return this.time <= 0;
        }
    }

    private List<MonsterSpawnSetting> getSettings(String name) {
        if (!npcSpawnSettings.containsKey(name)) {
            Coordinate coordinate = Coordinate.xy(178, 45);
            var list = Collections.singletonList(new MonsterSpawnSetting(new Rectangle(coordinate.move(-4, -4), coordinate.move(4, 4)), 4));
            npcSpawnSettings.put(name, list);
        }
        return npcSpawnSettings.get(name);
    }


    private void spawnMonsters(String name, RealmMap map, MonsterSpawnSetting setting) {
        for (int i = 0; i < setting.number(); i++) {
            var npc = npcFactory.createMonster(name, idGenerator.next(), map, setting.range().random());
            add(npc);
        }
    }


    private void spawnMerchant(String name, RealmMap map) {
        var merchant = npcFactory.createMerchant(name, idGenerator.next(), map, Coordinate.xy(178, 45));
        add(merchant);
    }


    private void respawn(Npc npc) {
        List<MonsterSpawnSetting> settings = getSettings(npc.name());
        for (MonsterSpawnSetting setting : settings) {
            if (setting.range().contains(npc.coordinate())) {
                Coordinate random = setting.range().random();
                npc.revive(random);
                add(npc);
            }
        }
    }


    public void init(RealmMap realmMap) {
        try {
            var name = "牛";
            List<MonsterSpawnSetting> settings = getSettings("牛");
            settings.forEach(setting -> spawnMonsters(name, realmMap, setting));
            spawnMerchant("老板娘", realmMap);
//            var npcs = create(name, realmMap, setting.get(0));
//            List<AbstractMonster> npcs = List.of(
//                    npcFactory.createMonster("犀牛", idGenerator.next(), re)),
//                    npcFactory.createMonster("牛", idGenerator.next(), realmMap, new Coordinate(39, 31)),
//                    npcFactory.createMonster("老虎", idGenerator.next(), realmMap, new Coordinate(39, 32)),
//                    npcFactory.createMonster("忍者", idGenerator.next(), realmMap, new Coordinate(39, 33)),
//                    npcFactory.createMonster("赤风", idGenerator.next(), realmMap, new Coordinate(39, 35)),
//                    npcFactory.createMonster("太极公子", idGenerator.next(), realmMap, new Coordinate(39, 36)),
//                    npcFactory.createMonster("投石女", idGenerator.next(), realmMap, new Coordinate(39, 37))
//                    npcFactory.createMonster("牛", idGenerator.next(), realmMap, new Coordinate(39, 31))
//                    npcFactory.createMonster("鹿", entityManager.generateEntityId(), map(), new Coordinate(39, 32))
//            );
//            npcs.forEach(this::add);
        } catch (Exception e) {
            log.error("Exception ", e);
        }
    }


    @Override
    protected Logger log() {
        return log;
    }

    private void updateRespawning(long delta) {
        Iterator<RespawningMonster> iterator = respawningMonsters.iterator();
        while (iterator.hasNext()) {
            RespawningMonster respawningMonster = iterator.next();
            if (respawningMonster.update(delta).canRespawn()) {
                iterator.remove();
                respawn(respawningMonster.npc);
            }
        }
    }

    @Override
    public void update(long delta) {
        updateManagedEntities(delta);
        updateRespawning(delta);
        projectileManager.update(delta);
    }

    @Override
    protected void onAdded(Npc entity) {
        sender.add(entity);
        sender.notifyVisiblePlayers(entity, new NpcJoinedEvent(entity));
        entity.registerEventListener(this);
        entity.registerEventListener(itemManager);
        entity.start();
    }

    @Override
    protected void onDeleted(Npc entity) {
        sender.remove(entity);
        respawningMonsters.add(new RespawningMonster(entity, 8000));
        entity.deregisterEventListener(this);
        entity.deregisterEventListener(itemManager);
    }

    private void onRemoveEntity(RemoveEntityEvent removeEntityEvent) {
        if (!(removeEntityEvent.source() instanceof Npc npc)) {
            return;
        }
        delete(npc);
    }

    @Override
    public void onEvent(EntityEvent entityEvent) {
        if (entityEvent instanceof RemoveEntityEvent removeEntityEvent) {
            onRemoveEntity(removeEntityEvent);
        } else if (entityEvent instanceof MonsterShootEvent shootEvent) {
            projectileManager.add(shootEvent.projectile());
            sender.notifyVisiblePlayers(shootEvent.source(), shootEvent);
        }
    }
}
