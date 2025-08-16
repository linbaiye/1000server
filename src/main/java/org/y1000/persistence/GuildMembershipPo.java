package org.y1000.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.y1000.guild.GuildMembership;

import java.time.LocalDateTime;

@Entity
@Table(name = "guild_membership")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuildMembershipPo {
    @Id
    private long playerId;

    private String role;

    @JoinColumn(name = "guild_id")
    @ManyToOne
    private GuildPo guild;

    @Column(updatable = false, name = "created_time")
    private LocalDateTime createdTime;


    public static GuildMembershipPo of(GuildPo stone, GuildMembership membership) {
        GuildMembershipPo  guildMembershipPo = new GuildMembershipPo();
        guildMembershipPo.setGuild(stone);
        guildMembershipPo.role = membership.guildRole();
        guildMembershipPo.playerId = membership.playerId();
        guildMembershipPo.createdTime = LocalDateTime.now();
        return guildMembershipPo;
    }

    public GuildMembership restore() {
        return new GuildMembership(role, guild.getName(), playerId);
    }

}
