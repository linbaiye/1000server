package org.y1000.sdb;

import org.apache.commons.lang3.StringUtils;

import java.util.Set;
import java.util.stream.Collectors;

public final class NonMonsterNpcSdbImpl extends AbstractNpcSdb implements NonMonsterNpcSdb {
    public static final NonMonsterNpcSdbImpl Instance = new NonMonsterNpcSdbImpl();
    private NonMonsterNpcSdbImpl() {
        read("Npc.sdb", "utf8");
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
    public String getAttackMagic(String name) {
        return get(name, "AttackMagic");
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
    public boolean isBanker(String name) {
        return "TRUE".equals(get(name, "boSeller"));
    }

    @Override
    public boolean hit(String name) {
        return "TRUE".equals(get(name, "boHit"));
    }

    @Override
    public boolean isQuester(String name) {
        return "TRUE".equals(get(name, "boQuester"));
    }

    @Override
    public int getImage(String name) {
        return getInt(name, "Image");
    }

    public Set<String> getAllAnimateIds() {
        return uniqueIds().stream().map(this::getAnimate).collect(Collectors.toSet());
    }
    public static void main(String[] args) {
        NonMonsterNpcSdbImpl sdb= NonMonsterNpcSdbImpl.Instance;
//        Set<String> names = itemSdb.names();
        Set<String> names = sdb.columnNames();
        Set<String> items = sdb.uniqueIds();
        for (String i: items) {

            System.out.println("----------------------------");
            System.out.println(i);
            for (String name : names) {
                if (!StringUtils.isEmpty(sdb.get(i, name)))
                    System.out.println(name + ": " + sdb.get(i, name));
            }
        }
    }
}