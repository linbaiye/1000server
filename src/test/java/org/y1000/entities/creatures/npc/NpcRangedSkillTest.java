package org.y1000.entities.creatures.npc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NpcRangedSkillTest {

    @Test
    void isAvailable() {
        NpcRangedSkill skill = new NpcRangedSkill(1, "test");
        assertTrue(skill.isAvailable());
        skill.consumeCounter();
        skill.consumeCounter();
        skill.consumeCounter();
        skill.consumeCounter();
        assertTrue(skill.isAvailable());
        skill.consumeCounter();
        assertFalse(skill.isAvailable());
    }
}