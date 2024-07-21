package org.y1000.sdb;


import org.apache.commons.lang3.StringUtils;

import java.util.Set;

public final class DynamicObjectSdbImpl extends AbstractSdbReader {

    public static final DynamicObjectSdbImpl INSTANCE = new DynamicObjectSdbImpl();
    private DynamicObjectSdbImpl() {
        read("Init/DynamicObject.sdb", "utf8");
    }

    public static void main(String[] args) {
        DynamicObjectSdbImpl sdb = DynamicObjectSdbImpl.INSTANCE;
//        Set<String> names = itemSdb.names();
        Set<String> names = sdb.columnNames();
        Set<String> items = sdb.names();
        for (String i: items) {
            if (!i.startsWith("狐狸"))
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
