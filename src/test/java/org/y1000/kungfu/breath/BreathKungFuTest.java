package org.y1000.kungfu.breath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.creatures.event.CreatureSoundEvent;
import org.y1000.entities.players.AbstractPlayerUnitTestFixture;
import org.y1000.entities.players.event.PlayerAttributeEvent;
import org.y1000.kungfu.EventResourceParameters;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class BreathKungFuTest extends AbstractPlayerUnitTestFixture {

    private BreathKungFu breathKungFu;

    private EventResourceParameters resourceParameters;

    @BeforeEach
    void setUp() {
        resourceParameters = Mockito.mock(EventResourceParameters.class);
        breathKungFu = BreathKungFu.builder().parameters(resourceParameters).name("test").exp(0).sound("1").build();
        setup();
    }

    @Test
    void update() {
        when(resourceParameters.power()).thenReturn(1);
        player.consumePower(1);
        breathKungFu.update(player, BreathKungFu.DEFAULT_TIMER_MILLIS, eventListener::onEvent);
        assertNotEquals(100, breathKungFu.level());
        assertNotNull(eventListener.removeFirst(PlayerAttributeEvent.class));
        assertNotNull(eventListener.removeFirst(CreatureSoundEvent.class));
    }

    @Test
    void canGenerateResources() {
        when(resourceParameters.life()).thenReturn(1);
        player.consumeLife(1);
        assertTrue(breathKungFu.canRegenerateResources(player));

        resourceParameters = Mockito.mock(EventResourceParameters.class);
        when(resourceParameters.power()).thenReturn(0);
        player.consumePower(1);
        assertFalse(breathKungFu.canRegenerateResources(player));
    }
}