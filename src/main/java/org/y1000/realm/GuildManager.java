package org.y1000.realm;

import org.y1000.entities.players.Player;
import org.y1000.guild.Guild;
import org.y1000.input.ApplyGuildKungFuInput;
import org.y1000.util.Coordinate;

public interface GuildManager extends ActiveEntityManager<Guild> {

    void init(Realm realm);

    void shutdown();

    void inviteMember(Player source, Player target);

    void playerDropGuildStone(Player player, Coordinate at, int slot);

    void confirmGuildCreation(Player player, int slot, String name);

    void cancelGuildCreation(Player player);

    void handleApplyGuildKungFuCommand(Player player);

    void applyGuildKungFu(Player player, ApplyGuildKungFuInput params);

    void grantGuildKungFu(Player player, String toPlayer);

}
