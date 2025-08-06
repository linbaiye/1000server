package org.y1000.sdb;

import java.util.Set;

public interface MonstersSdb extends NpcSdb {

    String getSoundStart(String name);

    String getSoundNormal(String name);

    boolean isPassive(String name);


    String getHaveMagic(String name);

    int getEscapeLife(String name);


    Set<String> getAllAnimateIds();

    String getEffect(String name);
}
