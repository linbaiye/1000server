package org.y1000.entities.creatures.npc.spell;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.entities.creatures.OldPlayerStateEnum;
import org.y1000.entities.creatures.npc.INpc;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CloneSpellTest {

    @Test
    void canCast() {
        CloneSpell spell = new CloneSpell(50, 1);
        var npc = Mockito.mock(INpc.class);
        when(npc.currentLife()).thenReturn(1);
        when(npc.maxLife()).thenReturn(3);
        assertTrue(spell.canCast(npc));
        when(npc.currentLife()).thenReturn(2);
        assertFalse(spell.canCast(npc));
    }
}