//package org.y1000.entities.creatures.monster;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.y1000.entities.Direction;
//import org.y1000.entities.creatures.State;
//import org.y1000.entities.creatures.event.CreatureAttackEvent;
//import org.y1000.entities.creatures.event.NpcChangeStateEvent;
//import org.y1000.entities.creatures.event.NpcMoveEvent;
//import org.y1000.entities.players.Player;
//import org.y1000.util.Coordinate;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//class MonsterMeleeFightAITest extends AbstractMonsterUnitTestFixture {
//
//    private MonsterMeleeFightAI ai;
//
//    @BeforeEach
//    void setUp() {
//        setup();
//        ai = new MonsterMeleeFightAI();
//        monster.changeAI(ai);
//    }
//
//    @Test
//    void start() {
//        var player = playerBuilder().coordinate(monster.coordinate().moveBy(monster.direction())).build();
//        monster.setFightingEntity(player);
//        monster.cooldownRecovery();
//        ai.start(monster);
//        assertEquals(State.IDLE, monster.stateEnum());
//        assertEquals(monster.cooldown(), monster.state().totalMillis());
//        assertNotNull(eventListener.removeFirst(NpcChangeStateEvent.class));
//    }
//
//    @Test
//    void onIdleDoneWhenTargetCloseEnough() {
//        var player = playerBuilder().coordinate(monster.coordinate().moveBy(monster.direction())).build();
//        monster.setFightingEntity(player);
//        ai.onIdleDone(monster);
//        assertEquals(State.ATTACK, monster.stateEnum());
//        assertNotNull(eventListener.removeFirst(CreatureAttackEvent.class));
//    }
//
//    @Test
//    void onIdleDoneWhenTargetGone() {
//        ai.onIdleDone(monster);
//        assertEquals(State.IDLE, monster.stateEnum());
//        assertNotNull(eventListener.removeFirst(NpcChangeStateEvent.class));
//        assertNotEquals(ai, monster.AI());
//    }
//
//    @Test
//    void onIdleDoneWhenTargetFar() {
//        when(realmMap.movable(any(Coordinate.class))).thenReturn(true);
//        Direction direction = monster.direction();
//        var player = playerBuilder().coordinate(monster.coordinate().moveBy(direction).moveBy(direction)).build();
//        monster.setFightingEntity(player);
//        ai.onIdleDone(monster);
//        assertEquals(State.FROZEN, monster.stateEnum());
//        assertNotNull(eventListener.removeFirst(NpcChangeStateEvent.class));
//    }
//
//    @Test
//    void onFrozenDoneWhenTargetCloseEnough() {
//        var player = Mockito.mock(Player.class);
//        when(player.coordinate()).thenReturn(monster.coordinate().moveBy(monster.direction()));
//        when(player.canBeAttackedNow()).thenReturn(true);
//        monster.setFightingEntity(player);
//        ai.onFrozenDone(monster);
//        assertEquals(State.ATTACK, monster.stateEnum());
//        assertTrue(monster.cooldown() != 0);
//        assertNotNull(eventListener.removeFirst(CreatureAttackEvent.class));
//        verify(player).attackedBy(monster);
//    }
//
//    @Test
//    void onFrozenDoneWhenTargetGone() {
//        ai.onFrozenDone(monster);
//        assertEquals(State.IDLE, monster.stateEnum());
//        assertNotNull(eventListener.removeFirst(NpcChangeStateEvent.class));
//        assertNotEquals(ai, monster.AI());
//    }
//
//    @Test
//    void onFrozenDoneWhenTargetFar() {
//        when(realmMap.movable(any(Coordinate.class))).thenReturn(true);
//        Direction direction = monster.direction();
//        var player = playerBuilder().coordinate(monster.coordinate().moveBy(direction).moveBy(direction)).build();
//        monster.setFightingEntity(player);
//        // even the monster needs to recover, still move.
//        monster.cooldownRecovery();
//        ai.onFrozenDone(monster);
//        assertEquals(State.WALK, monster.stateEnum());
//        assertNotNull(eventListener.removeFirst(NpcMoveEvent.class));
//    }
//
//    @Test
//    void onAttackDone() {
//        var player = playerBuilder().coordinate(monster.coordinate().moveBy(monster.direction())).build();
//        monster.setFightingEntity(player);
//        monster.cooldownAttack();
//        ai.onAttackDone(monster);
//        assertEquals(State.IDLE, monster.stateEnum());
//        assertNotNull(eventListener.removeFirst(NpcChangeStateEvent.class));
//    }
//}
