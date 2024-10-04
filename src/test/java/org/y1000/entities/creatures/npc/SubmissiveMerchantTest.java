package org.y1000.entities.creatures.npc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.TestingEventListener;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.monster.TestingMonsterAttributeProvider;
import org.y1000.entities.creatures.npc.AI.SubmissiveWanderingAI;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.item.ItemFactory;
import org.y1000.item.ItemType;
import org.y1000.realm.RealmMap;
import org.y1000.trade.TradeItem;
import org.y1000.util.Coordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SubmissiveMerchantTest extends AbstractNpcUnitTestFixture {
    private SubmissiveWanderingAI ai;

    private SubmissiveMerchant merchant;

    private RealmMap map;

    private final ItemFactory itemFactory = createItemFactory();

    private Merchantable merchantable;

    @BeforeEach
    void setUp() {
        var testingMonsterAttributeProvider = new TestingMonsterAttributeProvider();
        ai = new SubmissiveWanderingAI(Coordinate.xy(1, 1), Coordinate.Empty);
        map = Mockito.mock(RealmMap.class);
        merchantable = Mockito.mock(Merchantable.class);
        merchant = SubmissiveMerchant.builder()
                .id(nextId())
                .realmMap(map)
                .stateMillis(MONSTER_STATE_MILLIS)
                .name("merchant")
                .ai(ai)
                .attributeProvider(testingMonsterAttributeProvider)
                .direction(Direction.DOWN)
                .stateMillis(MONSTER_STATE_MILLIS)
                .coordinate(Coordinate.xy(3, 3))
                .merchantable(merchantable)
                .fileName("test")
                .build();
    }

    @Test
    void buy() {
        var player = Mockito.mock(Player.class);
        List<TradeItem> items = List.of(new TradeItem("肉", 1, 1), new TradeItem("皮", 1, 2));
        merchant.buy(player, items, itemFactory::createMoney);
        verify(merchantable, times(1)).buy(any(Player.class), anyList(), any(Function.class), any(Coordinate.class));
    }

    @Test
    void sell() {
        var player = Mockito.mock(Player.class);
        List<TradeItem> items = List.of(new TradeItem("生药", 1, 3), new TradeItem("丸药", 2, 4));
        merchant.sell(player, items, itemFactory::createItem);
        verify(merchantable, times(1)).sell(any(Player.class), anyList(), any(BiFunction.class), any(Coordinate.class));
    }
}