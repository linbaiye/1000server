package org.y1000.entities.creatures.monster;

import org.y1000.AbstractUnitTestFixture;
import org.y1000.TestingEventListener;

public abstract class AbstractMonsterUnitTestFixture extends AbstractUnitTestFixture  {
    protected PassiveMonster monster;

    protected TestingEventListener eventListener;

    protected void setup() {
        monster = monsterBuilder().life(4000).build();
        eventListener = new TestingEventListener();
        monster.registerEventListener(eventListener);
    }
}
