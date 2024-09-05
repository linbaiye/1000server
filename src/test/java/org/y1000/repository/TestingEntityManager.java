package org.y1000.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

class TestingEntityManager {

    private EntityManagerFactory entityManagerFactory;

    private EntityManager entityManager;
    private EntityTransaction transaction;

    public TestingEntityManager() {
        entityManagerFactory = Persistence.createEntityManagerFactory("org.y1000.test");
    }


    public EntityManager beginTx() {
        entityManager = entityManagerFactory.createEntityManager();
        transaction = entityManager.getTransaction();
        transaction.begin();
        return entityManager;
    }

    public EntityManager entityManager() {
        return entityManager;
    }

    public EntityManager newEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    public void submitTx() {
        transaction.commit();
    }

    public void close() {
        entityManager.close();
    }
}
