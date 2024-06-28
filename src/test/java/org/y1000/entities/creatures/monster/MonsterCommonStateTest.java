//package org.y1000.entities.creatures.monster;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.y1000.entities.creatures.State;
//
//import static org.mockito.Mockito.verify;
//
//class MonsterCommonStateTest extends AbstractMonsterUnitTestFixture {
//
//    @BeforeEach
//    void setUp() {
//        setup();
//    }
//
//    @Test
//    void onAttackDone() {
//        MonsterCommonState commonState = MonsterCommonState.attack(monster);
//        var ai = Mockito.mock(MonsterAI.class);
//        monster.changeAI(ai);
//        commonState.update(monster, monster.getStateMillis(State.ATTACK));
//        verify(ai).onAttackDone(monster);
//    }
//
//    @Test
//    void onIdleDone() {
//        MonsterCommonState commonState = MonsterCommonState.idle(monster);
//        var ai = Mockito.mock(MonsterAI.class);
//        monster.changeAI(ai);
//        commonState.update(monster, monster.getStateMillis(State.IDLE));
//        verify(ai).onIdleDone(monster);
//    }
//
//    @Test
//    void onFrozenDone() {
//        MonsterCommonState commonState = MonsterCommonState.freeze(monster);
//        var ai = Mockito.mock(MonsterAI.class);
//        monster.changeAI(ai);
//        commonState.update(monster, monster.getStateMillis(State.FROZEN));
//        verify(ai).onFrozenDone(monster);
//    }
//
//    @Test
//    void onCooldownDone() {
//        monster.cooldownAttack();
//        MonsterCommonState commonState = MonsterCommonState.cooldown(monster);
//        var ai = Mockito.mock(MonsterAI.class);
//        monster.changeAI(ai);
//        commonState.update(monster, monster.cooldown());
//        verify(ai).onIdleDone(monster);
//    }
//}