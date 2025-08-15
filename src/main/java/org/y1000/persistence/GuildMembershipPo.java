package org.y1000.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.y1000.guild.GuildMembership;
import org.y1000.guild.GuildStone;

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
    @ManyToOne(fetch = FetchType.EAGER)
    private GuildStonePo guildStone;

    @Column(updatable = false, name = "created_time")
    private LocalDateTime createdTime;


    public static GuildMembershipPo of(GuildStonePo stone, GuildMembership membership) {
        GuildMembershipPo  guildMembershipPo = new GuildMembershipPo();
        guildMembershipPo.setGuildStone(stone);
        guildMembershipPo.role = membership.guildRole();
        guildMembershipPo.playerId = membership.playerId();
        guildMembershipPo.createdTime = LocalDateTime.now();
        return guildMembershipPo;
    }

    public GuildMembership restore() {
        return new GuildMembership(role, guildStone.getName(), playerId);
    }

}
