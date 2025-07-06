package org.y1000.kungfu.attack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.TestingEventListener;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.OldPlayerStateEnum;
import org.y1000.entities.creatures.monster.AbstractMonsterUnitTestFixture;
import org.y1000.entities.creatures.monster.PassiveMonster;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.PlayerLife;
import org.y1000.entities.players.PlayerTestingAttribute;
import org.y1000.entities.players.event.OldPlayerAttackEvent;
import org.y1000.entities.players.event.PlayerAttackEventResponse;
import org.y1000.kungfu.TestingAttackKungFuParameters;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.input.ClientAttackEvent;
import org.y1000.message.serverevent.TextMessage;
import org.y1000.realm.Realm;
import org.y1000.util.Coordinate;

import static org.junit.jupiter.api.Assertions.*;

class MeleeAttackKungFuTest extends AbstractMonsterUnitTestFixture {

    /*
    private QuanfaKungFu kungFu;

    private PlayerImpl player;

    private ClientAttackEvent clientAttackEvent;

    private TestingEventListener playerEventListener;

    private Realm realm;

    private static class NoCostParameters implements AttackKungFuParameters {

        @Override
        public int powerToSwing() {
            return 0;
        }

        @Override
        public int innerPowerToSwing() {
            return 0;
        }

        @Override
        public int recovery() {
            return 0;
        }

        @Override
        public int outerPowerToSwing() {
            return 0;
        }

        @Override
        public int lifeToSwing() {
            return 0;
        }
    }

    private static class TwoCostParameters implements AttackKungFuParameters {

        @Override
        public int powerToSwing() {
            return 2;
        }

        @Override
        public int innerPowerToSwing() {
            return 2;
        }

        @Override
        public int recovery() {
            return 2;
        }

        @Override
        public int outerPowerToSwing() {
            return 2;
        }

        @Override
        public int lifeToSwing() {
            return 2;
        }
    }


    private QuanfaKungFu createKungFu(AttackKungFuParameters parameters) {
        return QuanfaKungFu.builder()
                .parameters(parameters)
                .name("test")
                .exp(0)
                .build();
    }


    @BeforeEach
    void setUp() {
        setup();
        realm = mockAllFlatRealm();
        kungFu = createKungFu(new NoCostParameters());
        player = playerBuilder()
                .attackKungFu(kungFu)
                .coordinate(new Coordinate(1, 1))
                .build();
        clientAttackEvent = new ClientAttackEvent(1L, 1L, kungFu.randomAttackState(), Direction.UP);
        playerEventListener = new TestingEventListener();
        player.registerEventListener(playerEventListener);
        player.joinRealm(realm, );
    }




    private PassiveMonster createMonster(Coordinate coordinate) {
        attributeProvider.life = 10000;
        return monsterBuilder().attributeProvider(attributeProvider).realmMap(realm.map()).coordinate(coordinate).build();
    }

    @Test
    void startAttack() {
        PassiveMonster monster = createMonster(player.coordinate().moveBy(clientAttackEvent.direction()));
        int actual = (70 + kungFu.attackSpeed()) * Realm.STEP_MILLIS;
        kungFu.startAttack(player, clientAttackEvent, monster);
        assertEquals(player.getEnemy(), monster);
        PlayerAttackEventResponse entityEvent = playerEventListener.removeFirst(PlayerAttackEventResponse.class);
        assertEquals(player.direction(), clientAttackEvent.direction());
        assertTrue(entityEvent.isAccepted());
        assertInstanceOf(PlayerAttackState.class, player.creatureState());
        assertEquals(actual, player.maxCooldown());
    }


    @Test
    void startAttack_noEffectWhenOutOfView() {
        var monster = createMonster(player.coordinate().move(0, Coordinate.VISIBLE_Y_RANGE + 1));
        kungFu.startAttack(player, clientAttackEvent, monster);
        assertNull(player.getEnemy());
        var entityEvent = playerEventListener.dequeue(PlayerAttackEventResponse.class);
        assertFalse(entityEvent.isAccepted());
        assertEquals(OldPlayerStateEnum.IDLE, player.oldStateEnum());
    }


    @Test
    void startAttack_changeTarget() {
        PassiveMonster monster = createMonster( player.coordinate().moveBy(clientAttackEvent.direction()));
        player.setEnemy(monster);
        player.cooldownAttack();

        PassiveMonster another = createMonster(player.coordinate().moveBy(clientAttackEvent.direction()));
        kungFu.startAttack(player, clientAttackEvent, another);
        assertEquals(player.getEnemy(), another);
        assertTrue(player.creatureState() instanceof PlayerCooldownState);
    }

    @Test
    void attackAgain() {
        System.out.println(kungFu.attackSpeed());
        PassiveMonster monster = createMonster(player.coordinate().moveBy(clientAttackEvent.direction()));
        player.setEnemy(monster);
        var expectedCooldown = (70 + kungFu.attackSpeed()) * Realm.STEP_MILLIS;
        kungFu.attackAgain(player);
        OldPlayerAttackEvent event = playerEventListener.removeFirst(OldPlayerAttackEvent.class);
        assertNotNull(event);
        assertEquals(expectedCooldown, player.maxCooldown());
        assertTrue(player.creatureState() instanceof PlayerAttackState);
        assertEquals(player.direction(), Direction.UP);
    }

    @Test
    void attackAgainWhenNoEnoughPower() {
        PassiveMonster monster = createMonster(player.coordinate().moveBy(clientAttackEvent.direction()));
        player.setEnemy(monster);
        int actual = (70 + kungFu.attackSpeed()) * Realm.STEP_MILLIS;
        kungFu.attackAgain(player);
        OldPlayerAttackEvent event = playerEventListener.removeFirst(OldPlayerAttackEvent.class);
        assertNotNull(event);
        assertEquals(actual, player.maxCooldown());
        assertTrue(player.creatureState() instanceof PlayerAttackState);
        assertEquals(player.direction(), Direction.UP);
    }

    @Test
    void attackAgain_whenTargetOutOfView() {
        PassiveMonster monster = createMonster( player.coordinate().move(Coordinate.VISIBLE_X_RANGE + 1, 0));
        player.setEnemy(monster);
        kungFu.attackAgain(player);
        assertSame(player.oldStateEnum(), OldPlayerStateEnum.FightStand);
        assertTrue(player.creatureState() instanceof PlayerStillState);
    }

    @Test
    void attackAgain_whenStillCooldown() {
        PassiveMonster monster = createMonster(player.coordinate().moveBy(clientAttackEvent.direction()));
        player.setEnemy(monster);
        player.cooldownAttack();
        kungFu.attackAgain(player);
        assertEquals(player.maxCooldown(), (70 + kungFu.attackSpeed()) * Realm.STEP_MILLIS);
        assertTrue(player.creatureState() instanceof PlayerCooldownState);
    }

    @Test
    void startAttackWhenNoPower() {
        player = playerBuilder().power(PlayerTestingAttribute.of(1))
                .life(new PlayerLife(10, 0)).innerPower(PlayerTestingAttribute.of(5))
                .outerPower(PlayerTestingAttribute.of(3)).build();
        player.registerEventListener(playerEventListener);
        player.joinRealm(realm, );
        kungFu = createKungFu(new TwoCostParameters());
        PassiveMonster monster = createMonster(player.coordinate().moveBy(clientAttackEvent.direction()));
        kungFu.startAttack(player, clientAttackEvent, monster);
        PlayerTextEvent event = playerEventListener.dequeue(PlayerTextEvent.class);
        assertEquals(TextMessage.TextType.NO_POWER.value(), event.toPacket().getText().getType());
        assertEquals(OldPlayerStateEnum.IDLE, player.oldStateEnum());
    }

    @Test
    void attackWhenNoLife() {
        player = playerBuilder().power(PlayerTestingAttribute.of(3)).life(new PlayerLife(1, 0))
                .innerPower(PlayerTestingAttribute.of(3)).outerPower(PlayerTestingAttribute.of(3)).build();
        player.registerEventListener(playerEventListener);
        player.joinRealm(realm, );
        var param = new TestingAttackKungFuParameters();
        kungFu = createKungFu(param.setLifeToString(103));
        PassiveMonster monster = createMonster(player.coordinate().moveBy(clientAttackEvent.direction()));
        kungFu.startAttack(player, clientAttackEvent, monster);
        PlayerTextEvent event = playerEventListener.removeFirst(PlayerTextEvent.class);
        assertEquals(TextMessage.TextType.NO_LIFE.value(), event.toPacket().getText().getType());
    }

    @Test
    void usePower() {
        player = playerBuilder().power(PlayerTestingAttribute.of(3)).life(new PlayerLife(3, 0))
                .innerPower(PlayerTestingAttribute.of(3)).outerPower(PlayerTestingAttribute.of(3)).build();
        player.joinRealm(realm, );
        player.registerEventListener(playerEventListener);
        kungFu = createKungFu(new TwoCostParameters());
        PassiveMonster monster = createMonster(player.coordinate().moveBy(clientAttackEvent.direction()));
        kungFu.startAttack(player, clientAttackEvent, monster);
        assertEquals(1, player.power());
        assertEquals(101, player.currentLife());
        assertEquals(1, player.innerPower());
        assertEquals(1, player.outerPower());
    }

    @Test
    void bodyDamage() {
        kungFu = createKungFu(new TestingAttackKungFuParameters(207));
        assertEquals(211, kungFu.bodyDamage());
        //99.98
        kungFu = QuanfaKungFu.builder().name("无名刀法").parameters(new TestingAttackKungFuParameters(207)).exp(1084540874).build();
        assertEquals(775, kungFu.bodyDamage());
        // 99.99
        var spearKungFu = SpearKungFu.builder().name("无名枪术").parameters(new TestingAttackKungFuParameters(230)).exp(1083942874).build();
        assertEquals(826, spearKungFu.bodyDamage());
    }

    @Test
    void recoveryAndAvoid() {
        kungFu = createKungFu(new TestingAttackKungFuParameters(207).setRecovery(100).setAvoidance(101));
        assertEquals(100, kungFu.recovery());
        assertEquals(101, kungFu.avoidance());
    }

    @Test
    void attackSpeed() {
        kungFu = createKungFu(new TestingAttackKungFuParameters(207).setAttackSpeed(80));
        assertEquals(80, kungFu.attackSpeed());
        // 38.74
        var sword = SwordKungFu.builder().parameters(new TestingAttackKungFuParameters().setAttackSpeed(60)).exp(1365874).build();
        assertEquals(51, sword.attackSpeed());
        // 99.99
        sword = SwordKungFu.builder().parameters(new TestingAttackKungFuParameters().setAttackSpeed(60)).exp(1084540874).build();
        assertEquals(37, sword.attackSpeed());
    }


    @Test
    void halDamage() {
        var blade = BladeKungFu.builder().parameters(new TestingAttackKungFuParameters().setHeadDamage(136).setArmDamage(136).setLegDamage(136)).exp(0)
                .build();
        assertEquals(138, blade.legDamage());
        assertEquals(138, blade.armDamage());
        assertEquals(138, blade.headDamage());
    }

    @Test
    void name() {
        var kungFuFactory = createKungFuFactory();
        var kf = kungFuFactory.createAttackKungFu("无击阵");
        while (kf.level() < 8000) {
            kf.gainPermittedExp(1000);
        }
        System.out.println(kf.exp());
        System.out.println(kf.damage());
    }*/
}
