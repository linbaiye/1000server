package org.y1000.sdb;

import org.apache.commons.lang3.StringUtils;

import java.util.Set;

public final class NpcSdbImpl extends AbstractSdbReader implements NpcSdb {
    public static final NpcSdbImpl Instance = new NpcSdbImpl();
    private NpcSdbImpl() {
        read("Npc.sdb");
    }

    @Override
    public int getRecovery(String name) {
        return getInt(name, "Recovery");
    }

    @Override
    public String getAnimate(String name) {
        return get(name, "animate");
    }

    @Override
    public int getAvoid(String name) {
        return getIntOrZero(name, "Avoid");
    }

    @Override
    public int getAttackSpeed(String name) {
        return getInt(name, "AttackSpeed");
    }

    @Override
    public String getSoundAttack(String name) {
        return get(name, "SoundAttack");
    }

    @Override
    public String getSoundStructed(String name) {
        return get(name, "SoundStructed");
    }

    @Override
    public String getSoundStart(String name) {
        return get(name, "SoundStart");
    }

    @Override
    public String getSoundNormal(String name) {
        return get(name, "SoundNormal");
    }

    @Override
    public String getSoundDie(String name) {
        return get(name, "SoundDie");
    }


    @Override
    public int getLife(String name) {
        return getInt(name, "Life");
    }

    @Override
    public int getAccuracy(String name) {
        return getIntOrZero(name, "Accuracy");
    }

    @Override
    public int getDamage(String name) {
        return getIntOrZero(name, "Damage");
    }

    @Override
    public int getArmor(String name) {
        return getIntOrZero(name, "Armor");
    }

    @Override
    public int getActionWidth(String name) {
        return getInt(name, "ActionWidth");
    }

    @Override
    public String getAttackMagic(String name) {
        return get(name, "AttackMagic");
    }


    @Override
    public int getWalkSpeed(String name) {
        return getInt(name, "WalkSpeed");
    }


    @Override
    public String getHaveItem(String name) {
        return getOrNull(name, "HaveItem");
    }

    @Override
    public boolean isProtector(String name) {
        var str = get(name, "boProtecter");
        return "TRUE".equals(str);
    }

    @Override
    public String getNpcText(String name) {
        return get(name, "NpcText");
    }

    @Override
    public boolean isSeller(String name) {
        return "TRUE".equals(get(name, "boSeller"));
    }

    @Override
    public String getViewName(String name) {
        return get(name, "ViewName");
    }

    @Override
    public String getShape(String name) {
        return get(name, "shape");
    }

    @Override
    public int getViewWidth(String name) {
        return getInt(name, "ViewWidth");
    }

    public static void main(String[] args) {
        NpcSdbImpl sdb= NpcSdbImpl.Instance;
//        Set<String> names = itemSdb.names();
        Set<String> names = sdb.columnNames();
        Set<String> items = sdb.names();
        for (String i: items) {
            if (!sdb.getViewName(i).contains("九尾狐")) {
                continue;
            }
            System.out.println("----------------------------");
            System.out.println(i);
            for (String name : names) {
                if (!StringUtils.isEmpty(sdb.get(i, name)))
                    System.out.println(name + ": " + sdb.get(i, name));
            }
        }
    }
}