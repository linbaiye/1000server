package org.y1000.kungfu;


public interface KungFu {
    String name();

    int level();

    boolean gainExp(int value);

    KungFuType kungFuType();

}
