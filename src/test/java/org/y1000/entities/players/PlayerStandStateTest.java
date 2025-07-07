package org.y1000.entities.players;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.entities.Direction;
import org.y1000.entities.players.event.PlayerMoveEvent;
import org.y1000.entities.players.event.PlayerSetPositionAndStateEvent;
import org.y1000.item.ItemFactory;
import org.y1000.kungfu.attack.AttackKungFuType;
import org.y1000.message.PlayerChangeStateEvent;
import org.y1000.message.input.MoveInput;
import org.y1000.util.Coordinate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class PlayerStandStateTest extends AbstractPlayerUnitTestFixture {

    private PlayerStandState state;

    private final ItemFactory itemFactory = createItemFactory();

    @BeforeEach
    void setUp() {
        super.setup();
        changeToIdle();
    }

    private void changeToIdle() {
        state = PlayerStandState.idle(player);
        player.changeState(state);
    }

    private void changeToFightStand() {
        state = PlayerStandState.fightStand(player);
        player.changeState(state);
    }

    @Test
    void idleMove_whenCoordinateMismatch() {
        MoveInput moveInput = new MoveInput(player.coordinate().move(1, 1), Direction.DOWN);
        state.tryMove(moveInput);
        assertNotNull(eventListener.removeFirst(PlayerSetPositionAndStateEvent.class));
    }

    @Test
    void idleMove_whenCoordinateNotMovable() {
        MoveInput moveInput = new MoveInput(player.coordinate(), player.direction().opposite());
        when(mockedRealm.map().movable(any(Coordinate.class))).thenReturn(false);
        state.tryMove(moveInput);
        assertEquals(player.direction(), moveInput.direction());
        assertNotNull(eventListener.removeFirst(PlayerSetPositionAndStateEvent.class));
    }

    @Test
    void idleMove_whenOk() {
        MoveInput moveInput = new MoveInput(player.coordinate(), Direction.DOWN);
        when(mockedRealm.map().movable(any(Coordinate.class))).thenReturn(true);
        state.tryMove(moveInput);
        assertNotNull(eventListener.removeFirst(PlayerMoveEvent.class));
        assertSame(player.stateEnum(), PlayerStateEnum.Move);
    }

    @Test
    void idleStateEnum() {
        changeToIdle();
        assertSame(state.playerStateEnum(), PlayerStateEnum.Idle);
    }

    @Test
    void fightWalkMove() {
        changeToFightStand();
        MoveInput moveInput = new MoveInput(player.coordinate(), Direction.DOWN);
        when(mockedRealm.map().movable(any(Coordinate.class))).thenReturn(true);
        state.tryMove(moveInput);
        assertNotNull(eventListener.removeFirst(PlayerMoveEvent.class));
        assertSame(player.stateEnum(), PlayerStateEnum.Move);
    }

    @Test
    void sayHello_whenIdle() {
        state.sayHello();
        assertEquals(player.stateEnum(), PlayerStateEnum.Hello);
        PlayerChangeStateEvent event = eventListener.removeFirst(PlayerChangeStateEvent.class);
        assertTrue(event.isIncludeSelf());
    }

    @Test
    void sayHello_whenFightStand() {
        changeToFightStand();
        state.sayHello();
        assertEquals(player.stateEnum(), PlayerStateEnum.FightStand);
    }

    @Test
    void tryUseAttackKungFu() {
        player.inventory().put(itemFactory.createEquipment("长剑"));
        state.tryToggleAttackKungFu(player.kungFuBook().findUnnamedAttack(AttackKungFuType.SWORD));
        assertEquals(player.kungFuBook().findUnnamedAttack(AttackKungFuType.SWORD), player.attackKungFu());
    }

    @Test
    void switchToFightStand() {
        player.toggleFootKungFu(player.kungFuBook().getUnnamedFoot());
        state.switchStand();
        assertEquals(player.stateEnum(), PlayerStateEnum.FightStand);
        PlayerChangeStateEvent stateEvent = eventListener.findFirst(PlayerChangeStateEvent.class).get();
        assertEquals(PlayerStateEnum.FightStand.value(), stateEvent.toPacket().getPlayerChangeState().getState());
        assertTrue(stateEvent.isIncludeSelf());
        assertTrue(player.footKungFu().isEmpty());
    }

    @Test
    void sitOrStandUp() {
        player.toggleFootKungFu(player.kungFuBook().getUnnamedFoot());
        state.sitOrStandUp();
        assertEquals(player.stateEnum(), PlayerStateEnum.Sit);
        assertTrue(player.footKungFu().isEmpty());
    }

    @Test
    void toggleBreathKungFu() {
        state.tryToggleBreathKungFu(player.kungFuBook().getUnnamedBreath());
        assertEquals(player.stateEnum(), PlayerStateEnum.Sit);
        assertTrue(player.breathKungFu().isPresent());
    }
}