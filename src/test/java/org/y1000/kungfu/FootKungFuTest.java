package org.y1000.kungfu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.TestingEventListener;
import org.y1000.entities.creatures.event.EntitySoundEvent;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.PlayerGainExpEvent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class FootKungFuTest {

    private FootKungFu footKungFu;

    private KeepParameters keepParameters;

    private FiveSecondsParameters fiveSecondsParameters;

    private EventResourceParameters eventResourceParameters;

    @BeforeEach
    void setUp() {
        keepParameters = Mockito.mock(KeepParameters.class);
        fiveSecondsParameters = Mockito.mock(FiveSecondsParameters.class);
        eventResourceParameters = Mockito.mock(EventResourceParameters.class);
        footKungFu = FootKungFu.builder()
                .sound("1")
                .keepParameters(keepParameters)
                .fiveSecondsParameters(fiveSecondsParameters)
                .eventResourceParameters(eventResourceParameters)
                .exp(0)
                .name("test")
                .build();
    }

    @Test
    void use5SecondsResources() {
        when(fiveSecondsParameters.lifePer5Seconds()).thenReturn(1);
        when(fiveSecondsParameters.powerPer5Seconds()).thenReturn(1);
        when(fiveSecondsParameters.innerPowerPer5Seconds()).thenReturn(1);
        when(fiveSecondsParameters.outerPowerPer5Seconds()).thenReturn(1);
        Player player = Mockito.mock(Player.class);
        when(player.currentLife()).thenReturn(2);
        assertTrue(footKungFu.updateResources(player, 5000));
        verify(player).consumeLife(1);
        verify(player).consumeInnerPower(1);
        verify(player).consumeOuterPower(1);
        verify(player).consumePower(1);
    }

    @Test
    void tryGainExpAndUseResources() {
        Player player = Mockito.mock(Player.class);
        TestingEventListener testingEventListener = new TestingEventListener();
        for (int i = 0; i < 9; i++) {
            footKungFu.tryGainExpAndUseResources(player, testingEventListener::onEvent);
            assertEquals(0, testingEventListener.eventSize());
        }
        footKungFu.tryGainExpAndUseResources(player, testingEventListener::onEvent);
        verify(player).consumeLife(any(Integer.class));
        verify(player).consumeInnerPower(any(Integer.class));
        verify(player).consumeOuterPower(any(Integer.class));
        verify(player).consumePower(any(Integer.class));
        assertNotNull(testingEventListener.removeFirst(PlayerGainExpEvent.class));
        EntitySoundEvent sound = testingEventListener.removeFirst(EntitySoundEvent.class);
        assertEquals("1", sound.toPacket().getSound().getSound());
        assertEquals(159, footKungFu.level());
        testingEventListener.clearEvents();
        footKungFu.tryGainExpAndUseResources(player, testingEventListener::onEvent);
        assertEquals(0, testingEventListener.eventSize());
    }
}