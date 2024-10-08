package org.y1000.entities.creatures.npc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class MerchantableImplTest extends AbstractNpcUnitTestFixture {

    private List<MerchantItem> buyItems;
    private List<MerchantItem> sellItems;

    private final ItemFactory itemFactory = createItemFactory();

    private Merchantable merchantable;

    @BeforeEach
    void setUp() {
        buyItems = new ArrayList<>();
        sellItems = new ArrayList<>();
        merchantable = new MerchantableImpl(buyItems, sellItems);
    }

    @Test
    void buy() {
        var player = Mockito.mock(Player.class);
        Inventory inventory = new Inventory();
        buyItems.add(new MerchantItem("皮", 7));
        buyItems.add(new MerchantItem("肉", 5));
        inventory.put(itemFactory.createItem("肉", 2));
        inventory.put(itemFactory.createItem("皮", 1));
        when(player.inventory()).thenReturn(inventory);
        List<TradeItem> items = List.of(new TradeItem("肉", 1, 1), new TradeItem("皮", 1, 2));
        merchantable.buy(player, items, itemFactory::createMoney);
        assertTrue(inventory.findFirstStackItem(ItemType.MONEY_NAME).isPresent());
        inventory.findFirstStackItem(ItemType.MONEY_NAME).ifPresent(money -> assertEquals(12, money.number()));
        assertTrue(inventory.findFirstStackItem("皮").isEmpty());
        assertTrue(inventory.findFirstStackItem("肉").isPresent());
        inventory.findFirstStackItem("肉").ifPresent( meat -> assertEquals(1, meat.number()));
    }

    @Test
    void sell() {
        var player = Mockito.mock(Player.class);
        Inventory inventory = new Inventory();
        when(player.inventory()).thenReturn(inventory);
        inventory.put(itemFactory.createMoney(41));
        sellItems.add(new MerchantItem("生药", 20));
        sellItems.add(new MerchantItem("丸药", 10));
        List<TradeItem> items = List.of(new TradeItem("生药", 1, 3), new TradeItem("丸药", 2, 4));
        merchantable.sell(player, items, itemFactory::createItem);
        assertTrue(inventory.findFirstStackItem(ItemType.MONEY_NAME).isPresent());
        inventory.findFirstStackItem(ItemType.MONEY_NAME).ifPresent(money -> assertEquals(1, money.number()));
    }

}