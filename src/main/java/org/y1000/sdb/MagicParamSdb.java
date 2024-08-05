package org.y1000.sdb;

import org.apache.commons.lang3.StringUtils;

import java.util.Set;

public final class MagicParamSdb extends AbstractSdbReader {
    public static final MagicParamSdb INSTANCE = new MagicParamSdb();
    private MagicParamSdb() {
        read("Init/MagicParam.sdb", "utf8");
    }

    public String getNameParam1(String monsterName, String MagicName) {
        String index = findIndex(monsterName, MagicName);
        return index != null ? get(index, "NameParam1") : null;
    }

    private String findIndex(String monsterName, String MagicName) {
        Set<String> names = names();
        for (String name : names) {
            if (get(name, "ObjectName").equals(monsterName) &&
                    get(name, "MagicName").equals(MagicName)) {
                return name;
            }
        }
        return null;
    }

    private Integer getNumberParam(String monsterName, String MagicName, String name) {
        String index = findIndex(monsterName, MagicName);
        return index != null ? getInt(index, name) : null;
    }

    public Integer getNumberParam1(String monsterName, String MagicName) {
        return getNumberParam(monsterName, MagicName, "NumberParam1");
    }

    public Integer getNumberParam2(String monsterName, String MagicName) {
        return getNumberParam(monsterName, MagicName, "NumberParam2");
    }

    public static void main(String[] args) {
        MagicParamSdb monstersSdb= MagicParamSdb.INSTANCE;
//        Set<String> names = itemSdb.names();
        Set<String> names = monstersSdb.columnNames();
        Set<String> items = monstersSdb.names();
        for (String i: items) {

            System.out.println("----------------------------");
            System.out.println(i);
            for (String name : names) {
                if (!StringUtils.isEmpty(monstersSdb.get(i, name)))
                    System.out.println(name + ": " + monstersSdb.get(i, name));
            }
        }
    }
}
