package org.y1000.repository;

import org.y1000.entities.players.inventory.Bank;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class BankRepositoryImpl implements BankRepository{
    private final Map<Long, Bank> playerBankMap;

    public BankRepositoryImpl() {
        this.playerBankMap = new HashMap<>();
    }

    @Override
    public void save(long playerId, Bank bank) {
        if (bank != null)
            playerBankMap.put(playerId, bank);
    }

    @Override
    public Optional<Bank> find(long playerId) {
        return Optional.ofNullable(playerBankMap.get(playerId));
    }
}
