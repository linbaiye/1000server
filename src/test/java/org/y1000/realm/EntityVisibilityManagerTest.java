package org.y1000.realm;


import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.y1000.item.ItemFactory;
import org.y1000.repository.ItemRepository;

class EntityVisibilityManagerTest {

    private RealmEntityEventSender manager;


    @BeforeEach
    void setUp() {
        manager = new RealmEntityEventSender(Mockito.mock(ItemRepository.class), Mockito.mock(ItemFactory.class));
    }

}