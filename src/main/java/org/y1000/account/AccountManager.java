package org.y1000.account;

import jakarta.persistence.EntityManagerFactory;
import org.apache.commons.lang3.Validate;

public final class AccountManager {

    private final EntityManagerFactory entityManagerFactory;

    public AccountManager(EntityManagerFactory entityManagerFactory) {
        Validate.notNull(entityManagerFactory);
        this.entityManagerFactory = entityManagerFactory;
    }

    public void register(String username, String passwd) {
        
    }
}
