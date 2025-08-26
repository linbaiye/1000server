package org.y1000.guild;

import org.y1000.entities.players.Player;

import java.util.Objects;

public record GuildMembership(String guildRole, String guildName, long playerId) {

    public StringBuilder appendGuildInfo(StringBuilder stringBuilder) {
        return stringBuilder.append("门派: ")
                .append(guildName)
                .append(" ")
                .append("门派职位: ")
                .append(guildRole);
    }

    public boolean isFounder() {
        return "门主".equals(guildRole);
    }

    public boolean canGrantKungFu() {
        return "门主".equals(guildRole) || "副门主".equals(guildRole);
    }

    public boolean canInvite() {
        return canGrantKungFu();
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GuildMembership that)) return false;
        return playerId() == that.playerId();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(playerId());
    }

    public static GuildMembership member(Player player, String guildName) {
        return new GuildMembership("门人", guildName, player.id());
    }

    public static GuildMembership founder(Player player, Guild guild) {
        return new GuildMembership("门主", guild.guildName(), player.id());
    }
}
