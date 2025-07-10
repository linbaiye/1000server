package org.y1000.entities.creatures.npc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.TestingEventListener;
import org.y1000.entities.creatures.OldPlayerStateEnum;
import org.y1000.entities.players.Damage;
import org.y1000.entities.creatures.event.*;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class DevirtueNpcAITest extends AbstractNpcUnitTestFixture {

    /*
    private SubmissiveWanderingAI ai;

    private SubmissiveMerchant merchant;

    private TestingMonsterAttributeProvider testingMonsterAttributeProvider;

    private RealmMap map;

    private TestingEventListener testingEventListener;

    private Merchantable merchantable;

    @BeforeEach
    void setUp() {
        testingMonsterAttributeProvider = new TestingMonsterAttributeProvider();
        testingMonsterAttributeProvider.life = 10000;
        ai = new SubmissiveWanderingAI(Coordinate.xy(1, 1), Coordinate.Empty);
        map = Mockito.mock(RealmMap.class);
        testingEventListener = new TestingEventListener();
        merchantable = Mockito.mock(Merchantable.class);
        merchant = SubmissiveMerchant.builder()
                .id(nextId())
                .realmMap(map)
                .stateMillis(MONSTER_STATE_MILLIS)
                .name("merchant")
                .attributeProvider(testingMonsterAttributeProvider)
                .stateMillis(MONSTER_STATE_MILLIS)
                .coordinate(Coordinate.xy(3, 3))
                .merchantable(merchantable)
                .fileName("test")
                .build();
        merchant.registerEventListener(testingEventListener);
    }


    @Test
    void afterIdle() {
        when(map.movable(any(Coordinate.class))).thenReturn(true);
        var previousDire = merchant.direction();
        merchant.changeAndStartAI(ai);
        merchant.changeState(NpcCommonState.idle(merchant.getStateMillis(OldPlayerStateEnum.IDLE)));
        merchant.update(merchant.getStateMillis(OldPlayerStateEnum.IDLE));
        assertTrue(previousDire != merchant.direction() || OldPlayerStateEnum.Move == merchant.oldStateEnum());
    }

    @Test
    void afterMove() {
        when(map.movable(any(Coordinate.class))).thenReturn(true);
        merchant.changeState(NpcMoveState.move(merchant, merchant.getStateMillis(OldPlayerStateEnum.Move)));
        merchant.update(merchant.getStateMillis(OldPlayerStateEnum.Move));
        assertEquals(OldPlayerStateEnum.IDLE, merchant.oldStateEnum());
        assertNotNull(testingEventListener.removeFirst(NpcChangeStateEvent.class));
    }

    @Test
    void afterHurt() {
        PlayerImpl player = playerBuilder().coordinate(merchant.coordinate().move(1, 0)).build();
        merchant.attackedBy(player);
        merchant.update(merchant.getStateMillis(OldPlayerStateEnum.HURT));
        if (merchant.getStateMillis(OldPlayerStateEnum.HURT) > merchant.getStateMillis(OldPlayerStateEnum.IDLE)) {
            assertEquals(OldPlayerStateEnum.Turn, merchant.oldStateEnum());
        } else {
            assertEquals(OldPlayerStateEnum.IDLE, merchant.oldStateEnum());
        }
    }

    @Test
    void attacked() {
        PlayerImpl player = playerBuilder().coordinate(merchant.coordinate().move(1, 0)).build();
        merchant.attackedBy(player);
        assertEquals(OldPlayerStateEnum.HURT, merchant.oldStateEnum());
        assertNotNull(testingEventListener.removeFirst(CreatureHurtEvent.class));
        assertNotNull(testingEventListener.removeFirst(EntitySoundEvent.class));
        testingEventListener.clearEvents();
        merchant.update(merchant.getStateMillis(OldPlayerStateEnum.HURT));
        assertEquals(OldPlayerStateEnum.IDLE, merchant.oldStateEnum());
    }

    @Test
    void killed() {
        var player = Mockito.mock(Player.class);
        when(player.hit()).thenReturn(100);
        when(player.damage()).thenReturn(new Damage(merchant.maxLife() + 1, 0, 0, 0));
        merchant.attackedBy(player);
        assertEquals(OldPlayerStateEnum.DIE, merchant.oldStateEnum());
        assertNotNull(testingEventListener.removeFirst(CreatureDieEvent.class));
    }*/
}