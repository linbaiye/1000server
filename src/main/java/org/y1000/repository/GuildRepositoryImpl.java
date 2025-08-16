package org.y1000.repository;

import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.y1000.guild.GuildMembership;
import org.y1000.guild.Guild;
import org.y1000.kungfu.KungFuFactory;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.persistence.GuildMembershipPo;
import org.y1000.persistence.GuildPo;
import org.y1000.realm.EntityIdGenerator;
import org.y1000.realm.Realm;

import java.util.*;

@Slf4j
public final class GuildRepositoryImpl implements GuildRepository {

    private final EntityManagerFactory entityManagerFactory;
    private final KungFuFactory kungFuFactory;

    public GuildRepositoryImpl(EntityManagerFactory entityManagerFactory,
                               KungFuFactory kungFuFactory) {
        Validate.notNull(entityManagerFactory);
        this.entityManagerFactory = entityManagerFactory;
        this.kungFuFactory = kungFuFactory;
    }


    private Guild restore(Map<Long, String> founderNames, Realm realm, long id, GuildPo stonePo) {
        AttackKungFu kf = null;
        if (stonePo.getGuildKungFuPo() != null)
            kf = (AttackKungFu) kungFuFactory.create(stonePo.getGuildKungFuPo().getName());
        return stonePo.restore(realm, id, kf, founderNames.get(stonePo.findFounderId()));
    }

    private Map<Long, String> loadFounderNames(EntityManager entityManager, List<Long> founderIds) {
        var query =  entityManager.createNativeQuery("select p.id, p.name from player p where p.id in ?");
        query.setParameter(1, founderIds);
        List<Object[]> queryResultList = query.getResultList();
        Map<Long, String> result = new HashMap<>();
        queryResultList.forEach(o -> result.put((Long)o[0], (String)o[1]));
        return result;
    }



    @Override
    public List<Guild> findByRealm(Realm realm, EntityIdGenerator entityIdGenerator) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            List<GuildPo> resultList = entityManager.createQuery("select gs from GuildPo gs where gs.realmId = ?1", GuildPo.class)
                    .setParameter(1, realm.id())
                    .getResultList();
            var founders = resultList.stream().map(GuildPo::findFounderId).toList();
            Map<Long, String> names = loadFounderNames(entityManager, founders);
            return resultList.stream().map(s -> restore(names, realm, entityIdGenerator.next(),s)).toList();
        }
    }

    @Override
    public int countByName(String name) {
        Validate.notNull(name);
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            Query query = entityManager.createQuery("select count(gs) from GuildPo gs where gs.name = ?1")
                    .setParameter(1, name);
            return ((Long)query.getSingleResult()).intValue();
        }
    }


    @Override
    public void save(EntityManager em, Guild guild,
                     long creatorId) {
        Validate.notNull(em);
        Validate.notNull(guild);
        GuildPo stonePo = GuildPo.convert(guild);
        em.persist(stonePo);
        guild.setGuildId(stonePo.getId());
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
    public void delete(int guildId) {
        try (var em = entityManagerFactory.createEntityManager()) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            em.createNativeQuery("delete from guild_membership where guild_id = ?")
                    .setParameter(1, guildId).executeUpdate();
            em.createNativeQuery("delete from guild where id = ?")
                    .setParameter(1, guildId).executeUpdate();
            transaction.commit();
        }
    }

    @Override
    public void update(Guild guild) {
        Validate.notNull(guild);
        if (guild.getGuildId() == null)
            return;
        try (var em = entityManagerFactory.createEntityManager()) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            GuildPo guildStonePo = em.find(GuildPo.class, guild.getGuildId());
            if (guildStonePo == null) {
                transaction.rollback();
                return;
            }
            guildStonePo.merge(guild);
            transaction.commit();
        }
    }

    @Override
    public void save(Guild guild) {
        Validate.isTrue(!guild.getMembers().isEmpty());
        try (var em = entityManagerFactory.createEntityManager()) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            GuildPo stonePo = GuildPo.convert(guild);
            em.persist(stonePo);
            transaction.commit();
            guild.setGuildId(stonePo.getId());
        }
    }

    @Override
    public int countGuildKungFu(String name) {
        Validate.notNull(name);
        try (var em = entityManagerFactory.createEntityManager()) {
            Query query = em.createQuery("select count(p) from GuildKungFuPo p where p.name = ?1")
                    .setParameter(1, name);
            return ((Long)query.getSingleResult()).intValue();
        }
    }
}
