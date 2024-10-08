package org.y1000.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.AbstractUnitTestFixture;
import org.y1000.entities.players.inventory.Bank;
import org.y1000.item.*;
import org.y1000.persistence.BankPo;
import org.y1000.persistence.ItemPo;
import org.y1000.sdb.ItemDrugSdbImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class BankRepositoryTest extends AbstractUnitTestFixture {

    private JpaFixture jpaFixture;
    private BankRepository bankRepository;
    private final ItemFactory itemFactory = createItemFactory();

    @BeforeEach
    void setUp() {
        jpaFixture = new JpaFixture();
        bankRepository = new ItemRepositoryImpl(ItemSdbImpl.INSTANCE, ItemDrugSdbImpl.INSTANCE, createKungFuBookRepositoryImpl(), jpaFixture.getEntityManagerFactory());
    }

    @Test
    void saveEmptyBank() {
        Bank bank = Bank.open();
        bankRepository.save(1L, bank);
        BankPo bankPo = jpaFixture.newEntityManager().createQuery("select b from BankPo b where b.playerId = ?1", BankPo.class)
                .setParameter(1, 1L).getResultList().get(0);
        assertEquals(1L, bankPo.getPlayerId());
        assertEquals(bank.capacity(), bankPo.getCapacity());
        assertEquals(bank.getUnlocked(), bankPo.getUnlocked());
    }

    @Test
    void saveNonEmptyBank() {
        Bank bank = Bank.open();
        bank.unlock();
        bank.put(itemFactory.createItem("长剑"));
        bank.put(itemFactory.createItem("生药", 12));
        bank.put(itemFactory.createItem("女子黄龙弓服"));
        bankRepository.save(1L, bank);
        BankPo bankPo = jpaFixture.newEntityManager().createQuery("select b from BankPo b where b.playerId = ?1", BankPo.class)
                .setParameter(1, 1L).getResultList().get(0);
        assertEquals(bank.getUnlocked(), bankPo.getUnlocked());
        List<ItemPo> items = jpaFixture.newEntityManager().createQuery("select i from ItemPo i where i.itemKey.playerId = ?1", ItemPo.class)
                .setParameter(1, 1L).getResultList();
        List<String> names = items.stream().map(ItemPo::getName).toList();
        assertTrue(names.contains("长剑"));
        assertTrue(names.contains("女子黄龙弓服"));
        assertEquals(3, items.size());
    }

    @Test
    void find() {
        Bank bank = Bank.open();
        bank.unlock();
        bank.unlock();
        bank.put(itemFactory.createItem("生药", 12));
        bank.put(itemFactory.createItem("女子黄龙弓服"));
        var equipment = (DecorativeEquipment)itemFactory.createItem("女子长发");
        Item item = itemFactory.createItem("红色染剂", 2);
        equipment.dye(item.color());
        bank.put(equipment);
        bankRepository.save(1L, bank);
        var bank1 = bankRepository.find(1L).get();
        assertEquals(20, bank1.getUnlocked());
        assertEquals("生药", bank1.getItem(1).name());
        assertEquals(12, ((StackItem)bank1.getItem(1)).number());
        assertEquals("女子黄龙弓服", bank1.getItem(2).name());
        assertEquals("女子长发", bank1.getItem(3).name());
        assertEquals(equipment.color(), bank1.getItem(3).color());
    }

    @Test
    void saveBankShouldBeIsolated() {
        Bank bank = Bank.open();
        bank.unlock();
        bank.put(itemFactory.createItem("长剑"));
        bankRepository.save(1L, bank);

        bank.put(itemFactory.createItem("长刀"));
        bankRepository.save(2L, bank);
        Bank bank2 = bankRepository.find(2L).get();
        assertEquals(10, bank2.getUnlocked());
        assertEquals("长剑", bank2.getItem(1).name());
        assertEquals("长刀", bank2.getItem(2).name());
        Bank bank1 = bankRepository.find(1L).get();
        assertEquals(10, bank1.getUnlocked());
        assertEquals("长剑", bank1.getItem(1).name());
        assertNull(bank1.getItem(2));
    }
}