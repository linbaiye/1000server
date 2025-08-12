package org.y1000.input;

import org.y1000.entities.Entity;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.creatures.npc.NpcBankAbility;
import org.y1000.entities.players.Player;
import org.y1000.network.gen.BankOperationInputPacket;

public record BankOperationInput(long id, int type, int fromSlot, int toSlot, int number) implements EntityInteractInput {

    private static final int InventoryToBank = 1;
    private static final int Swap = 2;
    private static final int BankToInventory = 3;
    private static final int InventoryToBankAvailable = 4;
    private static final int BankToInventoryAvailable = 5;
    private static final int RightClick = 6;
    @Override
    public long interactId() {
        return id;
    }

    @Override
    public void onEntityFound(Player player, Entity entity) {
        if (!(entity instanceof Npc npc))
            return;
        npc.findAbility(NpcBankAbility.class).ifPresent(npcBankAbility -> {
            if (type == Swap)
                npcBankAbility.move(npc, player, fromSlot, toSlot);
            else if (type == InventoryToBank)
                npcBankAbility.inventoryToBank(npc, player, fromSlot, toSlot, number);
            else if (type == InventoryToBankAvailable)
                npcBankAbility.inventoryToBank(npc, player, fromSlot, number);
            else if (type == BankToInventory)
                npcBankAbility.bankToInventory(npc, player, fromSlot, toSlot, number);
            else if (type == BankToInventoryAvailable)
                npcBankAbility.bankToInventory(npc, player, fromSlot,number);
            else if (type == RightClick)
                npcBankAbility.onRightClickSlot(npc, player, fromSlot);
        });
    }

    public static BankOperationInput fromPacket(BankOperationInputPacket packet) {
        return new BankOperationInput(packet.getNpcId(), packet.getType(), packet.getFromSlot(), packet.getToSlot(), packet.getNumber());
    }
}
