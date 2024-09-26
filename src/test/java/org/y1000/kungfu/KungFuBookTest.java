package org.y1000.kungfu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.kungfu.attack.AttackKungFuType;
import org.y1000.kungfu.attack.SwordKungFu;
import org.y1000.repository.KungFuBookRepositoryImpl;

import static org.junit.jupiter.api.Assertions.*;

class KungFuBookTest extends AbstractUnitTestFixture {

    private KungFuBook book;

    private final KungFuBookRepositoryImpl bookFactory = createKungFuBookRepositoryImpl();

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
        assertNotNull(book.getKungFu(2, 1));
    }

    @Test
    void add() {
        AttackKungFu kungFu = bookFactory.createAttackKungFu("杨家枪法");
        assertEquals(1, book.addToBasic(kungFu));
        assertEquals(0, book.addToBasic(kungFu));
    }

    @Test
    void addToSlot() {
        AttackKungFu kungFu = bookFactory.createAttackKungFu("杨家枪法");
        book.addToBasic(kungFu);
        kungFu = bookFactory.createAttackKungFu("杨家枪法");
        assertFalse(book.addToBasic(2, kungFu));
        kungFu = bookFactory.createAttackKungFu("雷剑式");
        assertFalse(book.addToBasic(0, kungFu));
        assertFalse(book.addToBasic(1, kungFu));
        assertTrue(book.addToBasic(2, kungFu));
    }

    @Test
    void findBasicSlot() {
        assertEquals(0, book.findBasicSlot("风灵旋"));
        assertNotEquals(0, book.addToBasic(kungFuFactory.create("风灵旋")));
        assertNotEquals(0, book.findBasicSlot("风灵旋"));
    }

    @Test
    void swap() {
        assertFalse(book.swapSlot(2, 1, 2));
        assertFalse(book.swapSlot(1, 1, 2));
        book.addToBasic(kungFuFactory.create("风灵旋"));
        assertTrue(book.getKungFu(2, 1).isPresent());
        assertTrue(book.swapSlot(2, 1, 2));
        assertTrue(book.getKungFu(2, 1).isEmpty());
        assertEquals("风灵旋", book.getKungFu(2, 2).map(KungFu::name).orElse(null));
        book.addToBasic(kungFuFactory.create("雷剑式"));
        assertTrue(book.swapSlot(2, 1, 2));
        assertEquals("风灵旋", book.getKungFu(2, 1).map(KungFu::name).orElse(null));
        assertEquals("雷剑式", book.getKungFu(2, 2).map(KungFu::name).orElse(null));
    }
}