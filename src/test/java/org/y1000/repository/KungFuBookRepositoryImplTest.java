package org.y1000.repository;

import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.y1000.kungfu.KungFu;
import org.y1000.kungfu.KungFuBook;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.kungfu.attack.AttackKungFuType;
import org.y1000.persistence.AttackKungFuParametersProvider;
import org.y1000.persistence.PlayerKungFuPo;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class KungFuBookRepositoryImplTest {

    private EntityManagerFactory entityManagerFactory;

    private KungFuBookRepositoryImpl kungFuBookRepository;

    private EntityManager entityManager;
    private EntityTransaction transaction;

    private KungFuBook kungFuBook;
    
    private final long playerId = 1L;
    
    private void beginTx() {
        entityManager = entityManagerFactory.createEntityManager();
        transaction = entityManager.getTransaction();
        transaction.begin();
    }

    private void submitTx() {
        transaction.commit();
    }


    @BeforeEach
    void setUp() {
        entityManagerFactory = Persistence.createEntityManagerFactory("org.y1000.test");
        kungFuBookRepository = new KungFuBookRepositoryImpl(entityManagerFactory);
        kungFuBook = kungFuBookRepository.create();
    }

    @AfterEach
    void tearDown() {
        if (entityManager != null)
            entityManager.close();
    }


    private List<PlayerKungFuPo> select() {
        Query query = entityManager.createQuery("select kf from PlayerKungFuPo kf where kf.key.playerId = ?1");
        query.setParameter(1, playerId);
        return query.getResultList();
    }


    private void saveBook() {
        beginTx();
        kungFuBookRepository.save(entityManager, playerId, kungFuBook);
        submitTx();
    }

    @Test
    void saveUnnamed() {
        saveBook();
        var kungFuPoList = select();
        assertFalse(kungFuPoList.isEmpty());
        Map<String, PlayerKungFuPo> map = kungFuPoList.stream().collect(Collectors.toMap(kungFuPo -> kungFuPo.getKey().getName(), Function.identity()));
        kungFuBook.foreachUnnamed((slot, kf) -> {
            PlayerKungFuPo removed = map.remove(kf.name());
            assertEquals(slot, removed.getSlot());
        });
        assertTrue(map.isEmpty());
    }


    @Test
    void updateBasic() {
        AttackKungFu attackKungFu = kungFuBookRepository.createAttackKungFu("雷剑式");
        int slot1 = kungFuBook.addToBasic(attackKungFu);
        AttackKungFu attackKungFu2 = kungFuBookRepository.createAttackKungFu("太极剑结");
        int slot2 = kungFuBook.addToBasic(attackKungFu2);
        saveBook();
        attackKungFu.gainPermittedExp(200);
        kungFuBook.swapSlot(2, slot1, slot2);
        saveBook();
        List<PlayerKungFuPo> kungFuPoList = select();
        PlayerKungFuPo saved = kungFuPoList.stream().filter(kungFuPo -> kungFuPo.getKey().getName().equals(attackKungFu.name()))
                .findFirst().get();
        assertEquals(saved.getExp(), attackKungFu.exp());
        assertEquals(saved.getSlot(), slot2);
    }

    @Test
    void find() {
        AttackKungFu attackKungFu = kungFuBookRepository.createAttackKungFu("雷剑式");
        kungFuBook.addToBasic(10, attackKungFu);
        kungFuBook.foreachUnnamed((slot, kf) -> kf.gainPermittedExp(slot));
        attackKungFu.gainPermittedExp(10);
        saveBook();
        Optional<KungFuBook> kungFuBookOptional = kungFuBookRepository.find(entityManager, playerId + 1);
        assertTrue(kungFuBookOptional.isEmpty());
        var saved = kungFuBookRepository.find(entityManager, playerId).get();
        assertNotNull(saved.getKungFu(1, 1));
        saved.foreachUnnamed((slot, kf) -> {
            assertEquals(kungFuBook.getKungFu(1, slot).get().exp(), saved.getKungFu(1, slot).get().exp());
            assertEquals(kungFuBook.getKungFu(1, slot).get().name(), saved.getKungFu(1, slot).get().name());
        });
        Optional<KungFu> kungFu = saved.getKungFu(2, 10);
        assertFalse(kungFu.isEmpty());
        assertEquals("雷剑式", kungFu.get().name());
        assertEquals(attackKungFu.exp(), kungFu.get().exp());
    }

    @Test
    void saveParameter() {
        AttackKungFuParametersProvider provider = AttackKungFuParametersProvider.
                builder()
                .name("test")
                .type(AttackKungFuType.Fist)
                .attackSpeed(1)
                .avoid(2)
                .build();
        kungFuBookRepository.saveGuildKungFuParameter(provider, 1);
        AttackKungFuParametersProvider saved = entityManagerFactory.createEntityManager().find(AttackKungFuParametersProvider.class, provider.getId());
        assertNotNull(saved);
        assertEquals("test", saved.getName());
        assertEquals(2, saved.getAvoid());
        assertEquals(1, saved.getAttackSpeed());
        assertEquals(AttackKungFuType.Fist, saved.getType());
    }

    @Test
    void counts() {
        AttackKungFuParametersProvider provider = AttackKungFuParametersProvider.
                builder()
                .name("test")
                .type(AttackKungFuType.Fist)
                .build();
        kungFuBookRepository.saveGuildKungFuParameter(provider, 1);
        assertEquals(1, kungFuBookRepository.countGuildKungFu(1));
        assertEquals(1, kungFuBookRepository.countGuildKungFuByName("test"));
    }

    @Test
    void findGuildKungFu() {
        AttackKungFuParametersProvider provider = AttackKungFuParametersProvider.
                builder()
                .name("test")
                .type(AttackKungFuType.Fist)
                .build();
        kungFuBookRepository.saveGuildKungFuParameter(provider, 1);
        assertTrue(kungFuBookRepository.findGuildKungfu(1).isPresent());
    }
}