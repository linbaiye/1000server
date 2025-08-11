package org.y1000.account;

import org.y1000.message.account.AccountMessage;
import org.y1000.network.Connection;

import java.util.List;

public interface AccountManager {
    void handle(Connection connection, AccountMessage message);

    List<Long> getAllPlayerId(Connection connection);

    long[] loginCharacter(Connection connection, String charName);
}
