package org.y1000.guild;

public record GuildMembership(int guildId, String guildRole, String guildName) {

    public StringBuilder append(StringBuilder stringBuilder) {
        return stringBuilder.append("门派: ")
                .append(guildName)
                .append(" ")
                .append("门派职位: ")
                .append(guildRole)
                .append("\r\n");
    }
}
