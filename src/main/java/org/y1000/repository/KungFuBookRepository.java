package org.y1000.repository;

import jakarta.persistence.EntityManager;
import org.y1000.kungfu.KungFuBook;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.persistence.AttackKungFuParametersProvider;

import java.util.Optional;

public interface KungFuBookRepository {
    void save(EntityManager entityManager, long playerId, KungFuBook kungFuBook);

    Optional<KungFuBook> find(EntityManager entityManager, long playerId);

    void saveGuildKungFuParameter(AttackKungFuParametersProvider parametersProvider, int guildId);

    int countGuildKungFuByName(String name);

    int countGuildKungFu(int guildId);

    Optional<AttackKungFu> findGuildKungfu(int guildId);
}
