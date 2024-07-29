package org.y1000.sdb;

import org.junit.jupiter.api.Test;
import org.y1000.util.Coordinate;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CreateDynamicObjectSdbImplTest {


    @Test
    void parse() {
        CreateDynamicObjectSdb sdb = new CreateDynamicObjectSdbImpl(19);
        Set<String> numbers = sdb.getNumbers();
        assertEquals(9, numbers.size());
        assertEquals(Coordinate.xy(135, 87), Coordinate.xy(sdb.getX("8"), sdb.getY("8")));
        for (String number : numbers) {
            assertNotNull(sdb.getName(number));
            assertNotEquals(0, sdb.getX(number));
            assertNotEquals(0, sdb.getY(number));
        }
    }

    @Test
    void getFirstNo() {
        CreateDynamicObjectSdb sdb = new CreateDynamicObjectSdbImpl(49);
        assertTrue(sdb.getFirstNo("坛子1").isPresent());
    }

    @Test
    void getDropItem() {
        CreateDynamicObjectSdb sdb = new CreateDynamicObjectSdbImpl(49);
        assertTrue(sdb.getFirstNo("坛子1").flatMap(sdb::getDropItem).isPresent());
    }
}