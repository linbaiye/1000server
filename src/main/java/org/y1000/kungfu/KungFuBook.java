package org.y1000.kungfu;
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

    public KungFuBook(Map<Integer, KungFu> unnamed) {
        this.unnamed = unnamed;
    }

    public void foreachUnnamed(BiConsumer<Integer, KungFu> kungFuBiConsumer) {
        unnamed.forEach(kungFuBiConsumer);
    }

    private <T extends KungFu> T findUnnamed(Predicate<T> predicate, Class<T> type) {
        for (KungFu kungFu : unnamed.values()) {
            if (type.isAssignableFrom(kungFu.getClass()) && predicate.test(type.cast(kungFu))) {
                return type.cast(kungFu);
            }
        }
        throw new IllegalStateException("Unnamed kungfu for type " + type + " does not exist.");
    }




    public AttackKungFu findUnnamed(AttackKungFuType type) {
        return findUnnamed(kungFu -> kungFu.getType() == type, AttackKungFu.class);
    }


    public ProtectKungFu getUnnamedProtection() {
        return findUnnamed(kungFu -> true, ProtectKungFu.class);
    }

    public FootKungFu getUnnamedFoot() {
        return findUnnamed(kungFu -> true, FootKungFu.class);
    }

    public Optional<KungFu> findKungFu(int page, int slot) {
        Validate.isTrue(page > 1, "Page must be greater than 0");
        Validate.isTrue(slot > 1, "Slot must be greater than 0");
        if (page == 1) {
            Optional.ofNullable(unnamed.get(slot));
        }
        return Optional.empty();
    }

    public static KungFuBook newInstance() {
        Map<Integer, KungFu> map = new HashMap<>();
        var fist = QuanfaKungFu.unnamed();
        map.put(1, fist);
        var sword = SwordKungFu.unnamed();
        map.put(2, sword);
        var blade = BladeKungFu.unnamed();
        map.put(3, blade);
        var axe = AxeKungFu.unnamed();
        map.put(4, axe);
        var spear = SpearKungFu.unnamed();
        map.put(5, spear);
        var bow = BowKungFu.unnamed();
        map.put(6, bow);
        var thr = ThrowKungFu.unnamed();
        map.put(7, thr);
        var bufa = FootKungFu.unnamed();
        map.put(8, bufa);
        map.put(9, BreathKungFu.unnamed());
        map.put(10, ProtectKungFu.unnamed());
        return new KungFuBook(map);
    }
}
