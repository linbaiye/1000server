package org.y1000.kungfu;


public interface KungFu {
    String name();

    int level();

    /**
     * Gain experience.
     * @param value exp
     * @return true if level up.
     */
    boolean gainExp(int value);


    KungFuType kungFuType();


    String description();


    KungFu duplicate();

}
