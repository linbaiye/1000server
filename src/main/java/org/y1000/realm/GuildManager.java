package org.y1000.realm;

import org.y1000.entities.players.Player;
import org.y1000.guild.GuildStone;
import org.y1000.input.ClientCreateGuildKungFuEvent;
import org.y1000.util.Coordinate;

public interface GuildManager extends ActiveEntityManager<GuildStone> {

    GuildStone create(String name, Coordinate coordinate);

    void foundGuild(Player founder, Coordinate coordinate, String name, int inventorySlot);

    void init(Realm realm);

    void createGuildKungFu(Player applicant, ClientCreateGuildKungFuEvent event);

    void shutdown();

    void teachGuildKungFu(Player source, Player target) ;

    void inviteMember(Player source, Player target);

    void playerDropGuildStone(Player player, Coordinate at, int slot);

    void confirmGuildCreation(Player player, int slot, String name);

    void cancelGuildCreation(Player player);

    void applyGuildKungFu(Player player);
}
