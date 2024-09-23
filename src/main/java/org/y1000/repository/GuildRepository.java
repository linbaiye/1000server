package org.y1000.repository;

import org.y1000.entities.players.Player;
import org.y1000.guild.GuildMembership;
import org.y1000.guild.GuildStone;
import org.y1000.realm.EntityIdGenerator;
import org.y1000.realm.RealmMap;

import java.util.List;

public interface GuildRepository {
    List<GuildStone> findByRealm(int realmId, EntityIdGenerator entityIdGenerator, RealmMap realmMap);

    int countByName(String name);

    void save(int realmId, GuildStone guildStone, Player creator, GuildMembership membership);

}
