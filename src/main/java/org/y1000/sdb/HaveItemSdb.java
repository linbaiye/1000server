package org.y1000.sdb;

import java.util.Optional;

public interface HaveItemSdb {

    Optional<String> getHaveItem(String monsterIdName);

    boolean containsMonster(String monsterIdName);
}
