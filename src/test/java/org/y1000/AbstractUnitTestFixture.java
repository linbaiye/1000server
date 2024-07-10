package org.y1000;

import org.mockito.Mockito;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.monster.MonsterMeleeAttackSkill;
import org.y1000.entities.creatures.monster.PassiveMonster;
import org.y1000.entities.creatures.monster.TestingMonsterAttributeProvider;
import org.y1000.entities.players.*;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.kungfu.KungFuBook;
import org.y1000.kungfu.KungFuBookFactory;
import org.y1000.kungfu.attack.AttackKungFuType;
import org.y1000.realm.Realm;
import org.y1000.realm.RealmMap;
import org.y1000.repository.KungFuBookRepositoryImpl;
import org.y1000.util.Coordinate;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public abstract class AbstractUnitTestFixture {

    protected final KungFuBookFactory kungFuBookFactory = new KungFuBookRepositoryImpl();

    private int id;

    protected Realm mockAllFlatRealm() {
        Realm mock = Mockito.mock(Realm.class);
        RealmMap map = Mockito.mock(RealmMap.class);
        when (map.movable(any(Coordinate.class))).thenReturn(true);
        when(mock.map()).thenReturn(map);
        return mock;
    }

    protected static final Map<State, Integer> MONSTER_STATE_MILLIS = new HashMap<>() {
        {
            put(State.IDLE, 1000);
            put(State.WALK, 770);
            put(State.HURT, 540);
            put(State.ATTACK, 700);
            put(State.DIE, 700);
            put(State.FROZEN, 900);
        }
    };


    protected PassiveMonster.PassiveMonsterBuilder monsterBuilder() {
        return PassiveMonster.builder()
                .id(nextId())
                .coordinate(Coordinate.xy(1, 1))
                .direction(Direction.UP)
                .name("test")
                .realmMap(Mockito.mock(RealmMap.class))
                .attackSkill(new MonsterMeleeAttackSkill())
                .attributeProvider(new TestingMonsterAttributeProvider())
                .stateMillis(MONSTER_STATE_MILLIS)
                ;
    }

    protected PlayerImpl.PlayerImplBuilder playerBuilder() {
        KungFuBook kungFuBook = kungFuBookFactory.create();
        return PlayerImpl.builder()
                .id(1L)
                .coordinate(new Coordinate(1, 1))
                .name("test")
                .kungFuBook(kungFuBook)
                .attackKungFu(kungFuBook.findUnnamedAttack(AttackKungFuType.QUANFA))
                .innateAttributesProvider(PlayerDefaultAttributes.INSTANCE)
                .yinYang(new YinYang())
                .life(PlayerLife.create())
                .arm(PlayerLife.create())
                .leg(PlayerLife.create())
                .head(PlayerLife.create())
                .innerPower(PlayerExperiencedAgedAttribute.createInnerPower())
                .outerPower(PlayerExperiencedAgedAttribute.createOuterPower())
                .power(PlayerExperiencedAgedAttribute.createPower())
                .pillSlots(new PillSlots())
                .inventory(new Inventory());
    }

    protected int nextId() {
        return id++;
    }

}
