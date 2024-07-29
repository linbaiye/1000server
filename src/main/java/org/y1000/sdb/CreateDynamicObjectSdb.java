package org.y1000.sdb;


import java.util.Optional;
import java.util.Set;

public interface CreateDynamicObjectSdb {

    int getX(String no);

    int getY(String no);

    String getName(String no);

    Set<String> getNumbers();

    Optional<String> getDropItem(String no);

    Optional<String> getFirstNo(String name);
}

