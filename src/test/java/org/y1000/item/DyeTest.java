package org.y1000.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class DyeTest extends AbstractItemUnitTestFixture {

    private DyableEquipment dyableEquipment;


    @BeforeEach
    void setUp() {
        dyableEquipment = Mockito.mock(DyableEquipment.class);
    }

    @Test
    void dye() {
        StackItem stackItem = (StackItem) itemFactory.createItem("脱色药");
        stackItem.origin(Dye.class).ifPresent(d ->  d.dye(dyableEquipment));
        verify(dyableEquipment, times(1)).bleach(anyInt());


        stackItem = (StackItem) itemFactory.createItem("红色染剂");
        stackItem.origin(Dye.class).ifPresent(d ->  d.dye(dyableEquipment));
        verify(dyableEquipment, times(1)).dye(anyInt());
    }
}