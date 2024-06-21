package org.y1000.entities.creatures.monster;

import org.y1000.AbstractUnitTestFixture;
import org.y1000.TestingEventListener;

public abstract class AbstractMonsterUnitTestFixture extends AbstractUnitTestFixture  {
    protected PassiveMonster monster;

    protected TestingMonsterAttributeProvider attributeProvider;

    protected TestingEventListener eventListener;

    protected void setup() {
        attributeProvider = new TestingMonsterAttributeProvider();
        attributeProvider.life = 4000;
        attributeProvider.recovery = 100;
        monster = monsterBuilder().attributeProvider(attributeProvider).build();
        eventListener = new TestingEventListener();
        monster.registerEventListener(eventListener);
    }
}
