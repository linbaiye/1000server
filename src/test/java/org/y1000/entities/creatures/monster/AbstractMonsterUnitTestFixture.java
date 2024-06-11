package org.y1000.entities.creatures.monster;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.TestingEventListener;
import org.y1000.entities.Direction;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

public abstract class AbstractMonsterUnitTestFixture extends AbstractUnitTestFixture  {
    protected PassiveMonster monster;

    protected TestingEventListener eventListener;

    protected void setup() {
        monster = monsterBuilder().build();
        eventListener = new TestingEventListener();
        monster.registerEventListener(eventListener);
    }

    protected PassiveMonster.PassiveMonsterBuilder monsterBuilder() {
        return PassiveMonster.builder().id(nextId())
                .coordinate(Coordinate.xy(1, 1))
                .direction(Direction.UP)
                .name("test")
                .realmMap(Mockito.mock(RealmMap.class))
                .attackSpeed(200)
                .recovery(100)
                .avoidance(0)
                .life(100)
                .wanderingRange(10)
                ;
    }
}
