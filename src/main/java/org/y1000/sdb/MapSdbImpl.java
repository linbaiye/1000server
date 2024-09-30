package org.y1000.sdb;


import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class MapSdbImpl extends AbstractSdbReader implements MapSdb {

    public static final MapSdbImpl INSTANCE = new MapSdbImpl();

    private MapSdbImpl() {
        read("Map.sdb", "utf8");
    }

    public String getMapName(String id) {
        return get(id, "MapName");
    }

    public String getMapName(int id) {
        return getMapName(String.valueOf(id));
    }

    @Override
    public String getMapTitle(int id) {
        return get(String.valueOf(id), "MapTitle");
    }

    @Override
    public String getSoundBase(int id) {
        return get(String.valueOf(id), "SoundBase");
    }

    @Override
    public String getTilName(int id) {
        return get(String.valueOf(id), "TilName");
    }

    @Override
    public String getObjName(int id) {
        return get(String.valueOf(id), "ObjName");
    }

    @Override
    public String getRofName(int id) {
        return get(String.valueOf(id), "RofName");
    }

    @Override
    public Optional<Integer> getRegenInterval(int id) {
        Integer anInt = getInt(String.valueOf(id), "RegenInterval");
        return anInt != null ? Optional.of(anInt)  : Optional.empty();
    }

    @Override
    public int getTargetServerID(int id) {
        return getInt(String.valueOf(id), "TargetServerID");
    }

    @Override
    public int getTargetX(int id) {
        return getInt(String.valueOf(id), "TargetX");
    }

    @Override
    public int getTargetY(int id) {
        return getInt(String.valueOf(id), "TargetY");
    }

    @Override
    public List<Integer> getAllIds() {
        return names().stream().map(Integer::parseInt).toList();
    }


    public static void main(String[] args) {
        MapSdbImpl sdb = MapSdbImpl .INSTANCE;
//        Set<String> names = itemSdb.names();
        Set<String> names = sdb.columnNames();
        Set<String> items = sdb.names();
        for (String i: items) {
            if (!i.equals("32"))
                continue;

            System.out.println("----------------------------");
            System.out.println(i);
            for (String name : names) {
                if (!StringUtils.isEmpty(sdb.get(i, name)))
                    System.out.println(name + ": " + sdb.get(i, name));
            }
        }
    }
}
