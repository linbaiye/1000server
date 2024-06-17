package org.y1000.kungfu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.TestingEventListener;
import org.y1000.entities.creatures.event.CreatureSoundEvent;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.PlayerGainExpEvent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class FootKungFuTest {

    private FootKungFu footKungFu;

    private KeepParameters keepParameters;

    private FiveSecondsParameters fiveSecondsParameters;

    @BeforeEach
    void setUp() {
        keepParameters = Mockito.mock(KeepParameters.class);
        fiveSecondsParameters = Mockito.mock(FiveSecondsParameters.class);
        footKungFu = FootKungFu.builder()
                .sound("1")
                .keepParameters(keepParameters)
                .fiveSecondsParameters(fiveSecondsParameters)
                .exp(0)
                .name("test")
                .build();
    }

    @Test
    void useResources() {
        when(fiveSecondsParameters.lifePer5Seconds()).thenReturn(1);
        when(fiveSecondsParameters.powerPer5Seconds()).thenReturn(1);
        when(fiveSecondsParameters.innerPowerPer5Seconds()).thenReturn(1);
        when(fiveSecondsParameters.outerPowerPer5Seconds()).thenReturn(1);
        Player player = Mockito.mock(Player.class);
        when(player.currentLife()).thenReturn(2);
        assertTrue(footKungFu.useResources(player, 5000));
        verify(player).consumeLife(1);
        verify(player).consumeInnerPower(1);
        verify(player).consumeOuterPower(1);
        verify(player).consumePower(1);
    }

    @Test
    void tryGainExp() {
        Player player = Mockito.mock(Player.class);
        TestingEventListener testingEventListener = new TestingEventListener();
        for (int i = 0; i < 9; i++) {
            footKungFu.tryGainExp(player, testingEventListener::onEvent);
            assertEquals(0, testingEventListener.eventSize());
        }
        footKungFu.tryGainExp(player, testingEventListener::onEvent);
        assertNotNull(testingEventListener.removeFirst(PlayerGainExpEvent.class));
        CreatureSoundEvent sound = testingEventListener.removeFirst(CreatureSoundEvent.class);
        assertEquals("1", sound.toPacket().getSound().getSound());
        assertEquals(159, footKungFu.level());

        testingEventListener.clearEvents();
        footKungFu.tryGainExp(player, testingEventListener::onEvent);
        assertEquals(0, testingEventListener.eventSize());
    }
}