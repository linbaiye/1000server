package org.y1000.entities.players;

import org.y1000.entities.creatures.State;
import org.y1000.exp.Experience;

public final class PlayerRevival {
    private final Experience experience;

    public PlayerRevival(int exp) {
        this(new Experience(exp));
    }

    public PlayerRevival() {
        this(new Experience(0));
    }

    public PlayerRevival(Experience experience) {
        this.experience = experience;
    }

    public PlayerRevival gainExp() {
        return new PlayerRevival(experience.gainDefaultExp());
    }

    public int exp() {
        return experience.value();
    }

    public int level() {
        return experience.level();
    }

    private int applyLevel(int value) {
        return value + value * experience.level() / 10000;
    }

    public int regenerateHalLife(State state) {
        return switch (state) {
            case DIE -> applyLevel(300);
            case SIT -> applyLevel(150);
            case IDLE -> applyLevel(80);
            default -> applyLevel(50);
        };
    }

    /*
      case FFeatureState of
         wfs_normal   : n := 50;
         wfs_care     : n := 20;
         wfs_sitdown  : n := 70;
         wfs_die      : n := 100;
         else n :=50;
      end;
     */
    public int regenerateResources(State state) {
        return switch (state) {
            case DIE -> applyLevel(100);
            case SIT -> applyLevel(70);
            case IDLE -> applyLevel(50);
            default -> applyLevel(10);
        };
    }
}
