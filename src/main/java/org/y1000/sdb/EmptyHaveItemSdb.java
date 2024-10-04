package org.y1000.sdb;

import java.util.Optional;

public final class EmptyHaveItemSdb implements HaveItemSdb {
    public static final EmptyHaveItemSdb INSTANCE = new EmptyHaveItemSdb();
    private EmptyHaveItemSdb() {}
    @Override
    public Optional<String> getHaveItem(String monsterIdName) {
        return Optional.empty();
    }

    @Override
    public boolean containsMonster(String monsterIdName) {
        return false;
    }
}
