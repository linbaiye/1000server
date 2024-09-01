package org.y1000.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountRepositoryImplTest {

    private AccountRepositoryImpl accountRepository;

    private TestingEntityManager entityManager;

    @BeforeEach
    void setUp() {
        accountRepository = new AccountRepositoryImpl();
        entityManager = new TestingEntityManager();
    }

    @Test
    void name() {

    }
}