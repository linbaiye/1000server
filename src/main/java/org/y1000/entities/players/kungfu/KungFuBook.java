package org.y1000.entities.players.kungfu;
import org.y1000.entities.players.kungfu.attack.*;
import java.util.HashMap;
import java.util.Map;

public final class KungFuBook {

    private final Map<String, KungFu> basic;

    public KungFuBook(Map<String, KungFu> kungFuSet) {
        this.basic = kungFuSet;
    }

    public AttackKungFu findBasic(AttackKungFuType type) {
        for (KungFu kungFu : basic.values()) {
            if (kungFu instanceof AttackKungFu attackKungFu && attackKungFu.getType() == type) {
                return attackKungFu;
            }
        }
        throw new IllegalStateException("Basic kungfu for type " + type + " does not exist.");
    }

    public static KungFuBook newInstance() {
        Map<String, KungFu> map = new HashMap<>();
        var sword = SwordKungFu.unnamed();
        map.put(sword.name(), sword);
        var blade = BladeKungFu.unnamed();
        map.put(blade.name(), blade);
        var bow = BowKungFu.unnamed();
        map.put(bow.name(), bow);
        var bufa = FootKungFu.unnamed();
        map.put(bufa.name(), bufa);
        var axe = AxeKungFu.unnamed();
        map.put(axe.name(), axe);
        var spear = SpearKungFu.unnamed();
        map.put(spear.name(), spear);
        var fist = QuanfaKungFu.unnamed();
        map.put(fist.name(), fist);
        return new KungFuBook(map);
    }
}
