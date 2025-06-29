package org.y1000.entities.creatures.npc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.OldPlayerStateEnum;
import org.y1000.entities.creatures.monster.AbstractMonsterUnitTestFixture;
import org.y1000.entities.creatures.npc.AI.MonsterWanderingAI;
import org.y1000.entities.creatures.npc.AI.ViolentNpcMeleeFightAI;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.realm.Realm;
import org.y1000.util.Coordinate;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class ViolentNpcMeleeFightAITest extends AbstractMonsterUnitTestFixture {
    private ViolentNpcMeleeFightAI ai;

    private Creature enemy;

    private Realm realm;

    @BeforeEach
    void setUp() {
        setup();
        enemy = Mockito.mock(Creature.class);
        when(enemy.canBeAttackedNow()).thenReturn(true);
        when(enemy.canBeSeenAt(any(Coordinate.class))).thenReturn(true);
        when(enemy.realmMap()).thenReturn(realmMap);
        ai = new ViolentNpcMeleeFightAI(enemy, monster);
        realm = mockRealm(realmMap);
    }

    @Test
    void stopFightingWhenPlayerLeft() {
        monster.changeCoordinate(Coordinate.xy(3, 3));
        monster.changeDirection(Direction.UP);
        var player = playerBuilder().coordinate(Coordinate.xy(3, 4)).build();
        player.joinRealm(realm, );
        ai = new ViolentNpcMeleeFightAI(player, monster);
        monster.changeAndStartAI(ai);
        assertEquals(OldPlayerStateEnum.ATTACK, monster.oldStateEnum());
        monster.update(monster.getStateMillis(OldPlayerStateEnum.ATTACK) - 10);
        player.leaveRealm();
        monster.update( 10 + monster.cooldown());
        assertNotEquals(OldPlayerStateEnum.ATTACK, monster.oldStateEnum());
    }

    @Test
    void chase() {
        monster.changeCoordinate(Coordinate.xy(3, 3));
        monster.changeDirection(Direction.UP);
        Set<Coordinate> notMovable = Set.of(Coordinate.xy(2, 4), Coordinate.xy(3, 4), Coordinate.xy(4, 4));
        when(realmMap.movable(any(Coordinate.class))).thenAnswer(invocationOnMock -> !notMovable.contains(invocationOnMock.getArgument(0)));
        when(enemy.coordinate()).thenReturn(Coordinate.xy(3, 5));
        monster.changeAndStartAI(ai);
        assertEquals(Direction.RIGHT, monster.direction());
        assertEquals(OldPlayerStateEnum.IDLE, monster.oldStateEnum());

        monster.update(monster.getStateMillis(OldPlayerStateEnum.IDLE));
        assertEquals(Direction.RIGHT, monster.direction());
        assertEquals(OldPlayerStateEnum.Move, monster.oldStateEnum());

        monster.update(monster.getStateMillis(OldPlayerStateEnum.Move));
        assertEquals(Direction.DOWN_RIGHT, monster.direction());
        assertEquals(OldPlayerStateEnum.IDLE, monster.oldStateEnum());

        monster.update(monster.getStateMillis(OldPlayerStateEnum.IDLE));
        assertEquals(Direction.DOWN_RIGHT, monster.direction());
        assertEquals(OldPlayerStateEnum.Move, monster.oldStateEnum());

        monster.update(monster.getStateMillis(OldPlayerStateEnum.Move));
        assertEquals(Direction.DOWN_LEFT, monster.direction());
        assertEquals(OldPlayerStateEnum.IDLE, monster.oldStateEnum());

        monster.update(monster.getStateMillis(OldPlayerStateEnum.IDLE));
        assertEquals(Direction.DOWN_LEFT, monster.direction());
        assertEquals(OldPlayerStateEnum.Move, monster.oldStateEnum());

        monster.update(monster.getStateMillis(OldPlayerStateEnum.Move));
        assertEquals(Direction.LEFT, monster.direction());
        assertEquals(OldPlayerStateEnum.ATTACK, monster.oldStateEnum());
    }

    @Test
    void chaseStuck() {
        monster.changeCoordinate(Coordinate.xy(4, 3));
        monster.changeDirection(Direction.UP);
        Set<Coordinate> notMovable = Set.of(Coordinate.xy(1, 4), Coordinate.xy(2, 4), Coordinate.xy(3, 4), Coordinate.xy(4, 4),
                Coordinate.xy(5, 4), Coordinate.xy(6, 4), Coordinate.xy(7, 4));
        when(realmMap.movable(any(Coordinate.class))).thenAnswer(invocationOnMock -> !notMovable.contains(invocationOnMock.getArgument(0)));
        when(enemy.coordinate()).thenReturn(Coordinate.xy(4, 5));
        monster.changeAndStartAI(ai);
        assertEquals(Direction.RIGHT, monster.direction());
        assertEquals(OldPlayerStateEnum.IDLE, monster.oldStateEnum());

        monster.update(monster.getStateMillis(OldPlayerStateEnum.IDLE));
        assertEquals(Direction.RIGHT, monster.direction());
        assertEquals(OldPlayerStateEnum.Move, monster.oldStateEnum());

        monster.update(monster.walkSpeed());
        assertEquals(Direction.RIGHT, monster.direction());
        assertEquals(OldPlayerStateEnum.Move, monster.oldStateEnum());

        monster.update(monster.walkSpeed());
        assertEquals(Direction.UP_LEFT, monster.direction());
        assertEquals(OldPlayerStateEnum.IDLE, monster.oldStateEnum());

        monster.update(monster.getStateMillis(OldPlayerStateEnum.IDLE));
        assertEquals(Direction.UP_LEFT, monster.direction());
        assertEquals(OldPlayerStateEnum.Move, monster.oldStateEnum());

        monster.update(monster.walkSpeed());
        assertEquals(Direction.DOWN_LEFT, monster.direction());
        assertEquals(OldPlayerStateEnum.IDLE, monster.oldStateEnum());
    }

    @Test
    void enemyDead() {
        monster.changeCoordinate(Coordinate.xy(3, 3));
        monster.changeDirection(Direction.UP);
        when(enemy.coordinate()).thenReturn(Coordinate.xy(3, 4));
        monster.changeAndStartAI(ai);
        assertEquals(OldPlayerStateEnum.ATTACK, monster.oldStateEnum());
        monster.update(monster.getStateMillis(OldPlayerStateEnum.ATTACK) - 10);
        reset(enemy);
        when(enemy.canBeAttackedNow()).thenReturn(false);
        monster.update( 10 + monster.cooldown());
        assertNotEquals(OldPlayerStateEnum.ATTACK, monster.oldStateEnum());
    }

    @Test
    void monsterDead() {
        monster.changeCoordinate(Coordinate.xy(3, 3));
        monster.changeDirection(Direction.UP);
        when(enemy.coordinate()).thenReturn(Coordinate.xy(3, 4));
        monster.changeAndStartAI(ai);
        assertEquals(OldPlayerStateEnum.ATTACK, monster.oldStateEnum());
        monster.update(monster.getStateMillis(OldPlayerStateEnum.ATTACK) - 10);
        monster.changeState(NpcCommonState.die(10000));
        monster.update( 20);
        assertNotEquals(OldPlayerStateEnum.ATTACK, monster.oldStateEnum());
    }

    @Test
    void changeEnemyIfCurrentEnemyFar() {
        PlayerImpl player = playerBuilder().coordinate(monster.coordinate().move(1, 0)).build();
        player.joinRealm(realm, );
        monster.changeAndStartAI(new MonsterWanderingAI(monster.coordinate()));
        monster.attackedBy(player);
        monster.update(monster.cooldown());
        assertEquals(OldPlayerStateEnum.ATTACK, monster.oldStateEnum());
        player.changeCoordinate(player.coordinate().move(2, 2));

        var another = playerBuilder().coordinate(monster.coordinate().move(0, 1)).build();
        another.joinRealm(realm, );
        monster.attackedBy(another);
        assertEquals(OldPlayerStateEnum.HURT, monster.oldStateEnum());
        monster.update(monster.cooldown());
        assertEquals(Direction.DOWN, monster.direction());
    }
}