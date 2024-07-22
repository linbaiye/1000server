package org.y1000.sdb;

import org.y1000.util.Coordinate;
import org.y1000.util.Rectangle;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCreateEntitySdb extends AbstractSdbReader implements CreateNpcSdb {

    private final List<NpcSpawnSetting> settings;

    public static final String SETTING_PATH = "Setting";

    public AbstractCreateEntitySdb(String name) {
        read(SETTING_PATH + "/" + name , "utf8");
        settings = parse();
    }

    protected abstract String parseName(String id);

    private List<NpcSpawnSetting> parse() {
        /*
        Name,MonsterName,Script,X,Y,Count,Width,Member,Total,
1,稻草人,,110,51,1,3,,,
         */
        List<NpcSpawnSetting> settingList = new ArrayList<>();
        for (String name : names()) {
            String npcName = parseName(name);
            int x = getInt(name, "X");
            int y = getInt(name, "Y");
            int count = getInt(name, "Count");
            int width = getInt(name, "Width");
            settingList.add(new NpcSpawnSetting(new Rectangle(Coordinate.xy(x - width, y - width), Coordinate.xy(x + width, y + width)), count, npcName));
        }
        return settingList;
    }

    @Override
    public List<NpcSpawnSetting> getAllSettings() {
        return settings;
    }
}
