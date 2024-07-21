package org.y1000.entities.creatures.npc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NpcRangedSkillTest {

    @Test
    void isAvailable() {
        NpcRangedSkill skill = new NpcRangedSkill(1, "test");
        assertTrue(skill.isAvailable());
        skill.consumeProjectile();
        skill.consumeProjectile();
        skill.consumeProjectile();
        skill.consumeProjectile();
        assertTrue(skill.isAvailable());
        skill.consumeProjectile();
        assertFalse(skill.isAvailable());
        skill.consumeProjectile();
        assertFalse(skill.isAvailable());
    }

    @Test
    void cooldown() {
        NpcRangedSkill skill = new NpcRangedSkill(1, "test");
        while (skill.isAvailable()) {
            skill.consumeProjectile();
        }
        skill.cooldown(NpcRangedSkill.COOLDOWN - 1);
        assertFalse(skill.isAvailable());
        skill.cooldown(1);
        assertTrue(skill.isAvailable());
    }
}