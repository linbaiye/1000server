package org.y1000.entities.creatures.npc;

import org.y1000.entities.HurtAbility;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.*;
import org.y1000.entities.players.inventory.AbstractInventory;
import org.y1000.entities.players.inventory.Bank;
import org.y1000.item.Item;
import org.y1000.item.StackItem;
import org.y1000.repository.BankRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NpcBankAbility implements NpcInteractAbility, NpcAnimatedAwareAbility {

    private static final String menuAction = "我的福袋";

    private final BankRepository bankRepository;

    private final Map<Player, Bank> transactions;

    public NpcBankAbility(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
        this.transactions = new HashMap<>();
    }

    @Override
    public void decorateMenuActions(List<String> menuActions) {
        menuActions.add(menuAction);
    }

    @Override
    public boolean supportsAction(String name) {
        return name.equals(menuAction);
    }

    public void inventoryToBank(Npc npc, Player player, int inventorySlot, int number) {
        if (stateOrDistanceInvalid(player, npc))
            return;
        Bank bank = transactions.get(player);
        if (bank == null)
            return;
        if (moveToEmptySlot(player, player.inventory(), bank, inventorySlot, number, "福袋已满") != 0) {
            player.sendEvent(UpdateInventorySlotMessage.update(player, inventorySlot));
            player.sendEvent(PlayerShowBankMessage.of(player, npc.id(), bank));
        }
        bankRepository.save(player.id(), bank);
    }

    private int moveToEmptySlot(Player player, AbstractInventory fromInventory,
                                 AbstractInventory toInventory,
                                 int fromSlot, int number,
                                 String fullTip) {
        if (!fromInventory.hasEnough(fromSlot, number)) {
            player.sendEvent(PlayerTextMessage.systip(player, "数量不足。"));
            return 0;
        }
        Item fromitem = fromInventory.getItem(fromSlot);
        if (!toInventory.canAdd(fromitem)) {
            player.sendEvent(PlayerTextMessage.systip(player, fullTip));
            return 0;
        }
        Item removedItem = fromInventory.remove(fromSlot, number);
        int addedSlot = toInventory.add(removedItem);
        removedItem.eventSound().ifPresent(s -> player.sendEvent(PlayerSoundEvent.toSelf(player, s)));
        return addedSlot;
    }


    public void bankToInventory(Npc npc, Player player, int bankSlot, int number) {
        if (stateOrDistanceInvalid(player, npc))
            return;
        Bank bank = transactions.get(player);
        if (bank == null)
            return;
        var addedSlot = moveToEmptySlot(player, bank, player.inventory(), bankSlot, number, "物品栏已满。");
        if (addedSlot != 0) {
            player.sendEvent(UpdateInventorySlotMessage.update(player, addedSlot));
            player.sendEvent(PlayerShowBankMessage.of(player, npc.id(), bank));
        }
        bankRepository.save(player.id(), bank);
    }


    private boolean moveItem(Player player,
                             AbstractInventory fromInventory,
                             AbstractInventory toInventory,
                             int fromSlot,
                             int toSlot,
                             int number,
                             String fullTip) {
        if (!fromInventory.hasEnough(fromSlot, number)) {
            player.sendEvent(PlayerTextMessage.systip(player, "数量不足。"));
            return false;
        }
        Item fromItem = fromInventory.getItem(fromSlot);
        if (!(fromItem instanceof StackItem)) {
            fromItem = fromInventory.remove(fromSlot);
            if (toInventory.canAdd(toSlot, fromItem)) {
                toInventory.add(toSlot, fromItem);
            } else {
                Item toItem = toInventory.remove(toSlot);
                fromInventory.add(fromSlot, toItem);
                toInventory.add(toSlot, fromItem);
            }
            return true;
        }
        if (!toInventory.canAdd(toSlot, fromItem.name(), number)) {
            player.sendEvent(PlayerTextMessage.systip(player, fullTip));
            return false;
        }
        Item moved = fromInventory.remove(fromSlot, number);
        toInventory.add(toSlot, moved);
        return true;
    }

    public void inventoryToBank(Npc npc, Player player, int inventorySlot, int bankSlot, int number) {
        if (stateOrDistanceInvalid(player, npc))
            return;
        Bank bank = transactions.get(player);
        if (bank == null || !bank.isSlotOpen(bankSlot))
            return;
        var item = player.inventory().getItem(inventorySlot);
        if (moveItem(player, player.inventory(), bank, inventorySlot, bankSlot, number, "福袋已满。")) {
            item.eventSound().ifPresent(s -> player.sendEvent(PlayerSoundEvent.toSelf(player, s)));
            player.sendEvent(UpdateInventorySlotMessage.update(player, inventorySlot));
            player.sendEvent(PlayerShowBankMessage.of(player, npc.id(), bank));
            bankRepository.save(player.id(), bank);
        }
    }

    public void bankToInventory(Npc npc, Player player, int bankSlot, int inventorySlot, int number) {
        if (stateOrDistanceInvalid(player, npc))
            return;
        Bank bank = transactions.get(player);
        if (bank == null)
            return;
        var item = bank.getItem(bankSlot);
        if (moveItem(player, bank, player.inventory(), bankSlot, inventorySlot, number, "物品栏已满。")) {
            item.eventSound().ifPresent(s -> player.sendEvent(PlayerSoundEvent.toSelf(player, s)));
            player.sendEvent(UpdateInventorySlotMessage.update(player, inventorySlot));
            player.sendEvent(PlayerShowBankMessage.of(player, npc.id(), bank));
            bankRepository.save(player.id(), bank);
        }
    }

    public void move(Npc npc, Player player, int from, int to) {
        if (stateOrDistanceInvalid(player, npc))
            return;
        Bank bank = transactions.get(player);
        if (bank == null)
            return;
        Item fromItem = bank.getItem(from);
        if (fromItem == null || !bank.move(from, to))
            return;
        player.sendEvent(PlayerShowBankMessage.of(player, npc.id(), bank));
        fromItem.eventSound().ifPresent(s -> player.sendEvent(PlayerSoundEvent.toSelf(player, s)));
        bankRepository.save(player.id(), bank);
    }

    public void unlock(Npc npc, Player player) {
        if (stateOrDistanceInvalid(player, npc))
            return;
        Bank bank = transactions.get(player);
        if (bank == null)
            return;
        if (bank.getUnlocked() == bank.capacity()) {
            player.sendEvent(PlayerTextMessage.systip(player, "福袋已全部解锁。"));
            return;
        }
        if (!player.inventory().hasEnoughMoney(10000)) {
            player.sendEvent(PlayerTextMessage.systip(player, "需要10000钱币。"));
            return;
        }
        int slot  = player.inventory().decreaseMoney(10000);
        bank.unlock();
        player.sendEvent(UpdateInventorySlotMessage.update(player, slot));
        player.sendEvent(PlayerShowBankMessage.of(player, npc.id(), bank));
        bankRepository.save(player.id(), bank);
    }

    @Override
    public void onAbilityClicked(Player player, Npc npc, String abilityName) {
        if (stateOrDistanceInvalid(player, npc) || !menuAction.equals(abilityName))
            return;
        var bank = transactions.get(player);
        if (bank == null) {
            bank = bankRepository.find(player.id()).orElse(Bank.open());
            transactions.put(player, bank);
        }
        player.sendEvent(PlayerShowBankMessage.of(player, npc.id(), bank));
    }

    public void onRightClickSlot(Npc npc, Player player, int slot) {
        if (stateOrDistanceInvalid(player, npc))
            return;
        Bank bank = transactions.get(player);
        if (bank == null || bank.getItem(slot) == null)
            return;
        Item item = bank.getItem(slot);
        player.sendEvent(ItemDescriptionMessage.bankWindow(player, slot, item));
    }


    @Override
    public void onStateChanged(Npc npc) {
        if (npc.findAbility(HurtAbility.class).map(HurtAbility::isDead).orElse(false)) {
            transactions.clear();
        } else {
            transactions.entrySet().removeIf(e -> e.getKey().isLeftRealm());
        }
    }
}
