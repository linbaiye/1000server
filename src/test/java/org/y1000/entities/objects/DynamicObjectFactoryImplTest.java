package org.y1000.entities.objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.y1000.realm.RealmMap;
import org.y1000.sdb.DynamicObjectSdbImpl;
import org.y1000.util.Coordinate;

import static org.junit.jupiter.api.Assertions.*;

class DynamicObjectFactoryImplTest {

    private final DynamicObjectFactoryImpl dynamicObjectFactory = new DynamicObjectFactoryImpl(DynamicObjectSdbImpl.INSTANCE);

    private RealmMap realmMap;

    @BeforeEach
    void setUp() {
        realmMap = Mockito.mock(RealmMap.class);
    }

    @Test
    void createDynamicObject() {
        var obj = dynamicObjectFactory.createDynamicObject("狐狸洞门A", 1L, realmMap, Coordinate.xy(1, 1));
        assertEquals(1L, obj.id());
    }
}