package org.y1000.sdb;

import org.apache.commons.lang3.StringUtils;

import java.util.Set;

public final class CreateNpcSdbImpl extends AbstractCreateEntitySdb {

    public CreateNpcSdbImpl(int realmId) {
        super(makeFileName(realmId));
    }

    public static String makeFileName(int id) {
        return "CreateNpc" + id + ".sdb";
    }

    @Override
    protected String parseName(String id) {
        return get(id, "NpcName");
    }

    public static void main(String[] args) {
        CreateNpcSdbImpl sdb = new CreateNpcSdbImpl(49);
//        Set<String> names = itemSdb.names();
        Set<String> names = sdb.columnNames();
        Set<String> items = sdb.names();
        for (String i: items) {
//            if (!i.startsWith("狐狸") || !"2".equals(sdb.get(i, "Kind")))
//            if (!"2".equals(sdb.get(i, "Kind")))
//                continue;
            System.out.println("----------------------------");
            System.out.println(i);
            for (String name : names) {
                if (!StringUtils.isEmpty(sdb.get(i, name)))
                    System.out.println(name + ": " + sdb.get(i, name));
            }
        }
    }


}

