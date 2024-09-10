package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.creatures.npc.Banker;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.PlayerOpenBankEvent;
import org.y1000.entities.players.inventory.AbstractInventory;
import org.y1000.entities.players.inventory.Bank;
import org.y1000.entities.players.inventory.Inventory;
import org.y1000.event.EntityEvent;
import org.y1000.event.EntityEventListener;
import org.y1000.item.Item;
import org.y1000.item.ItemType;
import org.y1000.item.StackItem;
import org.y1000.message.clientevent.ClientOperateBankEvent;
import org.y1000.message.serverevent.UpdateBankEvent;
import org.y1000.message.serverevent.UpdateInventorySlotEvent;
import org.y1000.repository.BankRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@Slf4j
final class BankManagerImpl implements EntityEventListener, BankManager {

    private record BankTransaction(Bank bank, Player player, Banker banker) {
    }

    private final Map<Player, BankTransaction> playerTransactionMap;

    private final EntityEventSender eventSender;

    private final NpcManager npcManager;

    private final BankRepository bankRepository;

    public BankManagerImpl(EntityEventSender eventSender,
                           NpcManager npcManager, BankRepository bankRepository) {
        this.bankRepository = bankRepository;
        this.playerTransactionMap = new HashMap<>();
        this.eventSender = eventSender;
        this.npcManager = npcManager;
    }

    @Override
    public void onEvent(EntityEvent entityEvent) {
        if (entityEvent == null || !(entityEvent.source() instanceof Player player)) {
            return;
        }
        BankTransaction transaction = playerTransactionMap.get(player);
        if (transaction == null) {
            return;
        }
        if (!transaction.banker().allowOperation(transaction.player())) {
            close(player);
        }
    }

    private void close(Player player) {
        BankTransaction transaction = playerTransactionMap.remove(player);
        if (transaction != null) {
            transaction.banker().deregisterEventListener(this);
            bankRepository.save(player.id(), transaction.bank());
            eventSender.notifySelf(UpdateBankEvent.close(player));
        }
        player.deregisterEventListener(this);
    }

    private void startTx(Player player, Banker banker) {
        Bank bank = bankRepository.find(player.id()).orElseGet(Bank::open);
        playerTransactionMap.put(player, new BankTransaction(bank, player, banker));
        banker.registerEventListener(this);
        player.registerEventListener(this);
        eventSender.notifySelf(new PlayerOpenBankEvent(player, bank));
    }

    private void start(long bankerId, Player player) {
        if (playerTransactionMap.containsKey(player)) {
            BankTransaction bankTransaction = playerTransactionMap.get(player);
            bankTransaction.banker().registerEventListener(this);
            player.registerEventListener(this);
            eventSender.notifySelf(new PlayerOpenBankEvent(player, bankTransaction.bank()));
        } else {
            npcManager.find(bankerId, Banker.class)
                    .filter(banker -> banker.allowOperation(player))
                    .ifPresent(b -> startTx(player, b));
        }
    }


    private void unlock(Player player, int slot) {
        if (playerTransactionMap.containsKey(player)) {
            return;
        }
        Item item = player.inventory().getItem(slot);
        if (item == null || item.itemType() != ItemType.BANK_INVENTORY) {
            return;
        }
        Bank bank = bankRepository.find(player.id()).orElseGet(Bank::open);
        if (!bank.canUnlock()) {
            return;
        }
        bank.unlock();
        player.inventory().decrease(slot);
        eventSender.notifySelf(UpdateInventorySlotEvent.update(player, slot));
        bankRepository.save(player.id(), bank);
    }


    @Override
    public void handle(Player player, ClientOperateBankEvent event) {
        if (event == null || player == null) {
            return;
        }
        if (event.isOpen()) {
            start(event.bankerId(), player);
        } else if (event.isUnlock()) {
            unlock(player, event.fromSlot());
        } else if (event.isInventoryToBank()) {
            inventoryToBank(player, event.fromSlot(), event.toSlot(), event.number());
        } else if (event.isClose()) {
            close(player);
        } else if (event.isBankToInventory()) {
            bankToInventory(player, event.fromSlot(), event.toSlot(), event.number());
        }
    }

    private boolean moveItem(AbstractInventory from, AbstractInventory to, int fromSlot, int toSlot, long number) {
        if (!from.hasEnough(fromSlot, number)) {
            return false;
        }
        Item item = from.getItem(fromSlot);
        if (item instanceof StackItem stackItem) {
            item = new StackItem(stackItem.item(), number);
        }
        if (!to.canPut(toSlot, item)) {
            return false;
        }
        Item removed = from.remove(fromSlot, number);
        to.put(toSlot, removed);
        return true;
    }

    private void swapItem(Player player, int inventorySlot, int bankSlot, BiFunction<Bank, Inventory, Boolean> swapper) {
        BankTransaction transaction = playerTransactionMap.get(player);
        if (transaction == null) {
            return;
        }
        var bank = transaction.bank();
        var inventory = player.inventory();
        if (!swapper.apply(bank, inventory)) {
            return;
        }
        eventSender.notifySelf(UpdateInventorySlotEvent.update(player, inventorySlot));
        eventSender.notifySelf(UpdateBankEvent.update(player, bank, bankSlot));
    }

    private void inventoryToBank(Player player, int inventorySlot, int bankSlot, long number) {
        swapItem(player, inventorySlot, bankSlot,
                (bank, inv) -> moveItem(inv, bank, inventorySlot, bankSlot, number));
    }

    public void bankToInventory(Player player, int bankSlot, int invSlot, long number) {
        swapItem(player, invSlot, bankSlot,
                (bank, inv) -> moveItem(bank, inv, bankSlot, invSlot, number));
    }
}
