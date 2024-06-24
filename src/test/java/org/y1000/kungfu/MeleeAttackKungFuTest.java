package org.y1000.kungfu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.TestingEventListener;
import org.y1000.entities.Direction;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.monster.AbstractMonsterUnitTestFixture;
import org.y1000.entities.creatures.monster.PassiveMonster;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.PlayerLife;
import org.y1000.entities.players.PlayerStillState;
import org.y1000.entities.players.PlayerTestingAttribute;
import org.y1000.entities.players.event.PlayerAttackAoeEvent;
import org.y1000.entities.players.event.PlayerAttackEvent;
import org.y1000.entities.players.event.PlayerAttackEventResponse;
import org.y1000.entities.players.fight.PlayerAttackState;
import org.y1000.entities.players.fight.PlayerCooldownState;
import org.y1000.kungfu.attack.*;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.clientevent.ClientAttackEvent;
import org.y1000.message.clientevent.ClientToggleKungFuEvent;
import org.y1000.realm.Realm;
import org.y1000.repository.KungFuBookRepositoryImpl;
import org.y1000.util.Coordinate;

import static org.junit.jupiter.api.Assertions.*;

class MeleeAttackKungFuTest extends AbstractMonsterUnitTestFixture {

    private QuanfaKungFu kungFu;

    private PlayerImpl player;

    private final KungFuBookFactory kungFuBookFactory = new KungFuBookRepositoryImpl();

    private ClientAttackEvent clientAttackEvent;

