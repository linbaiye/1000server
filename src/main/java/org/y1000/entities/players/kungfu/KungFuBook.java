package org.y1000.entities.players.kungfu;
import org.y1000.entities.players.kungfu.attack.*;
import org.y1000.entities.players.kungfu.breath.BreathKungFu;
import org.y1000.entities.players.kungfu.protect.ProtectKungFu;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public final class KungFuBook {

    private final Map<Integer, KungFu> unnamed;

    public KungFuBook(Map<Integer, KungFu> kungFuSet) {
        this.unnamed = kungFuSet;
    }

    public void foreachUnnamed(BiConsumer<Integer, KungFu> kungFuBiConsumer) {
        unnamed.forEach(kungFuBiConsumer);
    }

    public AttackKungFu findUnnamed(AttackKungFuType type) {
        for (KungFu kungFu : unnamed.values()) {
            if (kungFu instanceof AttackKungFu attackKungFu && attackKungFu.getType() == type) {
                return attackKungFu;
            }
        }
        throw new IllegalStateException("Unnamed kungfu for type " + type + " does not exist.");
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
