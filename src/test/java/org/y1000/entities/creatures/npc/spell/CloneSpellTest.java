package org.y1000.entities.creatures.npc.spell;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.npc.Npc;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CloneSpellTest {

    @Test
    void canCast() {
        CloneSpell spell = new CloneSpell(50, 1);
        var npc = Mockito.mock(Npc.class);
        when(npc.currentLife()).thenReturn(1);
        when(npc.maxLife()).thenReturn(3);
        when(npc.stateEnum()).thenReturn(State.IDLE);
        assertTrue(spell.canCast(npc));
        when(npc.currentLife()).thenReturn(2);
        assertFalse(spell.canCast(npc));
    }
}