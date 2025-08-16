package org.y1000.repository;

import jakarta.persistence.EntityManager;
import org.y1000.guild.GuildMembership;
import org.y1000.guild.Guild;
import org.y1000.realm.EntityIdGenerator;
import org.y1000.realm.Realm;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DevGuildRepository implements GuildRepository {

    private final List<Guild> stones = new ArrayList<>();

    private int id = 1;

    @Override
    public List<Guild> findByRealm(Realm realm, EntityIdGenerator entityIdGenerator) {
        return List.of();
    }

    @Override
    public int countByName(String name) {
        return stones.stream().filter(s -> s.guildName().equals(name)).findFirst().map(s -> 1).orElse(0);
    }

    @Override
    public void save(EntityManager entityManager, Guild guild, long creator) {

    }

    @Override
    public Optional<GuildMembership> findGuildMembership(EntityManager entityManager, long playerId) {
        return Optional.empty();
    }

    @Override
    public void delete(int guildId) {

    }

    @Override
    public void update(Guild guild) {
    }

    @Override
    public void save(Guild guild) {
        stones.add(guild);
        guild.setGuildId(id++);
    }

    @Override
    public int countGuildKungFu(String name) {
        return stones.stream().filter(s -> s.guildKungFu().map(k -> k.name().equals(name)).orElse(false))
                .findFirst().map(e -> 1).orElse(0);
    }
}
