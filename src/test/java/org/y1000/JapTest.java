package org.y1000;

import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
public class JapTest {
    private EntityManagerFactory entityManagerFactory;

    @BeforeEach
    void setUp() {
        // A SessionFactory is set up once for an application!
        try {
            entityManagerFactory = Persistence.createEntityManagerFactory("org.y1000.test");
        }
        catch (Exception e) {
            // The registry would be destroyed by the SessionFactory, but we
            // had trouble building the SessionFactory so destroy it manually.
            log.error("Exception ", e);
        }
    }

    @Test
    void name() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            transaction.commit();
        }
        catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
        finally {
            entityManager.close();
        }
    }

}
