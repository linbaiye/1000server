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

class BuyInteractabilityTest extends AbstractUnitTestFixture {

    private List<MerchantItem> buyItems;

    private final ItemFactory itemFactory = createItemFactory();

    @BeforeEach
    void setUp() {
        buyItems = new ArrayList<>();
        buyItems.add(new MerchantItem("皮", 7));
        buyItems.add(new MerchantItem("肉", 5));
    }

    @Test
    void buy() {
        var player = Mockito.mock(Player.class);
        BuyInteractability buyInteractability = new BuyInteractability(buyItems);
        Inventory inventory = new Inventory();
        inventory.put(itemFactory.createItem("肉", 2));
        inventory.put(itemFactory.createItem("皮", 1));
        when(player.inventory()).thenReturn(inventory);
        List<TradeItem> items = List.of(new TradeItem("肉", 1, 1), new TradeItem("皮", 1, 2));
        buyInteractability.buy(player, items, itemFactory::createMoney);
        assertTrue(inventory.findFirstStackItem(ItemType.MONEY_NAME).isPresent());
        inventory.findFirstStackItem(ItemType.MONEY_NAME).ifPresent(money -> assertEquals(12, money.number()));
        assertTrue(inventory.findFirstStackItem("皮").isEmpty());
        assertTrue(inventory.findFirstStackItem("肉").isPresent());
        inventory.findFirstStackItem("肉").ifPresent( meat -> assertEquals(1, meat.number()));
    }

    @Test
    void playerSeeingName() {
        assertEquals("卖物品", new BuyInteractability(buyItems).playerSeeingName());
    }

    @Test
    void interact() {
    }
}