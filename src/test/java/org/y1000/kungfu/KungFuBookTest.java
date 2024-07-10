package org.y1000.kungfu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.kungfu.attack.AttackKungFuType;
import org.y1000.kungfu.attack.SwordKungFu;
import org.y1000.repository.KungFuBookRepositoryImpl;

import static org.junit.jupiter.api.Assertions.*;

class KungFuBookTest {

    private KungFuBook book;

    private final KungFuBookRepositoryImpl bookFactory = new KungFuBookRepositoryImpl();

    private final KungFuFactory kungFuFactory = bookFactory;

    @BeforeEach
    void setUp() {
        book = bookFactory.create();
    }

    @Test
    void findKungFu() {
        assertEquals(book.findUnnamedAttack(AttackKungFuType.SWORD).name(), "无名剑法");
        assertEquals(book.getUnnamedFoot().name(), "无名步法");
        assertEquals(book.getUnnamedProtection().name(), "无名强身");
        book.addToBasic(SwordKungFu.builder().name("test").exp(0).build());
        assertNotNull(book.findKungFu(2, 1));
    }

    @Test
    void add() {
        AttackKungFu kungFu = bookFactory.createAttackKungFu("杨家枪法");
        assertEquals(1, book.addToBasic(kungFu));
        assertEquals(0, book.addToBasic(kungFu));
    }
}