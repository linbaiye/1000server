package org.y1000.repository;

import jakarta.persistence.EntityManager;
import org.y1000.entities.players.inventory.Bank;

import java.util.Optional;

public interface BankRepository {
    void save(long playerId, Bank bank);

    Optional<Bank> find(long playerId);

    void save(EntityManager entityManager, Bank bank);
}
