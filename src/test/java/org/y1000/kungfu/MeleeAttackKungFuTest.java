package org.y1000.kungfu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.TestingAttackKungFuParameters;
import org.y1000.TestingEventListener;
import org.y1000.entities.Direction;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.monster.AbstractMonsterUnitTestFixture;
import org.y1000.entities.creatures.monster.PassiveMonster;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.PlayerStillState;
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

    private static class NoCostParameters implements AttackKungFuFixedParameters {

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

    private static class TwoCostParameters implements AttackKungFuFixedParameters {

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


    private QuanfaKungFu createKungFu(AttackKungFuFixedParameters parameters) {
        return QuanfaKungFu.builder()
                .parameters(parameters)
                .name("test")
                .level(100)
                .build();
    }


    @BeforeEach
    void setUp() {
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
        return monsterBuilder().coordinate(coordinate).build();
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
    void startAttackAssistantEnabled() {
        PassiveMonster monster =  createMonster(player.coordinate().moveBy(clientAttackEvent.direction()));
        player.kungFuBook().addToBasic(AssistantKungFu.builder().name("test").level(100).eightDirection(true).build());
        player.handleClientEvent(new ClientToggleKungFuEvent(2, 1));
        kungFu.startAttack(player, clientAttackEvent, monster);
        assertEquals(player.getFightingEntity(), monster);
        PlayerAttackEventResponse entityEvent = playerEventListener.removeFirst(PlayerAttackEventResponse.class);
        assertTrue(entityEvent.isAccepted());
        var aoeEvent = playerEventListener.removeFirst(PlayerAttackAoeEvent.class);
        assertNotNull(aoeEvent);
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
    void attackWhenNoPower() {
        player = playerBuilder().power(1).innateLife(3).innerPower(3).outerPower(3).build();
        player.registerEventListener(playerEventListener);
        kungFu = createKungFu(new TwoCostParameters());
        PassiveMonster monster = createMonster(player.coordinate().moveBy(clientAttackEvent.direction()));
        kungFu.startAttack(player, clientAttackEvent, monster);
        PlayerTextEvent event = playerEventListener.dequeue(PlayerTextEvent.class);
        assertEquals(PlayerTextEvent.TextType.NO_POWER.value(), event.toPacket().getText().getType());
        assertInstanceOf(PlayerCooldownState.class, player.state());
    }

    @Test
    void attackWhenNoLife() {
        player = playerBuilder().power(3).innateLife(1).innerPower(3).outerPower(3).build();
        player.registerEventListener(playerEventListener);
        kungFu = createKungFu(new TwoCostParameters());
        PassiveMonster monster = createMonster(player.coordinate().moveBy(clientAttackEvent.direction()));
        kungFu.startAttack(player, clientAttackEvent, monster);
        PlayerTextEvent event = playerEventListener.dequeue(PlayerTextEvent.class);
        assertEquals(PlayerTextEvent.TextType.NO_LIFE.value(), event.toPacket().getText().getType());
    }

    @Test
    void usePower() {
        player = playerBuilder().power(3).innateLife(3).innerPower(3).outerPower(3).build();
        player.registerEventListener(playerEventListener);
        kungFu = createKungFu(new TwoCostParameters());
        PassiveMonster monster = createMonster(player.coordinate().moveBy(clientAttackEvent.direction()));
        kungFu.startAttack(player, clientAttackEvent, monster);
        assertEquals(1, player.power());
        assertEquals(1, player.currentLife());
        assertEquals(1, player.innerPower());
        assertEquals(1, player.outerPower());
    }

    @Test
    void bodyDamage() {
        kungFu = createKungFu(new TestingAttackKungFuParameters(207));
        assertEquals(211, kungFu.bodyDamage());
        kungFu = QuanfaKungFu.builder().name("无名刀法").parameters(new TestingAttackKungFuParameters(207)).level(9999).build();
        assertEquals(775, kungFu.bodyDamage());
        var spearKungFu = SpearKungFu.builder().name("无名枪术").parameters(new TestingAttackKungFuParameters(230)).level(9998).build();
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
        var sword = SwordKungFu.builder().parameters(new TestingAttackKungFuParameters().setAttackSpeed(60)).level(3872).build();
        assertEquals(51, sword.attackSpeed());
        sword = SwordKungFu.builder().parameters(new TestingAttackKungFuParameters().setAttackSpeed(60)).level(9999).build();
        assertEquals(37, sword.attackSpeed());
    }

    @Test
    void halDamage() {
        var blade = BladeKungFu.builder().parameters(new TestingAttackKungFuParameters().setHeadDamage(136).setArmDamage(136).setLegDamage(136)).level(100)
                .build();
        assertEquals(138, blade.legDamage());
        assertEquals(138, blade.armDamage());
        assertEquals(138, blade.headDamage());
    }
}
