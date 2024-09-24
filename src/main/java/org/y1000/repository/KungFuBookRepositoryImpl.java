package org.y1000.repository;

import jakarta.persistence.EntityManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.y1000.kungfu.*;
import org.y1000.kungfu.attack.*;
import org.y1000.kungfu.breath.BreathKungFu;
import org.y1000.kungfu.protect.ProtectKungFu;
import org.y1000.kungfu.protect.ProtectionParametersImpl;
import org.y1000.message.clientevent.ClientCreateGuildKungFuEvent;
import org.y1000.persistence.AttackKungFuParametersProvider;
import org.y1000.persistence.KungFuPo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class KungFuBookRepositoryImpl implements KungFuBookRepository, KungFuBookFactory, KungFuFactory {
    private final KungFuSdb kungFuSdb = KungFuSdb.INSTANCE;


    private final static List<String> UNNAMED_NAMES = List.of(
            "无名拳法", "无名剑法", "无名刀法", "无名槌法", "无名枪术", "无名弓术"
            , "无名投法", "无名步法", "无名心法", "无名强身"
    );

    public KungFuBookRepositoryImpl() {
    }


    private FootKungFu createFootKungFu(String name, int exp) {
        return FootKungFu.builder()
                .name(name)
                .exp(exp)
                .sound(kungFuSdb.getSoundEvent(name))
                .keepParameters(new DefaultKeepParameters(name, kungFuSdb))
                .fiveSecondsParameters(new DefaultFiveSecondParameters(name, kungFuSdb))
                .eventResourceParameters(new DefaultEventResourceParameters(name, kungFuSdb))
                .build();
    }

    private AttackKungFuParameters createAttackKungFuParameter(String name) {
        return new SdbAttackKungFuParametersImpl(name, kungFuSdb, new DefaultArmorParameters(name, kungFuSdb),
                new DefaultEventResourceParameters(name, kungFuSdb));
    }

    private SwordKungFu createSword(String name, int exp,
                                    AttackKungFuParameters attackKungFuParameters) {
        return SwordKungFu.builder()
                .name(name)
                .exp(exp)
                .parameters(attackKungFuParameters)
                .build();
    }

    private SwordKungFu createSword(String name, int exp) {
        return createSword(name, exp, createAttackKungFuParameter(name));
    }

    private QuanfaKungFu quanfaKungFu(String name, int exp, AttackKungFuParameters attackKungFuParameters) {
        return QuanfaKungFu.builder()
                .name(name)
                .exp(exp)
                .parameters(attackKungFuParameters)
                .build();
    }

    private QuanfaKungFu quanfaKungFu(String name, int exp) {
        return quanfaKungFu(name, exp, createAttackKungFuParameter(name));
    }

    private BladeKungFu bladeKungFu(String name, int exp, AttackKungFuParameters attackKungFuParameters) {
        return BladeKungFu.builder()
                .name(name)
                .exp(exp)
                .parameters(attackKungFuParameters)
                .build();
    }

    private BladeKungFu bladeKungFu(String name, int exp) {
        return bladeKungFu(name, exp, createAttackKungFuParameter(name));
    }

    private SpearKungFu spearKungFu(String name, int exp, AttackKungFuParameters attackKungFuParameters) {
        return SpearKungFu.builder()
                .name(name)
                .exp(exp)
                .parameters(attackKungFuParameters)
                .build();
    }

    private SpearKungFu spearKungFu(String name, int exp) {
        return spearKungFu(name, exp, createAttackKungFuParameter(name));
    }

    private AxeKungFu axeKungFu(String name, int exp, AttackKungFuParameters attackKungFuParameters) {
        return AxeKungFu.builder()
                .name(name)
                .exp(exp)
                .parameters(attackKungFuParameters)
                .build();
    }

    private AxeKungFu axeKungFu(String name, int exp) {
        return axeKungFu(name, exp, createAttackKungFuParameter(name));
    }

    private BowKungFu bowKungFu(String name, int exp) {
        return BowKungFu.builder()
                .name(name)
                .exp(exp)
                .parameters(createAttackKungFuParameter(name))
                .build();
    }


    private ThrowKungFu throwKungFu(String name, int exp) {
        return ThrowKungFu.builder()
                .name(name)
                .exp(exp)
                .parameters(createAttackKungFuParameter(name))
                .build();
    }


    private BreathKungFu breathKungFu(String name, int exp) {
        return BreathKungFu.builder().name(name).exp(exp)
                .parameters(new RevertedEventResourceParameters(name, kungFuSdb))
                .sound(kungFuSdb.getSoundEvent(name))
                .build();
    }

    private ProtectKungFu protectKungFu(String name, int exp) {
        return ProtectKungFu.builder()
                .name(name)
                .exp(exp)
                .parameters(new ProtectionParametersImpl(name, kungFuSdb,
                        new DefaultKeepParameters(name, kungFuSdb),
                        new DefaultArmorParameters(name, kungFuSdb),
                        new DefaultFiveSecondParameters(name, kungFuSdb)))
                .build();
    }

    private KungFu create(String name, int exp) {
        KungFuType kungFuType = kungFuSdb.getMagicType(name);
        return switch (kungFuType) {
            case QUANFA -> quanfaKungFu(name, exp);
            case SWORD -> createSword(name, exp);
            case BLADE -> bladeKungFu(name, exp);
            case SPEAR -> spearKungFu(name, exp);
            case AXE -> axeKungFu(name, exp);
            case BOW -> bowKungFu(name, exp);
            case THROW -> throwKungFu(name, exp);
            case FOOT -> createFootKungFu(name, exp);
            case BREATHING -> breathKungFu(name, exp);
            case PROTECTION -> protectKungFu(name, exp);
            case ASSISTANT ->  AssistantKungFu.builder().name(name).eightDirection("灵动八方".equals(name)).exp(exp).build();
            default -> throw new IllegalStateException("Unexpected value: " + kungFuType);
        };
    }


    @Override
    public KungFu create(String name) {
        Validate.notNull(name);
        return create(name, 0);
    }

    @Override
    public AttackKungFu createGuildKungFu(ClientCreateGuildKungFuEvent request) {
        if (checkGuildKungFuSpecification(request) != null) {
            throw new IllegalArgumentException();
        }
        AttackKungFuParametersProvider.builder()
                .attackSpeed(request.getSpeed())
                .recovery(request.getRecovery())
                .avoid(request.getAvoid())
                .headDamage(request.getHeadDamage())
                .bodyDamage(request.getBodyDamage())
                .armDamage(request.getArmDamage())
                .legDamage(request.getLegDamage())
                .headArmor(request.getHeadArmor())
                .bodyArmor(request.getBodyArmor())
                .legArmor(request.getLegArmor())
                .name(request.getName())
                .type(request.getType())

        return null;
    }


    @Override
    public String checkGuildKungFuSpecification(ClientCreateGuildKungFuEvent request) {
        Validate.notNull(request);
        if (StringUtils.isBlank(request.getName())) {
            return "请输入正确名字";
        }
        if (request.getName().length() >= 8) {
            return "名字最长8字符";
        }
        if (!request.getType().isMelee()) {
            return "武功只能是刀、剑、拳、槌、枪";
        }
        if (request.getSpeed() < 1 || request.getSpeed() > 99) {
            return "速度需在1-99之间";
        }
        if (request.getRecovery() < 1 || request.getRecovery() > 99) {
            return "恢复需在1-99之间";
        }
        if (request.getAvoid() < 1 || request.getAvoid() > 99) {
            return "闪躲需在1-99之间";
        }
        if (request.getHeadDamage() < 10 || request.getHeadDamage() > 70) {
            return "头攻需在10-70之间";
        }
        if (request.getArmDamage() < 10 || request.getArmDamage() > 70) {
            return "手攻需在10-70之间";
        }
        if (request.getBodyDamage() < 10 || request.getBodyDamage() > 70) {
            return "身攻需在10-70之间";
        }
        if (request.getHeadArmor() < 10 || request.getHeadArmor() > 70) {
            return "头防需在10-70之间";
        }
        if (request.getArmArmor() < 10 || request.getArmArmor() > 70) {
            return "手防需在10-70之间";
        }
        if (request.getBodyArmor() < 10 || request.getBodyArmor() > 70) {
            return "身防需在10-70之间";
        }
        if (request.getLegArmor() < 10 || request.getLegArmor() > 70) {
            return "脚防需在10-70之间";
        }
        if (request.getPowerToSwing() < 5 || request.getPowerToSwing() > 35) {
            return "武功消耗需在5-35之间";
        }
        if (request.getInnerPowerToSwing() < 5 || request.getInnerPowerToSwing() > 35) {
            return "内功消耗需在5-35之间";
        }
        if (request.getOuterPowerToSwing() < 5 || request.getOuterPowerToSwing() > 35) {
            return "外功消耗需在5-35之间";
        }
        if (request.getLifeToSwing() < 5 || request.getLifeToSwing() > 35) {
            return "活力消耗需在5-35之间";
        }
        if (request.getSpeed() + request.getBodyDamage() != 100) {
            return "速度和身攻之和需要等于100";
        }
        if (request.getRecovery() + request.getAvoid() != 100) {
            return "恢复和闪躲之和需要等于100";
        }
        if (request.getHeadDamage() + request.getArmDamage() + request.getLegDamage()
            + request.getBodyArmor() + request.getHeadArmor() + request.getArmArmor()
            + request.getLegArmor() != 228) {
            return "头攻+手攻+脚攻+身防+头防+手防+脚防需要等于228";
        }
        if (request.getOuterPowerToSwing() + request.getLifeToSwing() + request.getPowerToSwing() +
                request.getInnerPowerToSwing() != 80) {
            return "外功消耗+内功消耗+武功消耗+活力消耗需要等于80";
        }
        return null;
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
        return protectKungFu(name, 0);
    }

    private void addOrUpdate(int slot, KungFu kungFu, Map<String, KungFuPo> saved, EntityManager entityManager, long playerId) {
        KungFuPo managed = saved.remove(kungFu.name());
        if (managed != null) {
            managed.setExp(kungFu.exp());
            managed.setSlot(slot);
        } else {
            entityManager.persist(KungFuPo.create(slot, playerId, kungFu));
        }
    }

    private List<KungFuPo> getKungFuPoList(EntityManager entityManager, long playerId) {
        var query = entityManager.createQuery("select kf from KungFuPo kf where kf.key.playerId = ?1", KungFuPo.class);
        query.setParameter(1, playerId);
        return  query.getResultList();
    }

    @Override
    public void save(EntityManager entityManager, long playerId, KungFuBook kungFuBook) {
        Validate.notNull(entityManager);
        Validate.notNull(kungFuBook);
        List<KungFuPo> resultList = getKungFuPoList(entityManager, playerId);
        Map<String, KungFuPo> namePoMap =
                resultList.stream().collect(Collectors.toMap(kungFuPo -> kungFuPo.getKey().getName(), kungFuPo -> kungFuPo));
        kungFuBook.foreachUnnamed((slot, kungFu) -> addOrUpdate(slot, kungFu, namePoMap, entityManager, playerId));
        kungFuBook.foreachBasic((slot, kungFu) -> addOrUpdate(slot, kungFu, namePoMap, entityManager, playerId));
    }

    @Override
    public Optional<KungFuBook> find(EntityManager entityManager, long playerId) {
        Validate.notNull(entityManager);
        List<KungFuPo> resultList = getKungFuPoList(entityManager, playerId);
        if (resultList.isEmpty()) {
            return Optional.empty();
        }
        Map<Integer, KungFu> unnamed = resultList.stream()
                .filter(k -> UNNAMED_NAMES.contains(k.getKey().getName()))
                .collect(Collectors.toMap(KungFuPo::getSlot, kf -> create(kf.getKey().getName(), kf.getExp())));
        KungFuBook kungFuBook = new KungFuBook(unnamed);
        resultList.stream()
                .filter(k -> !UNNAMED_NAMES.contains(k.getKey().getName()))
                .forEach(kf -> kungFuBook.addToBasic(kf.getSlot(), create(kf.getKey().getName(), kf.getExp())));
        return Optional.of(kungFuBook);
    }
}
