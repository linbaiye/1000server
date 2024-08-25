package org.y1000.realm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.y1000.TestingEventListener;
import org.y1000.entities.creatures.npc.Banker;
import org.y1000.entities.creatures.npc.Merchant;
import org.y1000.entities.creatures.npc.NpcFactory;
import org.y1000.entities.creatures.npc.NpcFactoryImpl;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.AbstractPlayerEvent;
import org.y1000.entities.players.event.PlayerOpenBankEvent;
import org.y1000.entities.players.inventory.Bank;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.event.EntityEvent;
import org.y1000.item.AbstractItemUnitTestFixture;
import org.y1000.item.ItemSdbImpl;
import org.y1000.kungfu.KungFuSdb;
import org.y1000.message.clientevent.ClientOperateBankEvent;
import org.y1000.network.gen.OpenBankPacket;
import org.y1000.repository.BankRepository;
import org.y1000.sdb.*;
import org.y1000.util.Coordinate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BankManagerImplTest extends AbstractItemUnitTestFixture {

    private BankManager bankManager;
    private EntityEventSender eventSender;
    private NpcManager npcManager;
    private BankRepository bankRepository;
    private Player player;
    private TestingEventListener testingEventListener;
    private final NpcFactory npcFactory = new NpcFactoryImpl(ActionSdb.INSTANCE, MonstersSdbImpl.INSTANCE, KungFuSdb.INSTANCE, NpcSdbImpl.Instance,
            MagicParamSdb.INSTANCE, new MerchantItemSdbRepositoryImpl(ItemSdbImpl.INSTANCE));

    private Banker banker;

    @BeforeEach
    void setUp() {
        eventSender = Mockito.mock(EntityEventSender.class);
        npcManager = Mockito.mock(NpcManager.class);
        bankRepository = Mockito.mock(BankRepository.class);
        bankManager = new BankManagerImpl(eventSender, npcManager, bankRepository);
        player = Mockito.mock(Player.class);
        when(player.coordinate()).thenReturn(Coordinate.xy(1, 1));
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
    void unlock() {
        var item = itemFactory.createItem("福袋", 4);
        Inventory inventory = new Inventory();
        int add = inventory.add(item);
        when(player.inventory()).thenReturn(inventory);
        bankManager.handle(player, ClientOperateBankEvent.unlock(add));
    }
}