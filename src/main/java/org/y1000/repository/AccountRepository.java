package org.y1000.repository;

import jakarta.persistence.EntityManager;
import org.y1000.account.Account;

import java.util.Optional;

public interface AccountRepository {
    Optional<Account> find(EntityManager entityManager, String name);

    void save(EntityManager entityManager, Account account);
}
