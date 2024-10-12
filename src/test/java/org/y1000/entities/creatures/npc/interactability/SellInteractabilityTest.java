package org.y1000.entities.creatures.npc.interactability;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.entities.creatures.npc.MerchantItem;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.item.ItemFactory;
import org.y1000.item.ItemType;
import org.y1000.trade.TradeItem;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class SellInteractabilityTest extends AbstractUnitTestFixture {


    private List<MerchantItem> sellItems;

    private final ItemFactory itemFactory = createItemFactory();

    @BeforeEach
    void setUp() {
        sellItems = new ArrayList<>();
        sellItems.add(new MerchantItem("生药", 20));
        sellItems.add(new MerchantItem("丸药", 10));
    }

    @Test
    void sell() {
        var player = Mockito.mock(Player.class);
        Inventory inventory = new Inventory();
        when(player.inventory()).thenReturn(inventory);
        SellInteractability sellInteractability = new SellInteractability(sellItems);
        inventory.put(itemFactory.createMoney(41));
        List<TradeItem> items = List.of(new TradeItem("生药", 1, 3), new TradeItem("丸药", 2, 4));
        sellInteractability.sell(player, items, itemFactory::createItem);
        assertTrue(inventory.findFirstStackItem(ItemType.MONEY_NAME).isPresent());
        inventory.findFirstStackItem(ItemType.MONEY_NAME).ifPresent(money -> assertEquals(1, money.number()));
    }

    @Test
    void seeingName() {
        SellInteractability sellInteractability = new SellInteractability(sellItems);
        assertEquals("买物品", sellInteractability.playerSeeingName());
    }
}
