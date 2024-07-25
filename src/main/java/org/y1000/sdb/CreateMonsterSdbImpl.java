package org.y1000.sdb;


import org.apache.commons.lang3.StringUtils;

import java.util.Set;

public final class CreateMonsterSdbImpl extends AbstractCreateEntitySdb {

    public CreateMonsterSdbImpl(int realmId) {
        super(makeFileName(realmId));
    }

    public static String makeFileName(int realmId) {
        return "CreateMonster" + realmId + ".sdb";
    }

    @Override
    protected String parseName(String id) {
        return get(id, "MonsterName");
    }

    public static void main(String[] args) {
        CreateMonsterSdbImpl sdb = new CreateMonsterSdbImpl(49);
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
                if (name.contains("_")) {
                    continue;
                }
                if (!StringUtils.isEmpty(sdb.get(i, name)))
                    System.out.println(name + ": " + sdb.get(i, name));
            }
        }
    }



}
