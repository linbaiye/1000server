package org.y1000.repository;

import jakarta.persistence.*;
import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.guild.GuildMembership;
import org.y1000.guild.GuildStone;
import org.y1000.persistence.GuildMembershipPo;
import org.y1000.persistence.GuildStonePo;
import org.y1000.realm.EntityIdGenerator;
import org.y1000.realm.Realm;
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


    @Override
    public List<GuildStone> findByRealm(Realm realm, EntityIdGenerator entityIdGenerator) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            return entityManager.createQuery("select gs from GuildStonePo gs where gs.realmId = ?1", GuildStonePo.class)
                    .setParameter(1, realm.id())
                    .getResultStream()
                    .map(stonePo -> stonePo.restore(realm, entityIdGenerator.next()))
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
        guildStone.setGuildId(stonePo.getId());
    }

    @Override
    public Optional<GuildMembership> findGuildMembership(EntityManager entityManager, long playerId) {
        var membership = entityManager.createQuery("select g from GuildMembershipPo g where g.playerId = ?1",GuildMembershipPo.class)
                .setParameter(1, playerId)
                .getResultStream()
                .findFirst()
                .map(GuildMembershipPo::restore)
                .orElse(null);
        return Optional.ofNullable(membership);
    }

    @Override
    public void upsertMembership(EntityManager entityManager, long playerId, GuildMembership guildMembership) {
//        Validate.notNull(entityManager);
//        Validate.notNull(guildMembership);
//        var membershipPo = entityManager.find(GuildMembershipPo.class, playerId);
//        if (membershipPo != null) {
//            membershipPo.setRole(guildMembership.guildRole());
//        } else {
//            var po = new GuildMembershipPo(playerId, guildMembership.guildId(), guildMembership.guildRole(), LocalDateTime.now());
//            entityManager.persist(po);
//        }
    }

    @Override
    public void deleteGuildAndMembership(int guildId) {
//        try (var em = entityManagerFactory.createEntityManager()) {
//            EntityTransaction transaction = em.getTransaction();
//            transaction.begin();
//            em.createQuery("delete from GuildMembershipPo gm where gm.guildId = ?1")
//                    .setParameter(1, guildId).executeUpdate();
//            em.createQuery("delete from GuildStonePo gs where gs.id = ?1")
//                    .setParameter(1, guildId).executeUpdate();
//            transaction.commit();
//        }
    }

    @Override
    public void update(GuildStone guildStone) {
        Validate.notNull(guildStone);
        try (var em = entityManagerFactory.createEntityManager()) {
            em.getTransaction().begin();
            GuildStonePo guildStonePo = em.find(GuildStonePo.class, guildStone.getGuildId());
            if (guildStonePo != null) {
                guildStonePo.setCurrentHealth(guildStone.currentLife());
            }
            em.getTransaction().commit();
        }
    }

    @Override
    public void save(GuildStone guildStone) {
        Validate.isTrue(!guildStone.getMembers().isEmpty());
        try (var em = entityManagerFactory.createEntityManager()) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            GuildStonePo stonePo = GuildStonePo.convert(guildStone);
            em.persist(stonePo);
            transaction.commit();
            guildStone.setGuildId(stonePo.getId());
        }
    }
}
