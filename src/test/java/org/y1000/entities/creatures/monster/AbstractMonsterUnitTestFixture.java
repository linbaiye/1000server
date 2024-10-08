package org.y1000.entities.creatures.monster;

import org.y1000.AbstractUnitTestFixture;
import org.y1000.TestingEventListener;
import org.y1000.entities.creatures.npc.AI.MonsterWanderingAI;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

public abstract class AbstractMonsterUnitTestFixture extends AbstractUnitTestFixture  {
    protected PassiveMonster monster;

    protected TestingMonsterAttributeProvider attributeProvider;

    protected TestingEventListener eventListener;

    protected RealmMap realmMap;

    protected void setup() {
        realmMap = mockRealmMap();
        attributeProvider = new TestingMonsterAttributeProvider();
        attributeProvider.life = 4000;
        attributeProvider.recovery = 100;
        monster = monsterBuilder().realmMap(realmMap).ai(new MonsterWanderingAI(Coordinate.Empty)).attributeProvider(attributeProvider).build();
        eventListener = new TestingEventListener();
        monster.registerEventListener(eventListener);
    }
}
