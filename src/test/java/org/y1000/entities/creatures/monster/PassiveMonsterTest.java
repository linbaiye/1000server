package org.y1000.entities.creatures.monster;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.TestingEventListener;
import org.y1000.entities.Direction;
import org.y1000.entities.Projectile;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.CreatureHurtEvent;
import org.y1000.entities.creatures.monster.fight.MonsterFightCooldownState;
import org.y1000.entities.creatures.monster.fight.MonsterFightIdleState;
import org.y1000.entities.players.Player;
import org.y1000.realm.Realm;
import org.y1000.util.Coordinate;

import static org.junit.jupiter.api.Assertions.*;

class PassiveMonsterTest extends AbstractUnitTestFixture {

    private TestingEventListener listener;

    private PassiveMonster monster;

    private Player player;

    @BeforeEach
    void setUp() {
        listener = new TestingEventListener();
        monster = createMonster(Coordinate.xy(1, 1));
        monster.registerEventListener(listener);
        player = playerBuilder().build();
    }

    @Test
    void getHurt() {
        monster.attackedBy(player);
        assertEquals(player, monster.getFightingEntity());
        assertSame(monster.stateEnum(), State.HURT);
        assertNotNull(listener.dequeue(CreatureHurtEvent.class));
        monster.update(monster.getStateMillis(State.HURT));
        assertTrue(monster.state() instanceof MonsterFightIdleState);
    }

    @Test
    void getHurtChangeTarget() {
        player = playerBuilder().coordinate(monster.coordinate().move(2, 2)).build();
        Projectile projectile = Projectile.builder().target(monster).shooter(player).flyingMillis(10).build();
        monster.attackedBy(projectile);
        assertEquals(player, monster.getFightingEntity());
        var player1 = playerBuilder().coordinate(monster.coordinate().moveBy(Direction.LEFT)).build();
        monster.attackedBy(player1);
        assertEquals(player1, monster.getFightingEntity());
    }

    @Test
    void getHurtShouldNotChangeTargetIfAdjacent() {
        player = playerBuilder().coordinate(monster.coordinate().moveBy(Direction.UP)).build();
        monster.attackedBy(player);
        assertEquals(player, monster.getFightingEntity());
        var player1 = playerBuilder().coordinate(monster.coordinate().moveBy(Direction.LEFT)).build();
        monster.attackedBy(player1);
        assertEquals(player, monster.getFightingEntity());
    }

    @Test
    void fightingTargetLeft() {
        monster.attackedBy(player);
        player.leaveRealm();
        assertNull(monster.getFightingEntity());
        monster.update(monster.getStateMillis(State.HURT));
        assertEquals(monster.stateEnum(), State.IDLE);
    }

    @Test
    void attackCooldown() {
        monster.setFightingEntity(playerBuilder().coordinate(monster.coordinate().move(1, 0)).build());
        monster.changeState(MonsterFightCooldownState.cooldown(10));
        monster.update(10);
        assertEquals(Realm.STEP_MILLIS * monster.attackSpeed(), monster.cooldown());
    }
}