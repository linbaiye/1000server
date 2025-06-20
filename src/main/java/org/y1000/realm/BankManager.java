package org.y1000.realm;

import org.y1000.entities.players.Player;
import org.y1000.message.input.ClientOperateBankEvent;

interface BankManager {
    void handle(Player player, ClientOperateBankEvent event);
}
