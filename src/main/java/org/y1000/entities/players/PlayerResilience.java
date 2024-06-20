package org.y1000.entities.players;

import org.y1000.exp.Experience;

public final class PlayerResilience {
    private final Experience experience;

    public PlayerResilience(int exp) {
        this.experience = new Experience(exp);
    }

    public PlayerResilience(Experience experience) {
        this.experience = experience;
    }

    public int level() {
        return experience.level();
    }

    public PlayerResilience gainExp() {
        return new PlayerResilience(experience.gainDefaultExp());
    }

    public int reduceDamage(int damage) {
        if (damage <= 0) {
            return damage;
        }
        return damage - damage * experience.level() / 20000;
    }
}