    private TestingEventListener playerEventListener;

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
        kungFu = createKungFu(new NoCostParameters());
        player = playerBuilder()
                .attackKungFu(kungFu)
                .coordinate(new Coordinate(1, 1))
                .build();
        clientAttackEvent = new ClientAttackEvent(1L, 1L, kungFu.randomAttackState(), Direction.UP);
        playerEventListener = new TestingEventListener();
        player.registerEventListener(playerEventListener);
    }




    private PassiveMonster createMonster(Coordinate coordinate) {
        attributeProvider.life = 10000;
        return monsterBuilder().attributeProvider(attributeProvider).coordinate(coordinate).build();
    }

    @Test
    void startAttack() {
        PassiveMonster monster = createMonster(player.coordinate().moveBy(clientAttackEvent.direction()));
        kungFu.startAttack(player, clientAttackEvent, monster);
        assertEquals(player.getFightingEntity(), monster);
        PlayerAttackEventResponse entityEvent = playerEventListener.removeFirst(PlayerAttackEventResponse.class);
        assertEquals(player.direction(), clientAttackEvent.direction());
        assertTrue(entityEvent.isAccepted());
        assertInstanceOf(PlayerAttackState.class, player.state());
        assertEquals(player.cooldown(), (70 + kungFu.attackSpeed()) * Realm.STEP_MILLIS);
    }


    @Test
    void startAttack_noEffectWhenOutOfView() {
        var monster = createMonster(player.coordinate().move(0, Entity.VISIBLE_Y_RANGE + 1));
        kungFu.startAttack(player, clientAttackEvent, monster);
        assertNull(player.getFightingEntity());
        var entityEvent = playerEventListener.dequeue(PlayerAttackEventResponse.class);
        assertFalse(entityEvent.isAccepted());
        assertEquals(State.IDLE, player.stateEnum());
    }


    @Test
    void startAttack_changeTarget() {
        PassiveMonster monster = createMonster( player.coordinate().moveBy(clientAttackEvent.direction()));
        player.setFightingEntity(monster);
        player.cooldownAttack();

        PassiveMonster another = createMonster(player.coordinate().moveBy(clientAttackEvent.direction()));
        kungFu.startAttack(player, clientAttackEvent, another);
        assertEquals(player.getFightingEntity(), another);
        assertTrue(player.state() instanceof PlayerCooldownState);
    }

    @Test
    void attackAgain() {
        PassiveMonster monster = createMonster(player.coordinate().moveBy(clientAttackEvent.direction()));
        player.setFightingEntity(monster);
        kungFu.attackAgain(player);
        PlayerAttackEvent event = playerEventListener.removeFirst(PlayerAttackEvent.class);
        assertNotNull(event);
        assertEquals(player.cooldown(), (70 + kungFu.attackSpeed()) * Realm.STEP_MILLIS);
        assertTrue(player.state() instanceof PlayerAttackState);
        assertEquals(player.direction(), Direction.UP);
    }

    @Test
    void attackAgainWhenNoEnoughPower() {
        PassiveMonster monster = createMonster(player.coordinate().moveBy(clientAttackEvent.direction()));
        player.setFightingEntity(monster);
        kungFu.attackAgain(player);
        PlayerAttackEvent event = playerEventListener.removeFirst(PlayerAttackEvent.class);
        assertNotNull(event);
        assertEquals(player.cooldown(), (70 + kungFu.attackSpeed()) * Realm.STEP_MILLIS);
        assertTrue(player.state() instanceof PlayerAttackState);
        assertEquals(player.direction(), Direction.UP);
    }

    @Test
    void attackAgain_whenTargetOutOfView() {
        PassiveMonster monster = createMonster( player.coordinate().move(Entity.VISIBLE_X_RANGE + 1, 0));
        player.setFightingEntity(monster);
        kungFu.attackAgain(player);
        assertSame(player.stateEnum(), State.COOLDOWN);
        assertTrue(player.state() instanceof PlayerStillState);
    }

    @Test
    void attackAgain_whenStillCooldown() {
        PassiveMonster monster = createMonster(player.coordinate().moveBy(clientAttackEvent.direction()));
        player.setFightingEntity(monster);
        player.cooldownAttack();
        kungFu.attackAgain(player);
        assertEquals(player.cooldown(), (70 + kungFu.attackSpeed()) * Realm.STEP_MILLIS);
        assertTrue(player.state() instanceof PlayerCooldownState);
    }

    @Test
    void startAttackWhenNoPower() {
        player = playerBuilder().power(PlayerTestingAttribute.of(1))
                .life(new PlayerLife(10, 0)).innerPower(PlayerTestingAttribute.of(5))
                .outerPower(PlayerTestingAttribute.of(3)).build();
        player.registerEventListener(playerEventListener);
        kungFu = createKungFu(new TwoCostParameters());
        PassiveMonster monster = createMonster(player.coordinate().moveBy(clientAttackEvent.direction()));
        kungFu.startAttack(player, clientAttackEvent, monster);
        PlayerTextEvent event = playerEventListener.dequeue(PlayerTextEvent.class);
        assertEquals(PlayerTextEvent.TextType.NO_POWER.value(), event.toPacket().getText().getType());
        assertEquals(State.IDLE, player.stateEnum());
    }

    @Test
    void attackWhenNoLife() {
        player = playerBuilder().power(PlayerTestingAttribute.of(3)).life(new PlayerLife(1, 0))
                .innerPower(PlayerTestingAttribute.of(3)).outerPower(PlayerTestingAttribute.of(3)).build();
        player.registerEventListener(playerEventListener);
        var param = new TestingAttackKungFuParameters();
        kungFu = createKungFu(param.setLifeToString(103));
        PassiveMonster monster = createMonster(player.coordinate().moveBy(clientAttackEvent.direction()));
        kungFu.startAttack(player, clientAttackEvent, monster);
        PlayerTextEvent event = playerEventListener.removeFirst(PlayerTextEvent.class);
        assertEquals(PlayerTextEvent.TextType.NO_LIFE.value(), event.toPacket().getText().getType());
    }

    @Test
    void usePower() {
        player = playerBuilder().power(PlayerTestingAttribute.of(3)).life(new PlayerLife(3, 0))
                .innerPower(PlayerTestingAttribute.of(3)).outerPower(PlayerTestingAttribute.of(3)).build();
        player.registerEventListener(playerEventListener);
        var param = new TestingAttackKungFuParameters();
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
}
