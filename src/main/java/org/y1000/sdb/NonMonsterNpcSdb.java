package org.y1000.sdb;

import java.util.Set;

public interface NonMonsterNpcSdb extends NpcSdb {

    String getSoundStart(String name);

    String getSoundNormal(String name);

    boolean isProtector(String name);

    String getNpcText(String name);

    boolean isSeller(String name);

    boolean isBanker(String name);

    boolean isQuester(String name);

    int getImage(String name);

    Set<String> getAllAnimateIds();

}

