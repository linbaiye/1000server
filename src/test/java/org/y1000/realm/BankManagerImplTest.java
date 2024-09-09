package org.y1000.realm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.TestingEventListener;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.npc.Banker;
import org.y1000.entities.creatures.npc.NpcFactory;
import org.y1000.entities.creatures.npc.NpcFactoryImpl;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.AbstractPlayerEvent;
import org.y1000.entities.players.event.PlayerOpenBankEvent;
import org.y1000.entities.players.inventory.Bank;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.item.*;
import org.y1000.kungfu.KungFuSdb;
import org.y1000.message.PlayerMoveEvent;
import org.y1000.message.clientevent.ClientOperateBankEvent;
import org.y1000.message.serverevent.UpdateBankEvent;
import org.y1000.message.serverevent.UpdateInventorySlotEvent;
import org.y1000.network.gen.InventoryItemPacket;
import org.y1000.network.gen.OpenBankPacket;
import org.y1000.network.gen.UpdateBankPacket;
import org.y1000.repository.BankRepository;
import org.y1000.sdb.*;
import org.y1000.util.Coordinate;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BankManagerImplTest extends AbstractUnitTestFixture  {

    private BankManagerImpl bankManager;
    private EntityEventSender eventSender;
    private NpcManager npcManager;
    private BankRepository bankRepository;
    private Player player;
    private TestingEventListener testingEventListener;
    private final ItemFactory itemFactory = createItemFactory();
    private final NpcFactory npcFactory = new NpcFactoryImpl(ActionSdb.INSTANCE, MonstersSdbImpl.INSTANCE, KungFuSdb.INSTANCE, NpcSdbImpl.Instance,
            MagicParamSdb.INSTANCE, new MerchantItemSdbRepositoryImpl(ItemSdbImpl.INSTANCE));
    private Inventory inventory;

    private Banker banker;

    @BeforeEach
    void setUp() {
        eventSender = Mockito.mock(EntityEventSender.class);
        npcManager = Mockito.mock(NpcManager.class);
        bankRepository = Mockito.mock(BankRepository.class);
        bankManager = new BankManagerImpl(eventSender, npcManager, bankRepository);
        player = Mockito.mock(Player.class);
        when(player.coordinate()).thenReturn(Coordinate.xy(1, 1));
        inventory = new Inventory();
        when(player.inventory()).thenReturn(inventory);
        banker = (Banker) npcFactory.createNpc("仓库管理员", 2L, Mockito.mock(RealmMap.class), Coordinate.xy(2, 2));
        testingEventListener = new TestingEventListener();
        when(npcManager.find(2L, Banker.class)).thenReturn(Optional.of(banker));
        doAnswer(invocationOnMock -> {
            testingEventListener.onEvent(invocationOnMock.getArgument(0));
            return null;
        }).when(eventSender).notifySelf(any(AbstractPlayerEvent.class));
    }

    @Test
    void start() {
        bankManager.handle(player, new ClientOperateBankEvent(ClientOperateBankEvent.Operation.OPEN, banker.id(), 0, 0, 0));
        OpenBankPacket openBank = testingEventListener.removeFirst(PlayerOpenBankEvent.class).toPacket().getOpenBank();
        assertEquals(Bank.open().capacity(), openBank.getCapacity());
        assertEquals(Bank.open().getUnlocked(), openBank.getUnlocked());
    }

    @Test
    void whenMoveFarFromBanker() {
        bankManager.handle(player, new ClientOperateBankEvent(ClientOperateBankEvent.Operation.OPEN, banker.id(), 0, 0, 0));
        testingEventListener.clearEvents();
        when(player.coordinate()).thenReturn(Coordinate.xy(100, 100));
        bankManager.onEvent(new PlayerMoveEvent(player, Direction.RIGHT, Coordinate.xy(100, 100)));
        UpdateBankPacket updateBank = testingEventListener.removeFirst(UpdateBankEvent.class).toPacket().getUpdateBank();
        assertEquals(2, updateBank.getType());
    }

    @Test
    void unlock() {
        var item = itemFactory.createItem("福袋", 4);
        Inventory inventory = new Inventory();
        int add = inventory.add(item);
        when(player.inventory()).thenReturn(inventory);
        AtomicReference<Bank> savedBank = new AtomicReference<>();
        doAnswer(invocationOnMock -> {
            savedBank.set(invocationOnMock.getArgument(1));
            return null;
        }).when(bankRepository).save(anyLong(), any(Bank.class));
        bankManager.handle(player, ClientOperateBankEvent.unlock(add));
        assertEquals(10, savedBank.get().getUnlocked());
        verify(eventSender, times(1)).notifySelf(any(AbstractPlayerEvent.class));
        assertEquals(3, inventory.getStackItem(add, Item.class).get().number());
    }

    @Test
    void inventoryToBank() {
        var item = itemFactory.createItem("福袋", 4);
        int slot = inventory.add(item);
        Bank bank = Bank.open();
        bank.unlock();
        when(bankRepository.find(anyLong())).thenReturn(Optional.of(bank));
        bankManager.handle(player, ClientOperateBankEvent.open(banker.id()));
        bankManager.handle(player, new ClientOperateBankEvent(ClientOperateBankEvent.Operation.INVENTORY_TO_BANK, 0, slot, 1, 1));
        assertEquals("福袋", bank.getItem(1).name());
        InventoryItemPacket updateSlot = testingEventListener.removeFirst(UpdateInventorySlotEvent.class).toPacket().getUpdateSlot();
        assertEquals(slot, updateSlot.getSlotId());
        assertEquals(3, inventory.getStackItem(slot, Item.class).get().number());
        assertEquals(1, ((StackItem) bank.getItem(1)).number());
        UpdateBankPacket updateBank = testingEventListener.removeFirst(UpdateBankEvent.class).toPacket().getUpdateBank();
        assertEquals(1, updateBank.getUpdateSlot().getSlotId());
        assertEquals("福袋", updateBank.getUpdateSlot().getName());

        slot = inventory.add(itemFactory.createItem("长剑"));
        bankManager.handle(player, new ClientOperateBankEvent(ClientOperateBankEvent.Operation.INVENTORY_TO_BANK, 0, slot, 2, 1));
        assertEquals("长剑", bank.getItem(2).name());
        assertNull(inventory.getItem(slot));
    }

    @Test
    void bankToInventory() {
        Bank bank = new Bank(40, 40);
        bank.put(1, itemFactory.createItem("长剑"));
        when(bankRepository.find(anyLong())).thenReturn(Optional.of(bank));
        bankManager.handle(player, ClientOperateBankEvent.open(banker.id()));
        bankManager.handle(player, new ClientOperateBankEvent(ClientOperateBankEvent.Operation.BANK_TO_INVENTORY, 0, 1, 1, 1));
        assertNull(bank.getItem(1));
        assertEquals("长剑", inventory.getItem(1).name());
        assertNotNull(testingEventListener.removeFirst(UpdateBankEvent.class));
        assertNotNull(testingEventListener.removeFirst(UpdateInventorySlotEvent.class));

        testingEventListener.clearEvents();
        bank.put(2, itemFactory.createItem("生药", 10));
        bankManager.handle(player, new ClientOperateBankEvent(ClientOperateBankEvent.Operation.BANK_TO_INVENTORY, 0, 2, 2, 1));
        StackItem stackItem = (StackItem) bank.getItem(2);
        assertEquals(9, stackItem.number());
        assertEquals(1, inventory.getStackItem(2, Pill.class).get().number());
        assertNotNull(testingEventListener.removeFirst(UpdateBankEvent.class));
        assertNotNull(testingEventListener.removeFirst(UpdateInventorySlotEvent.class));
    }
}