package org.y1000.entities.creatures.monster;

import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.TestingEventListener;
import org.y1000.realm.RealmMap;

public abstract class AbstractMonsterUnitTestFixture extends AbstractUnitTestFixture  {
    protected PassiveMonster monster;

    protected TestingMonsterAttributeProvider attributeProvider;

    protected TestingEventListener eventListener;

    protected RealmMap realmMap;

    protected void setup() {
        realmMap = Mockito.mock(RealmMap.class);
        attributeProvider = new TestingMonsterAttributeProvider();
        attributeProvider.life = 4000;
        attributeProvider.recovery = 100;
        monster = monsterBuilder().realmMap(realmMap).attributeProvider(attributeProvider).build();
        eventListener = new TestingEventListener();
        monster.registerEventListener(eventListener);
    }
}
