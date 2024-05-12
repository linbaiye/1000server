package org.y1000.entities.attribute;

import java.util.concurrent.ThreadLocalRandom;

/**
 * I hate naming.
 * @param hit hit chance.
 * @param avoidance
 * @param recovery
 * @param holdance
 */
public record HarhAttribute(int hit, int avoidance, int recovery, int holdance, int attackSpeed){

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    public static final HarhAttribute DEFAULT = new HarhAttribute(0,25,50,0, 70);

    public boolean randomHit(HarhAttribute another) {
        var rand = RANDOM.nextInt(another.hit() + 75 + avoidance());
        return rand >= another.avoidance();
    }
}
