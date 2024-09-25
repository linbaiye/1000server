package org.y1000.repository;

import jakarta.persistence.*;
import org.apache.commons.lang3.Validate;
import org.y1000.guild.GuildMembership;
import org.y1000.guild.GuildStone;
import org.y1000.persistence.GuildMembershipPo;
import org.y1000.persistence.GuildStonePo;
import org.y1000.realm.EntityIdGenerator;
import org.y1000.realm.RealmMap;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
                     long creatorId) {
        Validate.notNull(em);
        Validate.notNull(guildStone);
        GuildStonePo stonePo = GuildStonePo.convert(guildStone);
        em.persist(stonePo);
        guildStone.setPersistentId(stonePo.getId());
    }

    @Override
    public Optional<GuildMembership> findGuildMembership(EntityManager entityManager, long playerId) {
        Validate.notNull(entityManager);
        GuildMembershipPo membershipPo = entityManager.find(GuildMembershipPo.class, playerId);
        if (membershipPo == null) {
            return Optional.empty();
        }
        var stonePo = entityManager.find(GuildStonePo.class, membershipPo.getGuildId());
        return stonePo == null ? Optional.empty() : Optional.of(new GuildMembership(stonePo.getId(), membershipPo.getRole(), stonePo.getName()));
    }

    @Override
    public void upsertMembership(EntityManager entityManager, long playerId, GuildMembership guildMembership) {
        Validate.notNull(entityManager);
        Validate.notNull(guildMembership);
        var membershipPo = entityManager.find(GuildMembershipPo.class, playerId);
        if (membershipPo != null) {
            membershipPo.setRole(guildMembership.guildRole());
        } else {
            var po = new GuildMembershipPo(playerId, guildMembership.guildId(), guildMembership.guildRole(), LocalDateTime.now());
            entityManager.persist(po);
        }
    }

    @Override
    public void deleteGuildAndMembership(int guildId) {
        try (var em = entityManagerFactory.createEntityManager()) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            em.createQuery("delete from GuildMembershipPo gm where gm.guildId = ?1")
                    .setParameter(1, guildId).executeUpdate();
            em.createQuery("delete from GuildStonePo gs where gs.id = ?1")
                    .setParameter(1, guildId).executeUpdate();
            transaction.commit();
        }
    }

    @Override
    public void update(GuildStone guildStone) {
        Validate.notNull(guildStone);
        try (var em = entityManagerFactory.createEntityManager()) {
            em.getTransaction().begin();
            GuildStonePo guildStonePo = em.find(GuildStonePo.class, guildStone.getPersistentId());
            if (guildStonePo != null) {
                guildStonePo.setCurrentHealth(guildStone.currentLife());
            }
            em.getTransaction().commit();
        }
    }
}
