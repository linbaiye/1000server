package org.y1000.entities.creatures.npc;

import org.y1000.entities.players.Player;

import java.util.List;

public class NpcBankAbility implements NpcInteractAbility, CooldownAbility {

    private static final String menuAction = "查看仓库";

    private int counter = 100;

    @Override
    public void decorateMenuActions(List<String> menuActions) {
        menuActions.add(menuAction);
    }

    @Override
    public boolean supportsAction(String name) {
        return name.equals(menuAction);
    }

    public void deposit(Player player, int inventorySlot, int bankSlot) {

    }

    public void withdraw(Player player, int bankSlot, int inventorySlot) {

    }

    @Override
    public void interact(Player player, Npc npc, String abilityName) {
        if (stateOrDistanceInvalid(player, npc) || !menuAction.equals(abilityName))
            return;
    }

    @Override
    public void cooldown(int delta) {
        if (--counter > 0)
            return;

    }
}
