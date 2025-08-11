package org.y1000.account;

import org.y1000.message.account.AccountMessage;
import org.y1000.network.Connection;
import org.y1000.repository.DevPlayerRepository;

import java.util.Collections;
import java.util.List;

public class DevAccountManager implements AccountManager {

    private final DevPlayerRepository devPlayerRepository;

    public DevAccountManager(DevPlayerRepository devPlayerRepository) {
        this.devPlayerRepository = devPlayerRepository;
    }

    @Override
    public void handle(Connection connection, AccountMessage message) {

    }

    @Override
    public List<Long> getAllPlayerId(Connection connection) {
        return Collections.emptyList();
    }

    @Override
    public long[] loginCharacter(Connection connection, String charName) {
        return devPlayerRepository.getAvailablePlayer();
    }
}
