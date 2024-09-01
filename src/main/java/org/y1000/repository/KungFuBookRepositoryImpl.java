package org.y1000.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.apache.commons.lang3.Validate;
import org.y1000.kungfu.*;
import org.y1000.kungfu.attack.*;
import org.y1000.kungfu.breath.BreathKungFu;
import org.y1000.kungfu.protect.ProtectKungFu;
import org.y1000.kungfu.protect.ProtectionParametersImpl;
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
        return new AttackKungFuParametersImpl(name, kungFuSdb, new DefaultArmorParameters(name, kungFuSdb),
                new DefaultEventResourceParameters(name, kungFuSdb));
    }


    private SwordKungFu createSword(String name, int exp) {
        return SwordKungFu.builder()
                .name(name)
                .exp(exp)
                .parameters(createAttackKungFuParameter(name))
                .build();
    }

    private QuanfaKungFu quanfaKungFu(String name, int exp) {
        return QuanfaKungFu.builder()
                .name(name)
                .exp(exp)
                .parameters(createAttackKungFuParameter(name))
                .build();
    }

    private BladeKungFu bladeKungFu(String name, int exp) {
        return BladeKungFu.builder()
                .name(name)
                .exp(exp)
                .parameters(createAttackKungFuParameter(name))
                .build();
    }

    private SpearKungFu spearKungFu(String name, int exp) {
        return SpearKungFu.builder()
                .name(name)
                .exp(exp)
                .parameters(createAttackKungFuParameter(name))
                .build();
    }

    private AxeKungFu axeKungFu(String name, int exp) {
        return AxeKungFu.builder()
                .name(name)
                .exp(exp)
                .parameters(createAttackKungFuParameter(name))
                .build();
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
            case ASSISTANT ->  AssistantKungFu.builder().name(name).exp(exp).build();
            default -> throw new IllegalStateException("Unexpected value: " + kungFuType);
        };
    }


    @Override
    public KungFu create(String name) {
        Validate.notNull(name);
        return create(name, 0);
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
        Query query = entityManager.createQuery("select kf from KungFuPo kf where kf.key.playerId = ?1");
        query.setParameter(1, playerId);
        return  (List<KungFuPo>)query.getResultList();
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
