package org.y1000.sdb;

import java.util.Optional;

public interface MapSdb {

    String getMapName(int id);

    String getMapTitle(int id);

    String getSoundBase(int id);

    String getTilName(int id);

    String getObjName(int id);

    String getRofName(int id);

    Optional<Integer> getRegenInterval(int id);

    int getTargetServerID(int id);

    int getTargetX(int id);

    int getTargetY(int id);

}
