package org.y1000.realm;



public interface Realm {
    int STEP_MILLIS = 10;

    void handle(Object event);

    RealmMap map();

    String title();

    String bgm();

    void update();

    void init();

    int id();

    void shutdown();
}
