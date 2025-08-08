package org.y1000.entities.creatures.npc;

import org.y1000.entities.HurtAbility;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.PlayerShowBankMessage;
import org.y1000.entities.players.event.PlayerSoundEvent;
import org.y1000.entities.players.event.PlayerTextMessage;
import org.y1000.entities.players.event.UpdateInventorySlotMessage;
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

    private void swap(long npcId, Player player, Bank bank, int inventorySlot, int bankSlot) {
        Item inventoryItem = player.inventory().remove(inventorySlot);
        Item bankItem = bank.remove(bankSlot);
        if (inventoryItem != null)
            bank.add(bankSlot, inventoryItem);
        if (bankItem != null)
            player.inventory().add(inventorySlot, bankItem);
        player.sendEvent(UpdateInventorySlotMessage.update(player, inventorySlot));
        player.sendEvent(PlayerShowBankMessage.of(player, npcId, bank));
        if (bankItem != null) {
            bankItem.eventSound().ifPresent(s -> player.sendEvent(PlayerSoundEvent.toSelf(player, s)));
            return;
        }
        if (inventoryItem != null)
            inventoryItem.eventSound().ifPresent(s -> player.sendEvent(PlayerSoundEvent.toSelf(player, s)));
    }

    public void inventoryToBank(Npc npc, Player player, int inventorySlot, int number) {
        if (stateOrDistanceInvalid(player, npc))
            return;
        Bank bank = transactions.get(player);
        if (bank == null)
            return;
        if (!player.inventory().hasEnough(inventorySlot, number)) {
            player.sendEvent(PlayerTextMessage.systip(player, "数量不足。"));
            return;
        }
        Item inventoryItem = player.inventory().getItem(inventorySlot);
        if (!bank.canTake(inventoryItem.name(), number)) {
            player.sendEvent(PlayerTextMessage.systip(player, "福袋已满。"));
            return;
        }
        var removed = player.inventory().remove(inventorySlot, number);
        bank.add(removed);
        player.sendEvent(UpdateInventorySlotMessage.update(player, inventorySlot));
        player.sendEvent(PlayerShowBankMessage.of(player, npc.id(), bank));
        inventoryItem.eventSound().ifPresent(s -> player.sendEvent(PlayerSoundEvent.toSelf(player, s)));
        bankRepository.save(player.id(), bank);
    }

    public void inventoryToBank(Npc npc, Player player, int inventorySlot, int bankSlot, int number) {
        if (stateOrDistanceInvalid(player, npc))
            return;
        Bank bank = transactions.get(player);
        if (bank == null || !bank.isSlotUnlocked(bankSlot))
            return;
        if (!player.inventory().hasEnough(inventorySlot, number)) {
            player.sendEvent(PlayerTextMessage.systip(player, "数量不足。"));
            return;
        }
        Item inventoryItem = player.inventory().getItem(inventorySlot);
        Item bankItem = bank.getItem(bankSlot);
        if (!(bankItem instanceof StackItem) && !(inventoryItem instanceof StackItem)) {
            swap(npc.id(), player, bank, inventorySlot, bankSlot);
        }
        bankRepository.save(player.id(), bank);
    }

    public void swap(Npc npc, Player player, int from, int to) {
        if (stateOrDistanceInvalid(player, npc))
            return;
        Bank bank = transactions.get(player);
        if (bank == null)
            return;
        Item fromItem = bank.remove(from);
        if (fromItem == null)
            return;
        Item toItem = bank.remove(to);
        bank.add(to, fromItem);
        if (toItem != null)
            bank.add(from, toItem);
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
            player.sendEvent(PlayerTextMessage.systip(player, "需要10000钱币解锁。"));
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


    @Override
    public void onStateChanged(Npc npc) {
        if (npc.findAbility(HurtAbility.class).map(HurtAbility::isDead).orElse(false)) {

        }
    }
}
