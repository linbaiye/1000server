package org.y1000.kungfu;


public interface KungFu {
    String name();

    int level();

    int exp();

    /**
     * Gain experience.
     * @param value exp
     * @return true if level up.
     */
    boolean gainPermittedExp(int value);

    boolean isExpFull();


    KungFuType kungFuType();


    String description();


    KungFu duplicate();

}
