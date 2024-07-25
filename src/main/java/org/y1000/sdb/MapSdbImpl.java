package org.y1000.sdb;


import org.apache.commons.lang3.StringUtils;

import java.util.Set;

public final class MapSdbImpl extends AbstractSdbReader implements MapSdb {

    public static final MapSdbImpl INSTANCE = new MapSdbImpl();

    private MapSdbImpl() {
        read("Map.sdb");
    }

    public String getMapName(String id) {
        return get(id, "MapName");
    }

    public String getMapName(int id) {
        return getMapName(String.valueOf(id));
    }


    public static void main(String[] args) {
        MapSdbImpl sdb = MapSdbImpl .INSTANCE;
//        Set<String> names = itemSdb.names();
        Set<String> names = sdb.columnNames();
        Set<String> items = sdb.names();
        for (String i: items) {
            if (!i.equals("19") && !i.equals("49"))
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
