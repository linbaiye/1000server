package org.y1000.entities.creatures.npc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.TestingEventListener;
import org.y1000.entities.Direction;
import org.y1000.entities.players.Damage;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.*;
import org.y1000.entities.creatures.monster.TestingMonsterAttributeProvider;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.message.SetPositionEvent;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class DevirtueNpcAITest extends AbstractNpcUnitTestFixture {

    private SubmissiveWanderingAI ai;

    private DevirtueMerchant merchant;

    private TestingMonsterAttributeProvider testingMonsterAttributeProvider;

    private RealmMap map;

    private TestingEventListener testingEventListener;

    @BeforeEach
    void setUp() {
        testingMonsterAttributeProvider = new TestingMonsterAttributeProvider();
        testingMonsterAttributeProvider.life = 10000;
        ai = new SubmissiveWanderingAI(Coordinate.xy(1, 1), Coordinate.Empty);
        map = Mockito.mock(RealmMap.class);
        testingEventListener = new TestingEventListener();
        merchant = DevirtueMerchant.builder()
                .id(nextId())
                .realmMap(map)
                .stateMillis(MONSTER_STATE_MILLIS)
                .name("merchant")
                .ai(ai)
                .attributeProvider(testingMonsterAttributeProvider)
                .direction(Direction.DOWN)
                .stateMillis(MONSTER_STATE_MILLIS)
                .coordinate(Coordinate.xy(3, 3))
                .textFileName("items.text")
                .sell(Collections.emptyList())
                .buy(Collections.emptyList())
                .build();
        merchant.registerEventListener(testingEventListener);
    }


    @Test
    void afterIdle() {
        when(map.movable(any(Coordinate.class))).thenReturn(true);
        var previousDire = merchant.direction();
        merchant.update(merchant.getStateMillis(State.IDLE));
        assertTrue(previousDire != merchant.direction() || State.WALK == merchant.stateEnum());
    }

    @Test
    void afterMove() {
        when(map.movable(any(Coordinate.class))).thenReturn(true);
        merchant.changeState(NpcMoveState.move(merchant, merchant.getStateMillis(State.WALK)));
        merchant.update(merchant.getStateMillis(State.WALK));
        assertEquals(State.IDLE, merchant.stateEnum());
        assertNotNull(testingEventListener.removeFirst(NpcChangeStateEvent.class));
    }

    @Test
    void afterHurt() {
        PlayerImpl player = playerBuilder().coordinate(merchant.coordinate().move(1, 0)).build();
        merchant.attackedBy(player);
        merchant.update(merchant.getStateMillis(State.HURT));
        if (merchant.getStateMillis(State.HURT) > merchant.getStateMillis(State.IDLE)) {
            assertEquals(State.FROZEN, merchant.stateEnum());
        } else {
            assertEquals(State.IDLE, merchant.stateEnum());
        }
    }

    @Test
    void attacked() {
        PlayerImpl player = playerBuilder().coordinate(merchant.coordinate().move(1, 0)).build();
        merchant.attackedBy(player);
        assertEquals(State.HURT, merchant.stateEnum());
        assertNotNull(testingEventListener.removeFirst(CreatureHurtEvent.class));
        assertNotNull(testingEventListener.removeFirst(EntitySoundEvent.class));
        testingEventListener.clearEvents();
        merchant.update(merchant.getStateMillis(State.HURT));
        assertEquals(State.IDLE, merchant.stateEnum());
    }

    @Test
    void killed() {
        var player = Mockito.mock(Player.class);
        when(player.hit()).thenReturn(100);
        when(player.damage()).thenReturn(new Damage(merchant.maxLife() + 1, 0, 0, 0));
        merchant.attackedBy(player);
        assertEquals(State.DIE, merchant.stateEnum());
        assertNotNull(testingEventListener.removeFirst(CreatureDieEvent.class));
    }
}