package org.y1000.realm;


import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.y1000.item.ItemFactory;
import org.y1000.repository.ItemRepository;

class RealmEntityManagerTest {

    private RealmEntityManager manager;


    @BeforeEach
    void setUp() {
        manager = new RealmEntityManager(Mockito.mock(ItemRepository.class), Mockito.mock(ItemFactory.class));
    }

}