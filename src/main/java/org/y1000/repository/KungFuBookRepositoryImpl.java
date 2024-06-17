package org.y1000.repository;

import org.y1000.kungfu.*;
import org.y1000.kungfu.attack.*;
import org.y1000.kungfu.breath.BreathKungFu;
import org.y1000.kungfu.protect.ProtectKungFu;
import org.y1000.kungfu.protect.ProtectionParametersImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class KungFuBookRepositoryImpl implements KungFuBookRepository, KungFuBookFactory, KungFuFactory {
    private final KungFuSdb kungFuSdb = KungFuSdb.INSTANCE;

    private final static List<String> UNNAMED_NAMES = List.of(
            "无名拳法", "无名剑法", "无名刀法", "无名槌法", "无名枪术", "无名弓术"
            , "无名投法", "无名步法", "无名心法", "无名强身"
    );


    private FootKungFu createFootKungFu(String name) {
        return FootKungFu.builder()
                .name(name)
                .exp(0)
                .sound(kungFuSdb.getSoundEvent(name))
                .keepParameters(new DefaultKeepParameters(name, kungFuSdb))
                .fiveSecondsParameters(new DefaultFiveSecondParameters(name, kungFuSdb))
                .build();
    }


    private SwordKungFu createSword(String name) {
        return SwordKungFu.builder()
                .name(name)
                .exp(0)
                .parameters(new AttackKungFuParametersImpl(name, kungFuSdb, new DefaultArmorParameters(name, kungFuSdb)))
                .build();
    }

    private QuanfaKungFu quanfaKungFu(String name) {
        return QuanfaKungFu.builder()
                .name(name)
                .exp(0)
                .parameters(new AttackKungFuParametersImpl(name, kungFuSdb, new DefaultArmorParameters(name, kungFuSdb)))
                .build();
    }

    private BladeKungFu bladeKungFu(String name) {
        return BladeKungFu.builder()
                .name(name)
                .exp(0)
                .parameters(new AttackKungFuParametersImpl(name, kungFuSdb, new DefaultArmorParameters(name, kungFuSdb)))
                .build();
    }

    private SpearKungFu spearKungFu(String name) {
        return SpearKungFu.builder()
                .name(name)
                .exp(0)
                .parameters(new AttackKungFuParametersImpl(name, kungFuSdb, new DefaultArmorParameters(name, kungFuSdb)))
                .build();
    }

    private AxeKungFu axeKungFu(String name) {
        return AxeKungFu.builder()
                .name(name)
                .exp(0)
                .parameters(new AttackKungFuParametersImpl(name, kungFuSdb, new DefaultArmorParameters(name, kungFuSdb)))
                .build();
    }

    private BowKungFu bowKungFu(String name) {
        return BowKungFu.builder()
                .name(name)
                .exp(0)
                .parameters(new AttackKungFuParametersImpl(name, kungFuSdb, new DefaultArmorParameters(name, kungFuSdb)))
                .build();
    }


    private ThrowKungFu throwKungFu(String name) {
        return ThrowKungFu.builder()
                .name(name)
                .exp(0)
                .parameters(new AttackKungFuParametersImpl(name, kungFuSdb, new DefaultArmorParameters(name, kungFuSdb)))
                .build();
    }


    private BreathKungFu breathKungFu(String name) {
        return BreathKungFu.builder().name(name).exp(100).build();
    }

    private ProtectKungFu protectKungFu(String name) {
        return ProtectKungFu.builder()
                .name(name)
                .parameters(new ProtectionParametersImpl(name, kungFuSdb,
                        new DefaultKeepParameters(name, kungFuSdb),
                        new DefaultArmorParameters(name, kungFuSdb),
                        new DefaultFiveSecondParameters(name, kungFuSdb)))
                .build();
    }


    private KungFu create(String name) {
        KungFuType kungFuType = kungFuSdb.getMagicType(name);
        return switch (kungFuType) {
            case QUANFA -> quanfaKungFu(name);
            case SWORD -> createSword(name);
            case BLADE -> bladeKungFu(name);
            case SPEAR -> spearKungFu(name);
            case AXE -> axeKungFu(name);
            case BOW -> bowKungFu(name);
            case THROW -> throwKungFu(name);
            case FOOT -> createFootKungFu(name);
            case BREATHING -> breathKungFu(name);
            case PROTECTION -> protectKungFu(name);
            case ASSISTANT ->  AssistantKungFu.builder().name(name).exp(0).build();
        };
    }

    @Override
    public KungFuBook create() {
        Map<Integer, KungFu> unnamed = new HashMap<>();
        for (int i = 1; i <= UNNAMED_NAMES.size(); i++ ){
            unnamed.put(i, create(UNNAMED_NAMES.get(i - 1)));
        }
        return new KungFuBook(unnamed);
    }

    @Override
    public AttackKungFu createAttackKungFu(String name) {
        return (AttackKungFu) create(name);
    }

    @Override
    public ProtectKungFu createProtection(String name) {
        return protectKungFu(name);
    }
}
