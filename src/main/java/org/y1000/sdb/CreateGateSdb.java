package org.y1000.sdb;


import java.util.Set;

public interface CreateGateSdb {

    int getMapId(String name);

    int getTX(String name);

    int getTY(String name);

    int getServerId(String name);

    Set<String> getNames(int realmId);

    int getWidth(String name);

    int getX(String name);

    int getY(String name);

    boolean isVisible(String name);

    String getRandomPos(String name);

    int getShape(String name);

    String getViewName(String name);
}
