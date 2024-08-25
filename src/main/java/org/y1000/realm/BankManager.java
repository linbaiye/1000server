package org.y1000.realm;

import org.y1000.entities.players.Player;
import org.y1000.message.clientevent.ClientOperateBankEvent;

interface BankManager {
    void handle(Player player, ClientOperateBankEvent event);
}
