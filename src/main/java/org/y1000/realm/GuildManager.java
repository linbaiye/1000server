package org.y1000.realm;

import org.y1000.entities.players.Player;
import org.y1000.guild.GuildStone;
import org.y1000.util.Coordinate;

public interface GuildManager extends ActiveEntityManager<GuildStone> {
    void create(Player founder, Coordinate coordinate, String name);

    void init();
}
