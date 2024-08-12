package org.y1000.sdb;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CreateMonsterSdbImplTest {

    @Test
    void getSettings() {
        CreateNpcSdb monsterSdb = new CreateMonsterSdbImpl(49);
        List<NpcSpawnSetting> settingList = monsterSdb.getSettings("牛");
        assertFalse(settingList.isEmpty());
    }
}