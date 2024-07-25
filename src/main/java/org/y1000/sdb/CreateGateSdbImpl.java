package org.y1000.sdb;

import org.apache.commons.lang3.StringUtils;

import java.util.Set;

public final class CreateGateSdbImpl extends AbstractSdbReader {

    private CreateGateSdbImpl() {
        read("Setting/CreateGate.sdb", "utf8");
    }

    public static final CreateGateSdbImpl INSTANCE = new CreateGateSdbImpl();

    public static void main(String[] args) {
        CreateGateSdbImpl monstersSdb= CreateGateSdbImpl.INSTANCE;
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
