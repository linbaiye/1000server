package org.y1000.repository;

import org.y1000.kungfu.*;
import org.y1000.kungfu.attack.*;
import org.y1000.kungfu.breath.BreathKungFu;
import org.y1000.kungfu.protect.ProtectKungFu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class KungFuBookRepositoryImpl implements KungFuBookRepository, KungFuBookFactory  {
    private final KungFuSdb kungFuSdb = KungFuSdb.INSTANCE;

    private final static List<String> UNNAMED_NAMES = List.of(
            "无名拳法", "无名剑法", "无名刀法", "无名槌法", "无名枪术", "无名弓术"
            , "无名投法", "无名步法", "无名心法", "无名强身"
    );


    private FootKungFu createFootKungFu(String name) {
        return FootKungFu.builder().name(name).level(100).build();
    }


    private <C extends AbstractAttackKungFu, B extends AbstractAttackKungFu.AbstractAttackKungFuBuilder<C, B>>
        void setAttackKungFuProperties(String name, AbstractAttackKungFu.AbstractAttackKungFuBuilder<C, B> builder) {
        /*
        Recovery,KeepRecovery,Avoid,accuracy,DamageBody,DamageHead,DamageArm,DamageLeg,DamageEnergy,ArmorBody,ArmorHead,ArmorArm,ArmorLeg,
         */
        builder.name(name);
        builder.level(100);
        builder.attackSpeed(kungFuSdb.getAttackSpeed(name));
        builder.bodyDamage(kungFuSdb.getDamageBody(name));
        builder.headDamage(kungFuSdb.getDamageHead(name));
        builder.armDamage(kungFuSdb.getDamageArm(name));
        builder.legDamage(kungFuSdb.getDamageLeg(name));
        builder.bodyArmor(kungFuSdb.getArmorBody(name));
        builder.headArmor(kungFuSdb.getArmorHead(name));
        builder.armArmor(kungFuSdb.getArmorArm(name));
        builder.legArmor(kungFuSdb.getArmorLeg(name));
        builder.parameters(new AttackKungFuParametersImpl(name, kungFuSdb));
    }


    private SwordKungFu createSword(String name) {
        SwordKungFu.SwordKungFuBuilder<?, ?> builder = SwordKungFu.builder();
        setAttackKungFuProperties(name, builder);
        return builder.build();
    }

    private QuanfaKungFu quanfaKungFu(String name) {
        var builder =  QuanfaKungFu.builder();
        setAttackKungFuProperties(name, builder);
        return builder.build();
    }

    private BladeKungFu bladeKungFu(String name) {
        var builder = BladeKungFu.builder();
        setAttackKungFuProperties(name, builder);
        return builder.build();
    }

    private SpearKungFu spearKungFu(String name) {
        var builder = SpearKungFu.builder();
        setAttackKungFuProperties(name, builder);
        return builder.build();
    }

    private AxeKungFu axeKungFu(String name) {
        var builder = AxeKungFu.builder();
        setAttackKungFuProperties(name, builder);
        return builder.build();
    }

    private BowKungFu bowKungFu(String name) {
        var builder = BowKungFu.builder();
        setAttackKungFuProperties(name, builder);
        return builder.build();
    }


    private ThrowKungFu throwKungFu(String name) {
        var builder = ThrowKungFu.builder();
        setAttackKungFuProperties(name, builder);
        return builder.build();
    }


    private BreathKungFu breathKungFu(String name) {
        return BreathKungFu.builder().name(name).level(100).build();
    }

    private ProtectKungFu protectKungFu(String name) {
        return ProtectKungFu.builder()
                .bodyArmor(kungFuSdb.getArmorBody(name))
                .headArmor(kungFuSdb.getArmorHead(name))
                .armArmor(kungFuSdb.getArmorArm(name))
                .legArmor(kungFuSdb.getArmorLeg(name))
                .name(name).level(100).build();
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
            case ASSISTANT ->  AssistantKungFu.builder().name(name).level(100).build();
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
}
