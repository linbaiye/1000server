package org.y1000.repository;

import jakarta.persistence.EntityManager;
import org.y1000.guild.GuildMembership;
import org.y1000.guild.GuildStone;
import org.y1000.realm.EntityIdGenerator;
import org.y1000.realm.RealmMap;

import java.util.List;
import java.util.Optional;

public interface GuildRepository {
    List<GuildStone> findByRealm(int realmId, EntityIdGenerator entityIdGenerator, RealmMap realmMap);

    int countByName(String name);

    void save(EntityManager entityManager, GuildStone guildStone, long creator);

    Optional<GuildMembership> findGuildMembership(EntityManager entityManager, long playerId);

    void upsertMembership(EntityManager entityManager, long playerId, GuildMembership guildMembership);

    void deleteGuildAndMembership(int guildId);

    void update(GuildStone guildStone);

}
