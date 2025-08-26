package org.y1000.repository;

import org.y1000.entities.players.inventory.Bank;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class BankDevRepository implements BankRepository {

    private final Map<Long, Bank> banks = new ConcurrentHashMap<>();

    @Override
    public void save(long playerId, Bank bank) {
        banks.put(playerId, bank);
    }

    @Override
    public Optional<Bank> find(long playerId) {
        return Optional.ofNullable(banks.get(playerId));
    }
}
