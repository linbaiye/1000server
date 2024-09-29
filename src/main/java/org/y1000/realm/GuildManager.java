package org.y1000.realm;

import org.y1000.entities.players.Player;
import org.y1000.guild.GuildMembership;
import org.y1000.guild.GuildStone;
import org.y1000.message.clientevent.ClientCreateGuildKungFuEvent;
import org.y1000.util.Coordinate;

public interface GuildManager extends ActiveEntityManager<GuildStone> {
    void foundGuild(Player founder, Coordinate coordinate, String name, int inventorySlot);

    void init();

    void createGuildKungFu(Player applicant, ClientCreateGuildKungFuEvent event);

    void shutdown();

    void teachGuildKungFu(Player source, Player target) ;

    void inviteMember(Player source, Player target);

}
