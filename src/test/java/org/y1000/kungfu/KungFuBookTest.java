package org.y1000.kungfu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.kungfu.attack.AttackKungFuType;
import org.y1000.repository.KungFuBookRepositoryImpl;

import static org.junit.jupiter.api.Assertions.*;

class KungFuBookTest {

    private KungFuBook book;

    private final KungFuBookFactory factory = new KungFuBookRepositoryImpl();

    @BeforeEach
    void setUp() {
        book = factory.create();
    }

    @Test
    void findUnnamed() {
        assertEquals(book.findUnnamedAttack(AttackKungFuType.SWORD).name(), "无名剑法");
        assertEquals(book.getUnnamedFoot().name(), "无名步法");
        assertEquals(book.getUnnamedProtection().name(), "无名强身");
    }
}