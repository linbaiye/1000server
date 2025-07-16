package org.y1000.entities.players;

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

    public int regenerateHalLife(PlayerStateEnum playerStateEnum) {
        return switch (playerStateEnum) {
            case Die -> applyLevel(300);
            case Sit -> applyLevel(150);
            case Idle -> applyLevel(80);
            default -> applyLevel(50);
        };
    }

    /*
      case FFeatureState of
         wfs_normal   : number := 50;
         wfs_care     : number := 20;
         wfs_sitdown  : number := 70;
         wfs_die      : number := 100;
         else number :=50;
      end;
     */
    public int regenerateResources(PlayerStateEnum playerStateEnum) {
        return switch (playerStateEnum) {
            case Die -> applyLevel(100);
            case Sit -> applyLevel(70);
            case Idle -> applyLevel(50);
            default -> applyLevel(10);
        };
    }
}
