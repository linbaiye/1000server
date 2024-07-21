package org.y1000.sdb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.item.ItemSdb;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class MerchantItemSdbRepositoryImplTest {
    private MerchantItemSdbRepositoryImpl repository;

    private ItemSdb itemSdb;

    @BeforeEach
    void setUp() {
        itemSdb = Mockito.mock(ItemSdb.class);
        repository = new MerchantItemSdbRepositoryImpl(itemSdb);
    }

    @Test
    void lbn() {
        when(itemSdb.getBuyPrice(anyString())).thenReturn(1);
        when(itemSdb.getPrice(anyString())).thenReturn(2);
        MerchantItemSdb sdb = repository.load("一级老板娘.txt");
        assertFalse(sdb.buy().isEmpty());
        assertFalse(sdb.sell().isEmpty());
    }

    @Test
    void fengxiong() {
        when(itemSdb.getBuyPrice(anyString())).thenReturn(1);
        when(itemSdb.getPrice(anyString())).thenReturn(2);
        MerchantItemSdb sdb = repository.load("一级风兄.txt");
        assertTrue(sdb.buy().isEmpty());
        assertFalse(sdb.sell().isEmpty());
    }
}