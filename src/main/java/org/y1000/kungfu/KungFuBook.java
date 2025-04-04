package org.y1000.kungfu;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.y1000.kungfu.attack.*;
import org.y1000.kungfu.breath.BreathKungFu;
import org.y1000.kungfu.protect.ProtectKungFu;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public final class KungFuBook {

    private final Map<Integer, KungFu> unnamed;
    private final Map<Integer, KungFu> basic;

    private static final int BASIC_PAGE = 2;

    private static final int BASIC_MAX = 30;

    public KungFuBook(Map<Integer, KungFu> unnamed) {
        this.unnamed = unnamed;
        this.basic = new HashMap<>();
    }

    private boolean basicExists(KungFu kungFu) {
        return basic.values().stream().anyMatch(k -> k.name().equals(kungFu.name()));
    }

    private boolean isBasicFull() {
        return basic.size() == BASIC_MAX;
    }

    public int addToBasic(KungFu kungFu) {
        Validate.notNull(kungFu);
        if (isBasicFull() || basicExists(kungFu)) {
            return 0;
        }
        for (int i = 1; i <= BASIC_MAX; i++) {
            if (!basic.containsKey(i)) {
                basic.put(i, kungFu);
                return i;
            }
        }
        return 0;
    }

    public boolean addToBasic(int slot, KungFu kungFu){
        Validate.notNull(kungFu);
        if (isBasicFull() || basicExists(kungFu) ||
                slot <= 0 || slot > BASIC_MAX ||
                basic.containsKey(slot)) {
            return false;
        }
        return basic.put(slot, kungFu) == null;
    }

    public int findBasicSlot(String name) {
        for (int i = 1; i <= BASIC_MAX; i++) {
            if (basic.containsKey(i) && basic.get(i).name().equals(name)) {
                return i;
            }
        }
        return 0;
    }

    public Optional<KungFu> findBasic(String name) {
        if (StringUtils.isEmpty(name)) {
            return Optional.empty();
        }
        int slot = findBasicSlot(name);
        if (slot == 0) {
            return Optional.empty();
        }
        return getKungFu(BASIC_PAGE, slot);
    }

    public void foreachUnnamed(BiConsumer<Integer, KungFu> kungFuBiConsumer) {
        unnamed.forEach(kungFuBiConsumer);
    }

    public void foreachBasic(BiConsumer<Integer, KungFu> kungFuBiConsumer) {
        basic.forEach(kungFuBiConsumer);
    }

    private <T extends KungFu> T findUnnamed(Predicate<T> predicate, Class<T> type) {
        for (KungFu kungFu : unnamed.values()) {
            if (type.isAssignableFrom(kungFu.getClass()) && predicate.test(type.cast(kungFu))) {
                return type.cast(kungFu);
            }
        }
        throw new IllegalStateException("Unnamed kungfu for type " + type + " does not exist.");
    }

    public AttackKungFu findUnnamedAttack(AttackKungFuType type) {
        return findUnnamed(kungFu -> kungFu.getType() == type, AttackKungFu.class);
    }

    public ProtectKungFu getUnnamedProtection() {
        return findUnnamed(kungFu -> true, ProtectKungFu.class);
    }

    public BreathKungFu getUnnamedBreath() {
        return findUnnamed(kungFu -> true, BreathKungFu.class);
    }


    public FootKungFu getUnnamedFoot() {
        return findUnnamed(kungFu -> true, FootKungFu.class);
    }

    public Optional<KungFu> getKungFu(int page, int slot) {
        Validate.isTrue(page > 0, "Page must be greater than 0");
        Validate.isTrue(slot > 0, "Slot must be greater than 0");
        if (page == 1) {
            return Optional.ofNullable(unnamed.get(slot));
        } else if (page == 2) {
            return Optional.ofNullable(basic.get(slot));
        }
        return Optional.empty();
    }

    public boolean swapSlot(int page, int slot1, int slot2) {
        if (page != 2) {
            return false;
        }
        var kungfu1 = basic.get(slot1);
        var kungfu2 = basic.get(slot2);
        if (kungfu1 == null && kungfu2 == null) {
            return false;
        }
        basic.remove(slot1);
        basic.remove(slot2);
        if (kungfu1 != null) {
            basic.put(slot2, kungfu1);
        }
        if (kungfu2 != null) {
            basic.put(slot1, kungfu2);
        }
        return true;
    }
}
