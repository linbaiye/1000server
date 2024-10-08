package org.y1000;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaQuery;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.exp.Experience;
import org.y1000.persistence.KungFuPo;

import java.time.LocalDateTime;

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
