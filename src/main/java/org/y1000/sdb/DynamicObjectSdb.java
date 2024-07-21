package org.y1000.sdb;

import java.util.Optional;

public interface DynamicObjectSdb {

    int getShape(String name);

    boolean isRemove(String name);

    Optional<String> getViewName(String name);

    int getRegenInterval(String name);

    int getOpennedInterval(String name);

    int getKind(String name);

}
