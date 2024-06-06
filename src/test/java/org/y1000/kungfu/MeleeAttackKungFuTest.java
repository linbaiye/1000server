package org.y1000.kungfu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.TestingPlayerEventListener;
import org.y1000.entities.Direction;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.monster.PassiveMonster;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.PlayerStillState;
import org.y1000.entities.players.event.PlayerAttackAoeEvent;
import org.y1000.entities.players.event.PlayerAttackEvent;
import org.y1000.entities.players.event.PlayerAttackEventResponse;
import org.y1000.entities.players.fight.PlayerAttackState;
import org.y1000.entities.players.fight.PlayerCooldownState;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.kungfu.attack.QuanfaKungFu;
import org.y1000.message.clientevent.ClientAttackEvent;
import org.y1000.message.clientevent.ClientToggleKungFuEvent;
import org.y1000.realm.Realm;
import org.y1000.realm.RealmMap;
import org.y1000.repository.KungFuBookRepositoryImpl;
import org.y1000.util.Coordinate;

import static org.junit.jupiter.api.Assertions.*;

class MeleeAttackKungFuTest {

    private QuanfaKungFu kungFu;

    private PlayerImpl player;

    private final KungFuBookFactory kungFuBookFactory = new KungFuBookRepositoryImpl();

    private ClientAttackEvent clientAttackEvent;

    private TestingPlayerEventListener playerEventListener;


    @BeforeEach
    void setUp() {
        kungFu = QuanfaKungFu.builder()
                .attackSpeed(100)
                .recovery(100)
                .bodyArmor(1)
                .bodyDamage(1)
                .level(100)
                .name("test")
                .headArmor(1)
                .headDamage(1)
                .armArmor(1)
                .armDamage(1)
                .legArmor(1)
                .legDamage(1)
                .build();
        player = PlayerImpl.builder()
                .id(1L)
                .name("test")
                .kungFuBook(kungFuBookFactory.create())
                .inventory(new Inventory())
                .attackKungFu(kungFu)
                .coordinate(new Coordinate(1, 1))
                .build();
        clientAttackEvent = new ClientAttackEvent(1L, 1L, kungFu.randomAttackState(), Direction.UP);
        playerEventListener = new TestingPlayerEventListener();
        player.registerOrderedEventListener(playerEventListener);
    }


    @Test
    void startAttack() {
        PassiveMonster monster = new PassiveMonster(1L, player.coordinate().moveBy(clientAttackEvent.direction()), Direction.UP, "test", Mockito.mock(RealmMap.class));
        kungFu.startAttack(player, clientAttackEvent, monster);
        assertEquals(player.getFightingEntity(), monster);
        PlayerAttackEventResponse entityEvent = playerEventListener.dequeue(PlayerAttackEventResponse.class);
        assertEquals(player.direction(), clientAttackEvent.direction());
        assertTrue(entityEvent.isAccepted());
        assertTrue(player.state() instanceof PlayerAttackState);
        assertEquals(player.cooldown(), (70 + kungFu.getAttackSpeed()) * Realm.STEP_MILLIS);
    }

    @Test
    void startAttackAssistantEnabled() {
        PassiveMonster monster = new PassiveMonster(1L, player.coordinate().moveBy(clientAttackEvent.direction()), Direction.UP, "test", Mockito.mock(RealmMap.class));
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
        var monster = new PassiveMonster(1L, player.coordinate().move(0, Entity.VISIBLE_Y_RANGE + 1), Direction.UP, "test", Mockito.mock(RealmMap.class));
        kungFu.startAttack(player, clientAttackEvent, monster);
        assertNull(player.getFightingEntity());
        var entityEvent = playerEventListener.dequeue(PlayerAttackEventResponse.class);
        assertFalse(entityEvent.isAccepted());
        assertEquals(State.IDLE, player.stateEnum());
    }


    @Test
    void startAttack_changeTarget() {
        PassiveMonster monster = new PassiveMonster(1L, player.coordinate().moveBy(clientAttackEvent.direction()), Direction.UP, "test", Mockito.mock(RealmMap.class));
        player.setFightingEntity(monster);
        player.cooldownAttack();

        PassiveMonster another = new PassiveMonster(1L, player.coordinate().moveBy(clientAttackEvent.direction()), Direction.UP, "test", Mockito.mock(RealmMap.class));
        kungFu.startAttack(player, clientAttackEvent, another);
        assertEquals(player.getFightingEntity(), another);
        assertTrue(player.state() instanceof PlayerCooldownState);
    }

    @Test
    void attackAgain() {
        PassiveMonster monster = new PassiveMonster(1L, player.coordinate().moveBy(clientAttackEvent.direction()), Direction.UP, "test", Mockito.mock(RealmMap.class));
        player.setFightingEntity(monster);
        kungFu.attackAgain(player);
        PlayerAttackEvent event = playerEventListener.dequeue(PlayerAttackEvent.class);
        assertNotNull(event);
        assertEquals(player.cooldown(), (70 + kungFu.getAttackSpeed()) * Realm.STEP_MILLIS);
        assertTrue(player.state() instanceof PlayerAttackState);
        assertEquals(player.direction(), Direction.UP);
    }

    @Test
    void attackAgain_whenTargetOutOfView() {
        PassiveMonster monster = new PassiveMonster(1L, player.coordinate().move(Entity.VISIBLE_X_RANGE + 1, 0), Direction.UP, "test", Mockito.mock(RealmMap.class));
        player.setFightingEntity(monster);
        kungFu.attackAgain(player);
        assertSame(player.stateEnum(), State.COOLDOWN);
        assertTrue(player.state() instanceof PlayerStillState);
    }

    @Test
    void attackAgain_whenStillCooldown() {
        PassiveMonster monster = new PassiveMonster(1L, player.coordinate().moveBy(clientAttackEvent.direction()), Direction.UP, "test", Mockito.mock(RealmMap.class));
        player.setFightingEntity(monster);
        player.cooldownAttack();
        kungFu.attackAgain(player);
        assertEquals(player.cooldown(), (70 + kungFu.getAttackSpeed()) * Realm.STEP_MILLIS);
        assertTrue(player.state() instanceof PlayerCooldownState);
    }
}
