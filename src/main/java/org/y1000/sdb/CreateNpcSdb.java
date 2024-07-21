package org.y1000.sdb;

import java.util.List;

public interface CreateNpcSdb {
    default List<NpcSpawnSetting> getSettings(String name) {
        return getAllSettings().stream().filter(npcSpawnSetting -> npcSpawnSetting.idName().equals(name)).toList();
    }

    List<NpcSpawnSetting> getAllSettings();

}
