package org.y1000.entities.creatures.monster;

import org.apache.commons.lang3.NotImplementedException;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.realm.RealmMap;
import org.y1000.sdb.ActionSdb;
import org.y1000.util.Coordinate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MonsterFactoryImpl implements MonsterFactory {

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

    private AggressiveMonster createAggresiveCreature(String name, long id, RealmMap map, Coordinate coordinate) {
        return AggressiveMonster.builder()
                .id(id)
                .name(name)
                .recovery(monsterSdb.getRecovery(name))
                .avoidance(monsterSdb.getAvoid(name))
                .life(monsterSdb.getLife(name))
                .attackSpeed(150)
                .recovery(70)
                .avoidance(25)
                .armor(monsterSdb.getArmor(name))
                .realmMap(map)
                .wanderingRange(monsterSdb.getActionWidth(name))
                .coordinate(coordinate)
                .direction(Direction.DOWN)
                .stateMillis(createActionLengthMap(monsterSdb.getAnimate(name)))
                .attackSound(monsterSdb.getSoundAttack(name))
                .build();
    }

    private PassiveMonster createPassiveCreature(String name, long id, RealmMap map, Coordinate coordinate) {
        return PassiveMonster.builder()
                .id(id)
                .name(name)
                .recovery(monsterSdb.getRecovery(name))
                .avoidance(monsterSdb.getAvoid(name))
                .life(monsterSdb.getLife(name))
                .attackSpeed(150 + monsterSdb.getAttackSpeed(name))
                .recovery(70)
                .avoidance(25)
                .armor(monsterSdb.getArmor(name))
                .realmMap(map)
                .wanderingRange(monsterSdb.getActionWidth(name))
                .coordinate(coordinate)
                .direction(Direction.DOWN)
                .stateMillis(createActionLengthMap(monsterSdb.getAnimate(name)))
                .attackSound(monsterSdb.getSoundAttack(name))
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
        return passive ? createPassiveCreature(name, id, realmMap, coordinate) : createAggresiveCreature(name, id, realmMap, coordinate);
    }
}
