package org.y1000.entities.creatures.monster;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.creatures.event.EntitySoundEvent;
import org.y1000.entities.projectile.PlayerProjectile;
import org.y1000.entities.attribute.Damage;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.CreatureHurtEvent;
import org.y1000.entities.players.Player;
import org.y1000.util.Coordinate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PassiveMonsterTest extends AbstractMonsterUnitTestFixture {

    private Player player;

    @BeforeEach
    void setUp() {
        setup();
        player = playerBuilder().build();
    }

    @Test
    void getHurt() {
        monster.attackedBy(player);
        assertSame(monster.stateEnum(), State.HURT);
        assertNotNull(eventListener.dequeue(CreatureHurtEvent.class));
        assertNotNull(eventListener.dequeue(EntitySoundEvent.class));
    }
//
//    @Test
//    void getHurtChangeTarget() {
//        player = playerBuilder().coordinate(monster.coordinate().move(2, 2)).build();
//        PlayerProjectile projectile = PlayerProjectile.builder().target(monster).shooter(player).damage(new Damage(1, 1,1,1)).build();
//        monster.attackedBy(projectile);
//        assertEquals(player, monster.getFightingEntity());
//        var player1 = playerBuilder().coordinate(monster.coordinate().moveBy(Direction.LEFT)).build();
//        monster.attackedBy(player1);
//        assertEquals(player1, monster.getFightingEntity());
//    }
//
//    @Test
//    void getHurtShouldNotChangeTargetIfAdjacent() {
//        player = playerBuilder().coordinate(monster.coordinate().moveBy(Direction.UP)).build();
//        monster.attackedBy(player);
//        assertEquals(player, monster.getFightingEntity());
//        var player1 = playerBuilder().coordinate(monster.coordinate().moveBy(Direction.LEFT)).build();
//        monster.attackedBy(player1);
//        assertEquals(player, monster.getFightingEntity());
//    }
//
//    @Test
//    void fightingTargetLeft() {
//        monster.attackedBy(player);
//        player.leaveRealm();
//        assertNull(monster.getFightingEntity());
//        monster.update(monster.getStateMillis(State.HURT));
//        assertEquals(monster.stateEnum(), State.IDLE);
//    }
//
//    @Test
//    void attackCooldown() {
//        monster.setFightingEntity(playerBuilder().coordinate(monster.coordinate().move(1, 0)).build());
//        monster.cooldownAttack();
//        assertEquals(Realm.STEP_MILLIS * monster.attackSpeed(), monster.cooldown());
//    }

    @Test
    void givePlayerExp() {
        Player attacker = Mockito.mock(Player.class);
        attributeProvider.life = 10000;
        attributeProvider.armor = 1;
        monster = monsterBuilder().attributeProvider(attributeProvider).build();
        when(attacker.damage()).thenReturn(new Damage(100, 100, 100, 100));
        monster.attackedBy(attacker);
        verify(attacker).gainAttackExp(10000);

        attacker = Mockito.mock(Player.class);
        attributeProvider.life = 100;
        attributeProvider.armor = 0;
        monster = monsterBuilder().attributeProvider(attributeProvider).build();
        when(attacker.damage()).thenReturn(new Damage(100, 100, 100, 100));
        monster.attackedBy(attacker);
        verify(attacker).gainAttackExp(44);
    }

    @Test
    void givePlayerRangedExp() {
        Player attacker = Mockito.mock(Player.class);
        when(attacker.coordinate()).thenReturn(Coordinate.xy(5, 5));
        attributeProvider.life = 10000;
        attributeProvider.armor = 1;
        monster = monsterBuilder().attributeProvider(attributeProvider).build();
        PlayerProjectile projectile = new PlayerProjectile(attacker, monster,  new Damage(1000, 1,1,1), 100);
        monster.attackedBy(projectile);
        verify(attacker).gainRangedAttackExp(any(Integer.class));
    }

    @Test
    void revive() {
        attributeProvider.life = 1000;
        monster = monsterBuilder().attributeProvider(attributeProvider).build();
        Player attacker = Mockito.mock(Player.class);
        when(attacker.damage()).thenReturn(new Damage(10000, 100, 100, 100));
        monster.attackedBy(attacker);
        assertEquals(0, monster.currentLife());
        assertEquals(State.DIE, monster.stateEnum());
        monster.revive(monster.coordinate());
        assertEquals(1000, monster.currentLife());
        assertEquals(State.IDLE, monster.stateEnum());
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