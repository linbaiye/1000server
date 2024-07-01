package org.y1000.entities.creatures.npc;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.y1000.TestingEventListener;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.monster.TestingMonsterAttributeProvider;
import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

class DevirtueMerchantTest extends AbstractNpcUnitTestFixture {
    private DevirtueMerchantAI ai;

    private DevirtueMerchant merchant;

    private TestingMonsterAttributeProvider testingMonsterAttributeProvider;

    private RealmMap map;

    private TestingEventListener eventListener;

    @BeforeEach
    void setUp() {
        testingMonsterAttributeProvider = new TestingMonsterAttributeProvider();
        testingMonsterAttributeProvider.life = 10000;
        ai = new DevirtueMerchantAI(Coordinate.xy(1, 1), Coordinate.Empty);
        map = Mockito.mock(RealmMap.class);
        eventListener = new TestingEventListener();
        merchant = DevirtueMerchant.builder()
                .id(nextId())
                .realmMap(map)
                .stateMillis(MONSTER_STATE_MILLIS)
                .name("merchant")
                .ai(ai)
                .attributeProvider(testingMonsterAttributeProvider)
                .direction(Direction.DOWN)
                .stateMillis(MONSTER_STATE_MILLIS)
                .coordinate(Coordinate.xy(3, 3))
                .build();
        merchant.registerEventListener(eventListener);
    }

}