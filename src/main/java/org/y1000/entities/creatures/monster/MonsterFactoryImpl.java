package org.y1000.entities.creatures.monster;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.kungfu.KungFuSdb;
import org.y1000.realm.RealmMap;
import org.y1000.sdb.ActionSdb;
import org.y1000.sdb.MonsterSdb;
import org.y1000.util.Coordinate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public final class MonsterFactoryImpl implements MonsterFactory {

    private final ActionSdb actionSdb;

    private final MonsterSdb monsterSdb;
    private final KungFuSdb kungFuSdb;

    public MonsterFactoryImpl(ActionSdb actionSdb, MonsterSdb monsterSdb, KungFuSdb kungFuSdb) {
        this.actionSdb = actionSdb;
        this.monsterSdb = monsterSdb;
        this.kungFuSdb = kungFuSdb;
    }

    private Map<State, Integer> createActionLengthMap(String animate) {
        Map<State, Integer> result = new HashMap<>();
        int idle = actionSdb.getActionLength(animate, State.IDLE);
        int move = actionSdb.getActionLength(animate, State.WALK);
        int hurt = actionSdb.getActionLength(animate, State.HURT);
        int attack = actionSdb.getActionLength(animate, State.ATTACK);
        int die = actionSdb.getActionLength(animate, State.DIE);
        int frozen = actionSdb.getActionLength(animate, State.FROZEN);
        result.put(State.IDLE, idle);
        result.put(State.WALK, move);
        result.put(State.HURT, hurt);
        result.put(State.ATTACK, attack);
        result.put(State.DIE, die);
        result.put(State.FROZEN, frozen < 100 ? frozen * 10 : frozen);
        return result;
    }

    private MonsterRangedSpell loadRangedSpell(String name) {
        var magciName = monsterSdb.getAttackMagic(name);
        if (StringUtils.isEmpty(magciName)) {
            log.debug("No magic for {}.", name);
            return null;
        }
        String bowImage = kungFuSdb.getBowImage(magciName.split(":")[0]);
        if (StringUtils.isEmpty(bowImage)) {
            log.debug("No bow image for {}.", magciName);
            return null;
        }
        log.debug("Loaded bow image {} for {}.", bowImage, name);
        return new MonsterRangedSpell(Integer.parseInt(bowImage));
    }

    private AggressiveMonster createAggressiveCreature(String name, long id, RealmMap map, Coordinate coordinate) {
        return new AggressiveMonster(id, coordinate, Direction.DOWN, name, map,
                createActionLengthMap(monsterSdb.getAnimate(name)), new MonsterAttributeProvider(name, monsterSdb),
                loadRangedSpell(name));
    }

    private PassiveMonster createPassiveCreature(String name, long id, RealmMap map, Coordinate coordinate) {
        return PassiveMonster.builder()
                .id(id)
                .coordinate(coordinate)
                .direction(Direction.DOWN)
                .name(name)
                .realmMap(map)
                .stateMillis(createActionLengthMap(monsterSdb.getAnimate(name)))
                .attributeProvider(new MonsterAttributeProvider(name, monsterSdb))
                .rangedSpell(loadRangedSpell(name))
                .build();
    }


    @Override
    public AbstractMonster createMonster(String name, long id, RealmMap realmMap, Coordinate coordinate) {
        Objects.requireNonNull(name);
        String animate = monsterSdb.getAnimate(name);
        if (animate == null) {
            throw new NotImplementedException(name + " has no action sdb.");
        }
        boolean passive = monsterSdb.isPassive(name);
        return passive ? createPassiveCreature(name, id, realmMap, coordinate) : createAggressiveCreature(name, id, realmMap, coordinate);
    }
}
