package org.y1000.entities.players;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.Direction;
import org.y1000.entities.Entity;
import org.y1000.entities.players.event.PlayerMoveEvent;
import org.y1000.entities.players.event.PlayerSetPositionAndStateEvent;
import org.y1000.item.Equipment;
import org.y1000.item.ItemFactory;
import org.y1000.kungfu.FootKungFu;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.kungfu.attack.AttackKungFuType;
import org.y1000.kungfu.breath.BreathKungFu;
import org.y1000.message.PlayerChangeStateEvent;
import org.y1000.message.SyncActiveKungEvent;
import org.y1000.message.input.MoveInput;
import org.y1000.message.input.TurnInput;
import org.y1000.util.Coordinate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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


    private void mockWithIdle() {
        mockPlayer();
        state = PlayerStandState.idle(player);
        when(player.stateEnum()).thenReturn(PlayerStateEnum.FightStand);
    }

    private void mockWithFightStand() {
        mockPlayer();
        state = PlayerStandState.fightStand(player);
        when(player.stateEnum()).thenReturn(PlayerStateEnum.FightStand);

    }

    @Test
    void tryMove_idle_WhenCoordinateMismatch() {
        MoveInput moveInput = new MoveInput(player.coordinate().move(1, 1), Direction.DOWN);
        state.tryMove(moveInput);
        assertNotNull(eventListener.removeFirst(PlayerSetPositionAndStateEvent.class));
    }

    @Test
    void tryMove_idle_whenCoordinateNotMovable() {
        MoveInput moveInput = new MoveInput(player.coordinate(), player.direction().opposite());
        when(mockedRealm.map().movable(any(Coordinate.class))).thenReturn(false);
        state.tryMove(moveInput);
        assertEquals(player.direction(), moveInput.direction());
        assertNotNull(eventListener.removeFirst(PlayerSetPositionAndStateEvent.class));
    }

    @Test
    void tryMove_idle_whenOk() {
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
    void tryMove_fightStand_whenOk() {
        changeToFightStand();
        MoveInput moveInput = new MoveInput(player.coordinate(), Direction.DOWN);
        when(mockedRealm.map().movable(any(Coordinate.class))).thenReturn(true);
        state.tryMove(moveInput);
        assertNotNull(eventListener.removeFirst(PlayerMoveEvent.class));
        assertSame(player.stateEnum(), PlayerStateEnum.Move);
    }

    @Test
    void sayHello_idle() {
        state.sayHello();
        assertEquals(player.stateEnum(), PlayerStateEnum.Hello);
        PlayerChangeStateEvent event = eventListener.removeFirst(PlayerChangeStateEvent.class);
        assertTrue(event.isIncludeSelf());
    }

    @Test
    void sayHello_fightStand() {
        changeToFightStand();
        state.sayHello();
        assertEquals(player.stateEnum(), PlayerStateEnum.FightStand);
    }


    @Test
    void sitOrStand_idle() {
        mockWithIdle();
        state.sitOrStandUp();
        PlayerChangeStateEvent playerChangeStateEvent = eventListener.removeFirst(PlayerChangeStateEvent.class);
        assertNotNull(playerChangeStateEvent);
        verify(player, times(1)).stopCombat();
        verify(player, times(1)).changeState(any(PlayerSitDownState.class));
        verify(player, times(1)).disableFootKungFuAndSync();
    }

    @Test
    void sitOrStand_fightStand() {
        mockWithFightStand();
        state.sitOrStandUp();
        assertNotNull(eventListener.removeFirst(PlayerChangeStateEvent.class));
        verify(player, times(1)).changeState(any(PlayerSitDownState.class));
        verify(player, times(1)).stopCombat();
    }

    @Test
    void sitOrStand_idle_withFootKungFu() {
        player.toggleFootKungFu(player.kungFuBook().getUnnamedFoot());
        eventListener.clear();
        state.sitOrStandUp();
        assertNotNull(eventListener.remove(PlayerChangeStateEvent.class));
        assertEquals(PlayerStateEnum.Sit, player.stateEnum());
        assertTrue(player.footKungFu().isEmpty());
        assertNotNull(eventListener.remove(SyncActiveKungEvent.class));
        assertTrue(eventListener.isEmpty());
    }

    @Test
    void turn_idle() {
        TurnInput turnInput = new TurnInput(player.direction().opposite());
        state.turn(turnInput);
        assertEquals(turnInput.direction(), player.direction());
        PlayerChangeStateEvent remove = eventListener.remove(PlayerChangeStateEvent.class);
        assertNotNull(remove);
        assertFalse(remove.isIncludeSelf());
    }

    @Test
    void switchStand_idle() {
        state.switchStand();
        assertEquals(player.stateEnum(), PlayerStateEnum.FightStand);
        PlayerChangeStateEvent stateEvent = eventListener.remove(PlayerChangeStateEvent.class);
        assertEquals(PlayerStateEnum.FightStand.value(), stateEvent.toPacket().getPlayerChangeState().getState());
        assertTrue(stateEvent.isIncludeSelf());
    }

    @Test
    void switchStand_fightStand() {
        mockWithFightStand();
        state.switchStand();
        PlayerChangeStateEvent remove = eventListener.remove(PlayerChangeStateEvent.class);
        assertNotNull(remove);
        verify(player, times(1)).stopCombat();
        verify(player, times(1)).disableFootKungFuAndSync();
        verify(player, times(1)).changeState(any(PlayerStandState.class));
    }

    @Test
    void switchStand_idle_footEnabled() {
        player.toggleFootKungFu(player.kungFuBook().getUnnamedFoot());
        state.switchStand();
        assertEquals(player.stateEnum(), PlayerStateEnum.FightStand);
        PlayerChangeStateEvent stateEvent = eventListener.remove(PlayerChangeStateEvent.class);
        assertEquals(PlayerStateEnum.FightStand.value(), stateEvent.toPacket().getPlayerChangeState().getState());
        assertTrue(stateEvent.isIncludeSelf());
        assertTrue(player.footKungFu().isEmpty());
    }

    @Test
    void tryToggleAttackKungFu() {
        player.inventory().add(itemFactory.createEquipment("长剑"));
        AttackKungFu unnamedAttack = player.kungFuBook().findUnnamedAttack(AttackKungFuType.SWORD);
        state.tryToggleAttackKungFu(unnamedAttack);
        assertEquals(player.kungFuBook().findUnnamedAttack(AttackKungFuType.SWORD), player.attackKungFu());
    }


    @Test
    void tryToggleBreathKungFu() {
        var breath = Mockito.mock(BreathKungFu.class);
        mockWithFightStand();
        doAnswer(a -> {
            assertEquals(PlayerStateEnum.Sit, ((PlayerSitDownState)a.getArgument(0)).playerStateEnum());
            return null;
        }).when(player).changeState(any(PlayerSitDownState.class));
        state.tryToggleBreathKungFu(breath);
        verify(player, times(1)).stopCombat();
        verify(player, times(1)).changeState(any(PlayerSitDownState.class));
        assertNotNull(eventListener.remove(PlayerChangeStateEvent.class));
    }

    @Test
    void tryToggleFootKungFu_idle() {
        mockWithIdle();
        FootKungFu unnamedFoot = mock(FootKungFu.class);
        state.tryToggleFootKungFu(unnamedFoot);
        verify(player, times(1)).toggleFootKungFu(unnamedFoot);
    }

    @Test
    void tryToggleFootKungFu_fightStand() {
        mockWithFightStand();
        FootKungFu unnamedFoot = mock(FootKungFu.class);
        doAnswer(a -> {
            assertEquals(PlayerStateEnum.Idle, ((PlayerStandState)a.getArgument(0)).playerStateEnum());
            return null;
        }).when(player).changeState(any(PlayerStandState.class));
        state.tryToggleFootKungFu(unnamedFoot);
        verify(player, times(1)).toggleFootKungFu(unnamedFoot);
        verify(player, times(1)).changeState(any(PlayerStandState.class));
        verify(player, times(1)).stopCombat();
        assertNotNull(eventListener.remove(PlayerChangeStateEvent.class));
    }

    @Test
    void tryEquip() {
        mockWithFightStand();
        var equip = Mockito.mock(Equipment.class);
        state.equip(1, equip);
        verify(player, times(1)).tryEquipFromSlot(1, equip);
    }

    @Test
    void handleAfterHurt_Idle() {
        state.handleAfterHurt();
        assertEquals(PlayerStateEnum.Idle, player.stateEnum());
        assertNotNull(eventListener.remove(PlayerChangeStateEvent.class));
    }

    @Test
    void handleAfterHurt_FightStand() {
        changeToFightStand();
        state.handleAfterHurt();
        assertEquals(PlayerStateEnum.FightStand, player.stateEnum());
        assertNotNull(eventListener.remove(PlayerChangeStateEvent.class));
    }

    @Test
    void toggleFootKungFu() {
        mockPlayer();
        state = PlayerStandState.idle(player);
        FootKungFu mock = mock(FootKungFu.class);
        state.tryToggleFootKungFu(mock);
        verify(player, times(1)).toggleFootKungFu(mock);
    }

    @Test
    void toggleAttackKungFu() {
        mockPlayer();
        state = PlayerStandState.idle(player);
        var mock = mock(AttackKungFu.class);
        state.tryToggleAttackKungFu(mock);
        verify(player, times(1)).tryChangeAttackKungFu(mock);
    }

    @Test
    void acceptAttack_idle() {
        mockWithIdle();
        state = PlayerStandState.idle(player);
        var entity = Mockito.mock(ActiveEntity.class);
        when(player.tryAcceptAttack(entity)).thenReturn(-1);
        state.attack(entity);
        verify(player, times(0)).changeState(any());

        when(player.tryAcceptAttack(entity)).thenReturn(0);
        state.attack(entity);
        doAnswer(i -> {
            assertEquals(PlayerStateEnum.FightStand, ((PlayerStandState)i.getArgument(0)).playerStateEnum());
            return null;
        }).when(player).changeState(any(PlayerStandState.class));
        verify(player, times(1)).changeState(any(PlayerStandState.class));
        assertNotNull(eventListener.removeFirst(PlayerChangeStateEvent.class));
    }
}