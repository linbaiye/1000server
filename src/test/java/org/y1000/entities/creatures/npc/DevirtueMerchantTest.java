package org.y1000.entities.creatures.npc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.TestingEventListener;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.monster.TestingMonsterAttributeProvider;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.item.ItemFactory;
import org.y1000.item.ItemSdbImpl;
import org.y1000.item.ItemType;
import org.y1000.realm.RealmMap;
import org.y1000.repository.ItemRepositoryImpl;
import org.y1000.repository.KungFuBookRepositoryImpl;
import org.y1000.sdb.ItemDrugSdbImpl;
import org.y1000.trade.TradeItem;
import org.y1000.util.Coordinate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class DevirtueMerchantTest extends AbstractNpcUnitTestFixture {
    private DevirtueMerchantAI ai;

    private DevirtueMerchant merchant;

    private TestingMonsterAttributeProvider testingMonsterAttributeProvider;

    private RealmMap map;

    private TestingEventListener eventListener;

    private List<MerchantItem> buyItems;
    private List<MerchantItem> sellItems;

    private final ItemFactory itemFactory = new ItemRepositoryImpl(ItemSdbImpl.INSTANCE, ItemDrugSdbImpl.INSTANCE, new KungFuBookRepositoryImpl());

    @BeforeEach
    void setUp() {
        testingMonsterAttributeProvider = new TestingMonsterAttributeProvider();
        testingMonsterAttributeProvider.life = 10000;
        ai = new DevirtueMerchantAI(Coordinate.xy(1, 1), Coordinate.Empty);
        map = Mockito.mock(RealmMap.class);
        eventListener = new TestingEventListener();
        buyItems = new ArrayList<>();
        sellItems = new ArrayList<>();
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
                .buy(buyItems)
                .sell(sellItems)
                .build();
        merchant.registerEventListener(eventListener);
    }

    @Test
    void buy() {
        var player = Mockito.mock(Player.class);
        when(player.canBeSeenAt(any(Coordinate.class))).thenReturn(true);
        Inventory inventory = new Inventory();
        buyItems.add(new MerchantItem("皮", 7));
        buyItems.add(new MerchantItem("肉", 5));
        inventory.add(itemFactory.createItem("肉", 2));
        inventory.add(itemFactory.createItem("皮", 1));
        when(player.inventory()).thenReturn(inventory);
        List<TradeItem> items = List.of(new TradeItem("肉", 1, 1), new TradeItem("皮", 1, 2));
        merchant.buy(player, items, itemFactory::createMoney);
        assertTrue(inventory.findFirstStackItem(ItemType.MONEY_NAME).isPresent());
        inventory.findFirstStackItem(ItemType.MONEY_NAME).ifPresent(money -> assertEquals(12, money.number()));
        assertTrue(inventory.findFirstStackItem("皮").isEmpty());
        assertTrue(inventory.findFirstStackItem("肉").isPresent());
        inventory.findFirstStackItem("肉").ifPresent( meat -> assertEquals(1, meat.number()));
    }

    @Test
    void sell() {
        var player = Mockito.mock(Player.class);
        when(player.canBeSeenAt(any(Coordinate.class))).thenReturn(true);
        Inventory inventory = new Inventory();
        when(player.inventory()).thenReturn(inventory);
        inventory.add(itemFactory.createMoney(41));
        sellItems.add(new MerchantItem("生药", 20));
        sellItems.add(new MerchantItem("丸药", 10));
        List<TradeItem> items = List.of(new TradeItem("生药", 1, 3), new TradeItem("丸药", 2, 4));
        merchant.sell(player, items, itemFactory::createItem);
        assertTrue(inventory.findFirstStackItem(ItemType.MONEY_NAME).isPresent());
        inventory.findFirstStackItem(ItemType.MONEY_NAME).ifPresent(money -> assertEquals(1, money.number()));
    }
}