package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.creatures.event.MonsterJoinedEvent;
import org.y1000.entities.creatures.event.MonsterShootEvent;
import org.y1000.entities.creatures.monster.AbstractMonster;
import org.y1000.entities.creatures.monster.MonsterFactory;
import org.y1000.event.EntityEvent;
import org.y1000.event.EntityEventListener;
import org.y1000.sdb.MonsterSpawnSetting;
import org.y1000.util.Coordinate;
import org.y1000.util.Rectangle;

import java.util.*;

@Slf4j
final class MonsterManager extends AbstractEntityManager<AbstractMonster> implements EntityEventListener {

    private final EntityEventSender sender;

    private final EntityIdGenerator idGenerator;

    private final MonsterFactory monsterFactory;

    private final Map<String, List<MonsterSpawnSetting>> monsterSpawnSettings;

    private final List<RespawningMonster> respawningMonsters;

    private final ProjectileManager projectileManager;

    private final ItemManager itemManager;


    public MonsterManager(EntityEventSender sender,
                          EntityIdGenerator idGenerator,
                          MonsterFactory monsterFactory,
                          ItemManager itemManager) {
        this.sender = sender;
        this.idGenerator = idGenerator;
        this.monsterFactory = monsterFactory;
        this.itemManager = itemManager;
        this.monsterSpawnSettings = new HashMap<>();
        respawningMonsters = new ArrayList<>();
        projectileManager = new ProjectileManager();
    }


    private static class RespawningMonster {
        private final AbstractMonster monster;
        private int time;

        private RespawningMonster(AbstractMonster monster, int time) {
            this.monster = monster;
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
        if (!monsterSpawnSettings.containsKey(name)) {
            Coordinate coordinate = Coordinate.xy(178, 45);
            var list = Collections.singletonList(new MonsterSpawnSetting(new Rectangle(coordinate.move(-4, -4), coordinate.move(4, 4)), 4));
            monsterSpawnSettings.put(name, list);
        }
        return monsterSpawnSettings.get(name);
    }


    private void spawnMonsters(String name, RealmMap map, MonsterSpawnSetting setting) {
        for (int i = 0; i < setting.number(); i++) {
            var monster = monsterFactory.createMonster(name, idGenerator.next(), map, setting.range().random());
            add(monster);
        }
    }


    private void respawn(AbstractMonster monster) {
        List<MonsterSpawnSetting> settings = getSettings(monster.name());
        for (MonsterSpawnSetting setting : settings) {
            if (setting.range().contains(monster.coordinate())) {
                Coordinate random = setting.range().random();
                monster.revive(random);
                add(monster);
            }
        }
    }


    public void init(RealmMap realmMap) {
        try {
            var name = "牛";
            List<MonsterSpawnSetting> settings = getSettings("牛");
            settings.forEach(setting -> spawnMonsters(name, realmMap, setting));
//            var monsters = create(name, realmMap, setting.get(0));

//            List<AbstractMonster> monsters = List.of(
//                    monsterFactory.createMonster("犀牛", idGenerator.next(), re)),
//                    monsterFactory.createMonster("牛", idGenerator.next(), realmMap, new Coordinate(39, 31)),
//                    monsterFactory.createMonster("老虎", idGenerator.next(), realmMap, new Coordinate(39, 32)),
//                    monsterFactory.createMonster("忍者", idGenerator.next(), realmMap, new Coordinate(39, 33)),
//                    monsterFactory.createMonster("赤风", idGenerator.next(), realmMap, new Coordinate(39, 35)),
//                    monsterFactory.createMonster("太极公子", idGenerator.next(), realmMap, new Coordinate(39, 36)),
//                    monsterFactory.createMonster("投石女", idGenerator.next(), realmMap, new Coordinate(39, 37))
//                    monsterFactory.createMonster("牛", idGenerator.next(), realmMap, new Coordinate(39, 31))
//                    monsterFactory.createMonster("鹿", entityManager.generateEntityId(), map(), new Coordinate(39, 32))
//            );
//            monsters.forEach(this::add);
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
                respawn(respawningMonster.monster);
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
    protected void onAdded(AbstractMonster entity) {
        sender.add(entity);
        entity.registerEventListener(this);
        sender.sendEvent(new MonsterJoinedEvent(entity));
        entity.registerEventListener(itemManager);
    }

    @Override
    protected void onDeleted(AbstractMonster entity) {
        sender.remove(entity);
        respawningMonsters.add(new RespawningMonster(entity, 8000));
    }

    private void onRemoveEntity(RemoveEntityEvent removeEntityEvent) {
        if (!(removeEntityEvent.source() instanceof AbstractMonster monster)) {
            return;
        }
        delete(monster);
    }

    @Override
    public void onEvent(EntityEvent entityEvent) {
        if (entityEvent instanceof RemoveEntityEvent removeEntityEvent) {
            onRemoveEntity(removeEntityEvent);
        } else if (entityEvent instanceof MonsterShootEvent shootEvent) {
            projectileManager.add(shootEvent.projectile());
        }
    }
}
