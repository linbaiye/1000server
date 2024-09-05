package org.y1000.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.Validate;
import org.y1000.account.Account;

import java.util.List;
import java.util.Optional;

public final class AccountRepositoryImpl implements AccountRepository {
    @Override
    public Optional<Account> find(EntityManager entityManager, String name) {
        Validate.notNull(entityManager);
        Validate.notNull(name);
        TypedQuery<Account> query = entityManager.createQuery("select a from Account a where a.userName = ?1", Account.class);
        query.setParameter(1, name);
        return Optional.ofNullable(query.getSingleResult());
    }

    @Override
    public void save(EntityManager entityManager,
                     Account account) {
        Validate.notNull(entityManager);
        Validate.notNull(account);
        entityManager.persist(account);
    }

}
