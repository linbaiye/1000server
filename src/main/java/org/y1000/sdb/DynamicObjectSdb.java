package org.y1000.sdb;

import org.y1000.entities.objects.DynamicObjectType;

import java.util.Optional;

public interface DynamicObjectSdb {

    String getShape(String name);

    boolean isRemove(String name);

    Optional<String> getViewName(String name);

    int getRegenInterval(String name);

    int getOpenedInterval(String name);
    int getArmor(String name);

    DynamicObjectType getKind(String name);

    String getSStep0(String name);
    String getEStep0(String name);

    String getSStep1(String name);
    String getEStep1(String name);

    String getSStep2(String name);

    String getEStep2(String name);

    String getEventItem(String name);

    String getGuardPos(String name);

    Optional<String> getSoundEvent(String name);

    Optional<String> getSoundDie(String name);

    Optional<String> getSoundSpecial(String name);

    int getLife(String name);

    default int getOpenedMillis(String name) {
        return 10 * getOpenedInterval(name);
    }

}
