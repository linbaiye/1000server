package org.y1000.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
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

import java.util.*;
import java.util.stream.Collectors;

public final class KungFuBookRepositoryImpl implements KungFuBookRepository, KungFuBookFactory, KungFuFactory {
    private final KungFuSdb kungFuSdb = KungFuSdb.INSTANCE;

    private final EntityManagerFactory entityManagerFactory;


    private final static List<String> UNNAMED_NAMES = List.of(
            "无名拳法", "无名剑法", "无名刀法", "无名槌法", "无名枪术", "无名弓术"
            , "无名投法", "无名步法", "无名心法", "无名强身"
    );

    private List<AttackKungFuParametersProvider> providers;

    public KungFuBookRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        Validate.notNull(entityManagerFactory);
        this.entityManagerFactory = entityManagerFactory;
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
        if (kungFuSdb.contains(name)) {
            return new AttackKungFuParametersImpl(name, kungFuSdb, new DefaultArmorParameters(name, kungFuSdb),
                    new DefaultEventResourceParameters(name, kungFuSdb));
        }
        return getProvider(name).map(AttackKungFuParametersImpl::new)
                .orElseThrow(() -> new IllegalArgumentException(name + "does not exist."));
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


    private Optional<AttackKungFuParametersProvider> getProvider(String name) {
        return getProviders().stream()
                .filter(provider -> provider.getName().equals(name))
                .findFirst();
    }

    private KungFu create(String name, int exp) {
        KungFuType kungFuType;
        if (kungFuSdb.contains(name))
            kungFuType = kungFuSdb.getMagicType(name);
        else
            kungFuType = getProvider(name)
                    .map(provider -> provider.getType().toKungFuType())
                    .orElse(null);
        if (kungFuType == null) {
            throw new IllegalStateException("Unknown kungfu: " + name);
        }
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
    public void saveGuildKungFuParameter(AttackKungFuParametersProvider provider) {
        Validate.notNull(provider);
        try (var em = entityManagerFactory.createEntityManager()) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            em.persist(provider);
            transaction.commit();
        }
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


    private synchronized List<AttackKungFuParametersProvider> getProviders(EntityManager entityManager) {
        if (providers == null) {
            providers = entityManager.createQuery("select p from AttackKungFuParametersProvider p",
                    AttackKungFuParametersProvider.class).getResultList();
        }
        return new ArrayList<>(providers);
    }

    private List<AttackKungFuParametersProvider> getProviders() {
        try (var em = entityManagerFactory.createEntityManager()) {
            return getProviders(em);
        }
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
