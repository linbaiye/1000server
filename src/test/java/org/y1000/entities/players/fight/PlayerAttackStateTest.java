package org.y1000.entities.players.fight;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.TestingPlayerEventListener;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.CreatureShootEvent;
import org.y1000.entities.creatures.monster.PassiveMonster;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.kungfu.attack.AttackKungFuType;
import org.y1000.kungfu.attack.QuanfaKungFu;
import org.y1000.message.InputResponseMessage;
import org.y1000.message.clientevent.ClientMovementEvent;
import org.y1000.message.clientevent.input.RightMouseClick;
import org.y1000.realm.Realm;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import static org.junit.jupiter.api.Assertions.*;

class PlayerAttackStateTest extends AbstractUnitTestFixture {
    private PlayerImpl player;

    private TestingPlayerEventListener eventListener;

    @BeforeEach
    public void setup() {
        player = playerBuilder().build();
        eventListener = new TestingPlayerEventListener();
        player.registerOrderedEventListener(eventListener);
    }

    @Test
    void hurtRelated() {
        var attackState = PlayerAttackState.of(player);
        assertEquals(State.COOLDOWN, attackState.decideAfterHurtState());
        attackState.afterHurt(player);
        assertTrue(player.state() instanceof PlayerCooldownState);
        var before = player.coordinate();
        attackState.moveToHurtCoordinate(player);
        assertEquals(before, player.coordinate());
    }

    @Test
    void considerAttackSpeed() {
        player = playerBuilder().attackKungFu(QuanfaKungFu.builder().attackSpeed(-60).name("test").level(100).build()).build();
        var state = PlayerAttackState.of(player);
        assertEquals(100, state.totalMillis());
        player = playerBuilder().attackKungFu(QuanfaKungFu.builder().attackSpeed(160).name("test").level(100).build()).build();
        state = PlayerAttackState.of(player);
        assertEquals(player.getStateMillis(state.stateEnum()), state.totalMillis());
    }

    @Test
    void emitProjectileEvent() {
        TestingPlayerEventListener eventListener = new TestingPlayerEventListener();
        player = playerBuilder().attackKungFu(kungFuBookFactory.create().findUnnamedAttack(AttackKungFuType.BOW)).build();
        player.setFightingEntity(new PassiveMonster(1L, new Coordinate(2, 2), Direction.UP, "test", Mockito.mock(RealmMap.class)));
        player.registerOrderedEventListener(eventListener);
        var state = PlayerAttackState.of(player);
        state.update(player, state.totalMillis());
        CreatureShootEvent dequeue = eventListener.dequeue(CreatureShootEvent.class);
        assertNotNull(dequeue);
    }

    @Test
    void move() {
        Realm realm = mockAllFlatRealm();
        player.joinReam(realm);
        var state = PlayerAttackState.of(player);
        player.changeState(state);
        eventListener.clearEvents();
        state.move(player, new ClientMovementEvent(new RightMouseClick(1, Direction.RIGHT), player.coordinate()));
        assertTrue(player.state() instanceof PlayerFightWalkState);
        InputResponseMessage event = eventListener.dequeue(InputResponseMessage.class);
        assertSame(event.source(), player);
        assertEquals(event.toPacket().getResponsePacket().getPositionPacket().getDirection(), Direction.RIGHT.value());
    }

    @Test
    void doables() {
        var state = PlayerAttackState.of(player);
        assertTrue(state.attackable());
        assertTrue(state.canSitDown());
        assertFalse(state.canStandUp());
        assertTrue(state.canUseFootKungFu());
    }

    @Test
    void rewindMove() {

    }
}