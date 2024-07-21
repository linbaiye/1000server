package org.y1000.sdb;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateNpcSdbImplTest {

    @Test
    void getNpcSettings() {
        CreateNpcSdbImpl createNpcSdb = new CreateNpcSdbImpl(49);
        assertFalse(createNpcSdb.getAllSettings().isEmpty());
        assertFalse(createNpcSdb.getSettings("一级老板娘").isEmpty());
    }
}