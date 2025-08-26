package org.y1000.repository;

import jakarta.persistence.EntityManager;
import org.y1000.guild.GuildMembership;
import org.y1000.guild.Guild;
import org.y1000.realm.EntityIdGenerator;
import org.y1000.realm.Realm;

import java.util.List;
import java.util.Optional;

public interface GuildRepository {
    List<Guild> findByRealm(Realm realm, EntityIdGenerator entityIdGenerator);

    int countByName(String name);

    void save(EntityManager entityManager, Guild guild, long creator);

    Optional<GuildMembership> findGuildMembership(EntityManager entityManager, long playerId);

    void delete(int guildId);

    void update(Guild guild);

    void save(Guild guild);

    int countGuildKungFu(String name);

}
