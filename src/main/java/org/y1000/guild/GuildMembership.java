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

    public boolean isFounder() {
        return "门主".equals(guildRole);
    }

    public boolean canGiveKungFu() {
        return "门主".equals(guildRole) || "副门主".equals(guildRole);
    }

    public boolean canInvite() {
        return canGiveKungFu();
    }
}
