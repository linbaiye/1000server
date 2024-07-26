package org.y1000.sdb;

import org.apache.commons.lang3.StringUtils;

import java.util.Set;
import java.util.stream.Collectors;

public final class CreateGateSdbImpl extends AbstractSdbReader implements CreateGateSdb {

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

    @Override
    public int getMapId(String name) {
        return getInt(name, "MapId");
    }

    @Override
    public int getTX(String name) {
        return getInt(name, "TX");
    }

    @Override
    public int getTY(String name) {
        return getInt(name, "TY");
    }

    @Override
    public int getServerId(String name) {
        return getInt(name, "ServerId");
    }

    @Override
    public Set<String> getNames(int realmId) {
        return names().stream()
                .filter(n -> getMapId(n) == realmId)
                .collect(Collectors.toSet());
    }

    @Override
    public int getWidth(String name) {
        return getInt(name, "Width");
    }
}
