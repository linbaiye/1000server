package org.y1000.entities.creatures.monster;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.TestingEventListener;
import org.y1000.entities.Direction;
import org.y1000.entities.Projectile;
import org.y1000.entities.attribute.Damage;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.CreatureHurtEvent;
import org.y1000.entities.creatures.monster.fight.MonsterFightCooldownState;
import org.y1000.entities.creatures.monster.fight.MonsterFightIdleState;
import org.y1000.entities.players.Player;
import org.y1000.kungfu.KungFuFactory;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.realm.Realm;
import org.y1000.realm.RealmMap;
import org.y1000.repository.KungFuBookRepositoryImpl;
import org.y1000.sdb.ActionSdb;
import org.y1000.util.Coordinate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.y1000.sdb.*;

class PassiveMonsterTest extends AbstractMonsterUnitTestFixture{

    private TestingEventListener listener;

    private PassiveMonster monster;

    private Player player;

    @BeforeEach
    void setUp() {
        listener = new TestingEventListener();
        monster = monsterBuilder().coordinate(Coordinate.xy(1, 1)).build();
        monster.registerEventListener(listener);
        player = playerBuilder().build();
    }

    @Test
    void getHurt() {
        monster.attackedBy(player);
        assertEquals(player, monster.getFightingEntity());
        assertSame(monster.stateEnum(), State.HURT);
        assertNotNull(listener.dequeue(CreatureHurtEvent.class));
        monster.update(monster.getStateMillis(State.HURT));
        assertTrue(monster.state() instanceof MonsterFightIdleState);
    }

    @Test
    void getHurtChangeTarget() {
        player = playerBuilder().coordinate(monster.coordinate().move(2, 2)).build();
        Projectile projectile = Projectile.builder().target(monster).shooter(player).flyingMillis(10).build();
        monster.attackedBy(projectile);
        assertEquals(player, monster.getFightingEntity());
        var player1 = playerBuilder().coordinate(monster.coordinate().moveBy(Direction.LEFT)).build();
        monster.attackedBy(player1);
        assertEquals(player1, monster.getFightingEntity());
    }

    @Test
    void getHurtShouldNotChangeTargetIfAdjacent() {
        player = playerBuilder().coordinate(monster.coordinate().moveBy(Direction.UP)).build();
        monster.attackedBy(player);
        assertEquals(player, monster.getFightingEntity());
        var player1 = playerBuilder().coordinate(monster.coordinate().moveBy(Direction.LEFT)).build();
        monster.attackedBy(player1);
        assertEquals(player, monster.getFightingEntity());
    }

    @Test
    void fightingTargetLeft() {
        monster.attackedBy(player);
        player.leaveRealm();
        assertNull(monster.getFightingEntity());
        monster.update(monster.getStateMillis(State.HURT));
        assertEquals(monster.stateEnum(), State.IDLE);
    }

    @Test
    void attackCooldown() {
        monster.setFightingEntity(playerBuilder().coordinate(monster.coordinate().move(1, 0)).build());
        monster.changeState(MonsterFightCooldownState.cooldown(10));
        monster.update(10);
        assertEquals(Realm.STEP_MILLIS * monster.attackSpeed(), monster.cooldown());
    }

    @Test
    void givePlayerExp() {
        Player attacker = Mockito.mock(Player.class);
        monster = monsterBuilder().life(10000).avoidance(0).armor(1).build();
        when(attacker.damage()).thenReturn(new Damage(100, 100, 100, 100));
        monster.attackedBy(attacker);
        verify(attacker).gainAttackExp(10000);

        attacker = Mockito.mock(Player.class);
        monster = monsterBuilder().life(100).avoidance(0).armor(0).build();
        when(attacker.damage()).thenReturn(new Damage(100, 100, 100, 100));
        monster.attackedBy(attacker);
        verify(attacker).gainAttackExp(44);
    }

//    @Test
//    void name() {
//        MonsterFactoryImpl monsterFactory = new MonsterFactoryImpl(ActionSdb.INSTANCE, MonsterSdb.INSTANCE);
//        RealmMap mock = Mockito.mock(RealmMap.class);
//        AbstractMonster monster = monsterFactory.createMonster("狗", 1L, mock, Coordinate.xy(1, 1));
//        KungFuFactory kungFuFactory = new KungFuBookRepositoryImpl();
//        AttackKungFu kungFu = kungFuFactory.createAttackKungFu("无名刀法");
//        //159, 213, 262, 309.
//        int time = 1;
//        for (int i = 0; i < time; i++) {
//            var damagedLife = Math.max(kungFu.bodyDamage() - monster.bodyArmor(), 1);
//            var n = monster.maxLife() / damagedLife;
//            var DEFAULT_EXP = 10000;
//            var exp = n > 15 ? DEFAULT_EXP : DEFAULT_EXP * n * n / 15 * 15;
//            kungFu.gainExp(exp);
//            if (i == time - 1) {
//                System.out.println(kungFu.level());
//                System.out.println(kungFu.bodyDamage());
//            }
//        }
//    }
}