package org.y1000.sdb;


import org.apache.commons.lang3.StringUtils;
import org.y1000.entities.objects.DynamicObjectType;

import java.util.Optional;
import java.util.Set;

public final class DynamicObjectSdbImpl extends AbstractSdbReader implements DynamicObjectSdb {

    public static final DynamicObjectSdbImpl INSTANCE = new DynamicObjectSdbImpl();
    private DynamicObjectSdbImpl() {
        read("Init/DynamicObject.sdb", "utf8");
    }

    public static void main(String[] args) {
        DynamicObjectSdbImpl sdb = DynamicObjectSdbImpl.INSTANCE;
//        Set<String> names = itemSdb.names();
        Set<String> names = sdb.columnNames();
        Set<String> items = sdb.names();
        for (String i: items) {
            if (!"1".equals(sdb.get(i, "Kind")) &&
            !"2".equals(sdb.get(i, "Kind"))
            )
                continue;
            System.out.println("----------------------------");
            System.out.println(i);
            for (String name : names) {
                if (!StringUtils.isEmpty(sdb.get(i, name)))
                    System.out.println(name + ": " + sdb.get(i, name));
            }
        }
    }

    @Override
    public String getShape(String name) {
        return get(name, "Shape");
    }

    @Override
    public boolean isRemove(String name) {
        return "TRUE".equals(get(name, "boRemove"));
    }

    @Override
    public Optional<String> getViewName(String name) {
        String s = get(name, "ViewName");
        return StringUtils.isEmpty(s) ? Optional.empty() : Optional.of(s);
    }

    @Override
    public int getRegenInterval(String name) {
        return getIntOrZero(name, "RegenInterval");
    }

    @Override
    public int getOpenedInterval(String name) {
        return getInt(name, "OpennedInterval");
    }

    @Override
    public int getArmor(String name) {
        return getIntOrZero(name, "Armor");
    }

    @Override
    public DynamicObjectType getKind(String name) {
        return getEnum(name, "Kind", DynamicObjectType::fromValue);
    }

    @Override
    public String getSStep0(String name) {
        return get(name, "SStep0");
    }

    @Override
    public String getEStep0(String name) {
        return get(name, "EStep0");
    }

    @Override
    public String getSStep1(String name) {
        return get(name, "SStep1");
    }

    @Override
    public String getEStep1(String name) {
        return get(name, "EStep1");
    }

    @Override
    public String getSStep2(String name) {
        return get(name, "SStep2");
    }

    @Override
    public String getEStep2(String name) {
        return get(name, "EStep2");
    }

    @Override
    public String getEventItem(String name) {
        return get(name, "EventItem");
    }

    @Override
    public String getGuardPos(String name) {
        return get(name, "GuardPos");
    }

    @Override
    public Optional<String> getSoundEvent(String name) {
        var str = get(name, "SoundEvent");
        return !StringUtils.isEmpty(str) ? Optional.of(str) : Optional.empty();
    }

    @Override
    public Optional<String> getSoundSpecial(String name) {
        var str = get(name, "SoundSpecial");
        return !StringUtils.isEmpty(str) ? Optional.of(str) : Optional.empty();
    }

    @Override
    public int getLife(String name) {
        return getIntOrZero(name, "Life");
    }

}
