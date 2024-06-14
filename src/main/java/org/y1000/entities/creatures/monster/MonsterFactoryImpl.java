package org.y1000.entities.creatures.monster;

import org.apache.commons.lang3.NotImplementedException;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.realm.RealmMap;
import org.y1000.sdb.ActionSdb;
import org.y1000.sdb.MonsterSdb;
import org.y1000.util.Coordinate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class MonsterFactoryImpl implements MonsterFactory {

    private final ActionSdb actionSdb;

    private final MonsterSdb monsterSdb;

    public MonsterFactoryImpl(ActionSdb actionSdb, MonsterSdb monsterSdb) {
        this.actionSdb = actionSdb;
        this.monsterSdb = monsterSdb;
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

    private AggressiveMonster createAggressiveCreature(String name, long id, RealmMap map, Coordinate coordinate) {
        return new AggressiveMonster(id, coordinate, Direction.DOWN, name, map,
                createActionLengthMap(monsterSdb.getAnimate(name)),
                new MonsterAttributeProvider(name, monsterSdb));
    }

    private PassiveMonster createPassiveCreature(String name, long id, RealmMap map, Coordinate coordinate) {
        return new PassiveMonster(id, coordinate, Direction.DOWN, name, map,
                createActionLengthMap(monsterSdb.getAnimate(name)),
                new MonsterAttributeProvider(name, monsterSdb));
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
