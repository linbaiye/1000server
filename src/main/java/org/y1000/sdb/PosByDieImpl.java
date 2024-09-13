package org.y1000.sdb;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.Set;

public final class PosByDieImpl extends AbstractSdbReader implements PosByDieSdb {

    public static final PosByDieImpl INSTANCE = new PosByDieImpl();
    private PosByDieImpl() {
        read("Init/PosByDie.sdb", "utf8");
    }

    @Override
    public Integer getDestServer(String id) {
        return getInt(id, "DestServer");
    }

    @Override
    public Integer getDestX(String id) {
        return getInt(id, "DestX");
    }

    @Override
    public Integer getDestY(String id) {
        return getInt(id, "DestY");
    }

    @Override
    public Optional<String> findIdByRealmId(int realmId) {
        Set<String> names = names();
        for (String id : names) {
            Integer serverId = getInt(id, "Server");
            if (serverId != null && serverId == realmId)
                return Optional.of(id);
        }
        return Optional.empty();
    }

    public static void main(String[] args) {
        PosByDieImpl sdb= PosByDieImpl.INSTANCE;
//        Set<String> names = itemSdb.names();
        Set<String> names = sdb.columnNames();
        Set<String> items = sdb.names();
        for (String i: items) {
            System.out.println("----------------------------");
            System.out.println(i);
            for (String name : names) {
                if (!StringUtils.isEmpty(sdb.get(i, name)))
                    System.out.println(name + ": " + sdb.get(i, name));
            }
        }
    }

}
