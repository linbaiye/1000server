package org.y1000.sdb;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RealmSpecificSdbRepositoryImplTest {
    private RealmSpecificSdbRepository repository = RealmSpecificSdbRepositoryImpl.INSTANCE;

    @Test
    void dynamicObject() {
        assertTrue(repository.objectSdbExists(19));
    }
}