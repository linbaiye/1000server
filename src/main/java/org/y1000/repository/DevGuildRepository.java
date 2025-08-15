package org.y1000.repository;

import jakarta.persistence.EntityManager;
import org.y1000.guild.GuildMembership;
import org.y1000.guild.GuildStone;
import org.y1000.realm.EntityIdGenerator;
import org.y1000.realm.Realm;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DevGuildRepository implements GuildRepository {

    private final List<GuildStone> stones = new ArrayList<>();

    private int id = 1;

    @Override
    public List<GuildStone> findByRealm(Realm realm, EntityIdGenerator entityIdGenerator) {
        return List.of();
    }

    @Override
    public int countByName(String name) {
        return stones.stream().filter(s -> s.guildName().equals(name)).findFirst().map(s -> 1).orElse(0);
    }

    @Override
    public void save(EntityManager entityManager, GuildStone guildStone, long creator) {

    }

    @Override
    public Optional<GuildMembership> findGuildMembership(EntityManager entityManager, long playerId) {
        return Optional.empty();
    }

    @Override
    public void upsertMembership(EntityManager entityManager, long playerId, GuildMembership guildMembership) {

    }

    @Override
    public void deleteGuildAndMembership(int guildId) {

    }

    @Override
    public void update(GuildStone guildStone) {

    }

    @Override
    public void save(GuildStone guildStone) {
        stones.add(guildStone);
        guildStone.setGuildId(id++);
    }
}
