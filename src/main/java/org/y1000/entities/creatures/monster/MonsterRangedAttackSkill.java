package org.y1000.entities.creatures.monster;



public final class MonsterRangedAttackSkill extends AbstractMonsterAttackSkill {

    private final int projectileSpriteId;

    private final String swingSound;

    public MonsterRangedAttackSkill(int projectSpriteId, String throwProjectSound) {
        this.projectileSpriteId = projectSpriteId;
        this.swingSound = throwProjectSound;
    }


    public int projectileSpriteId() {
        return projectileSpriteId;
    }

    public String sound() {
        return swingSound;
    }
}
