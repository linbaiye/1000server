package org.y1000.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.guild.GuildMembership;
import org.y1000.guild.GuildStone;
import org.y1000.persistence.GuildMembershipPo;
import org.y1000.persistence.GuildStonePo;
import org.y1000.realm.EntityIdGenerator;
import org.y1000.realm.RealmMap;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public final class GuildRepositoryImpl implements GuildRepository {

    private final EntityManagerFactory entityManagerFactory;

    public GuildRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        Validate.notNull(entityManagerFactory);
        this.entityManagerFactory = entityManagerFactory;
    }

    private GuildStone restore(GuildStonePo stonePo,
                               EntityIdGenerator entityIdGenerator,
                               RealmMap realmMap) {
        return GuildStone.builder()
                .idName(stonePo.getName())
                .currentHealth(stonePo.getCurrentHealth())
                .dynamicObjectSdb(stonePo)
                .realmMap(realmMap)
                .id(entityIdGenerator.next())
                .coordinate(stonePo.coordinate())
                .persistentId(stonePo.getId())
                .realmId(stonePo.getRealmId())
                .build();
    }

    @Override
    public List<GuildStone> findByRealm(int realmId, EntityIdGenerator entityIdGenerator, RealmMap realmMap) {
        Validate.notNull(entityIdGenerator);
        Validate.notNull(realmMap);
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            return entityManager.createQuery("select gs from GuildStonePo gs where gs.realmId = ?1", GuildStonePo.class)
                    .setParameter(1, realmId)
                    .getResultStream()
                    .map(stonePo -> restore(stonePo, entityIdGenerator, realmMap))
                    .toList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public int countByName(String name) {
        Validate.notNull(name);
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            Query query = entityManager.createQuery("select count(gs) from GuildStonePo gs where gs.name = ?1")
                    .setParameter(1, name);
            return ((Long)query.getSingleResult()).intValue();
        }
    }


    @Override
    public void save(EntityManager em, GuildStone guildStone,
                     long creatorId, GuildMembership membership) {
        Validate.notNull(guildStone);
        Validate.notNull(membership);
        GuildStonePo stonePo = GuildStonePo.convert(guildStone);
        em.persist(stonePo);
        guildStone.setPersistentId(stonePo.getId());
        em.persist(new GuildMembershipPo(creatorId, stonePo.getId(), membership.guildRole(), LocalDateTime.now()));
    }
}
