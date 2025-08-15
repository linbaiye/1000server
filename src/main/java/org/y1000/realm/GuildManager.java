package org.y1000.realm;

import org.y1000.entities.players.Player;
import org.y1000.guild.GuildStone;
import org.y1000.input.ApplyGuildKungFuInput;
import org.y1000.util.Coordinate;

public interface GuildManager extends ActiveEntityManager<GuildStone> {


    void init(Realm realm);

    void shutdown();

    void teachGuildKungFu(Player source, Player target) ;

    void inviteMember(Player source, Player target);

    void playerDropGuildStone(Player player, Coordinate at, int slot);

    void confirmGuildCreation(Player player, int slot, String name);

    void cancelGuildCreation(Player player);

    void handleApplyGuildKungFuCommand(Player player);

    void applyGuildKungFu(Player player, ApplyGuildKungFuInput params);

    void grantGuildKungFu(Player player, String toPlayer);
}
