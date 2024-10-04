package org.y1000.sdb;

import java.util.Optional;

public class HaveItemSdbImpl extends AbstractSdbReader implements HaveItemSdb {

    public HaveItemSdbImpl(int realmId) {
        read(AbstractCreateEntitySdb.SETTING_PATH +  "/" + makeFileName(realmId), "utf8");
    }

    @Override
    public Optional<String> getHaveItem(String monsterIdName) {
        return !containsMonster(monsterIdName) ? Optional.empty() : Optional.ofNullable(get(monsterIdName, "HaveItem"));
    }

    @Override
    public boolean containsMonster(String monsterIdName) {
        return contains(monsterIdName);
    }

    public static String makeFileName(int id) {
        return "HaveItem" + id + ".sdb";
    }
}
