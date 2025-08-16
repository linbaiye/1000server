package org.y1000.persistence;

import jakarta.persistence.*;
import lombok.*;
import org.y1000.guild.GuildMembership;
import org.y1000.guild.Guild;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.realm.Realm;
import org.y1000.util.Coordinate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Builder
@Table(name = "guild")
@NoArgsConstructor
@AllArgsConstructor
public class GuildPo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private int realmId;

    private int x;

    private int y;

    private int maxHealth;

    private int currentHealth;

    private int icon;

    @Column(updatable = false)
    private LocalDateTime createdTime;

    @OneToOne(mappedBy = "guild", cascade = CascadeType.ALL)
    private GuildKungFuPo guildKungFuPo;

    @OneToMany(mappedBy = "guild", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GuildMembershipPo> members;

    public Coordinate coordinate() {
        return Coordinate.xy(x, y);
    }

    public void merge(Guild guild) {
        currentHealth = guild.currentLife();
        guild.guildKungFu().ifPresentOrElse(kf -> {
            guildKungFuPo = GuildKungFuPo.convert(this, kf);
        }, () -> {
            if (guildKungFuPo != null) {
                guildKungFuPo.setGuild(null);
            }
            guildKungFuPo = null;
        });
        if (members == null) {
            members = guild.getMembers().stream().map(m -> GuildMembershipPo.of(this, m)).toList();
            return;
        }
        var current = guild.getMembers().stream().map(GuildMembership::playerId).collect(Collectors.toSet());
        members.removeIf(m -> !current.contains(m.getPlayerId()));
        guild.getMembers().forEach(m -> {
            if (members.stream().anyMatch(mo -> mo.getPlayerId() == m.playerId()))
                return;
            members.add(GuildMembershipPo.of(this, m));
        });
    }

    public Long findFounderId() {
        if (members == null)
            return null;
        return members.stream().filter(m -> "门主".equals(m.getRole())).findFirst().map(GuildMembershipPo::getPlayerId).orElse(null);
    }


    public Guild restore(Realm realm, long id, AttackKungFu attackKungFu, String founderName) {
        Set<GuildMembership> m = members.stream().map(GuildMembershipPo::restore).collect(Collectors.toSet());
        var st = new Guild(id, coordinate(), currentHealth, maxHealth, name, this.id, icon, realm, m, founderName);
        st.registerGuildKungFu(attackKungFu);
        return st;
    }

    public static GuildPo convert(Guild guild) {
        GuildPo stonePo = GuildPo.builder()
                .name(guild.guildName())
                .realmId(guild.getRealm().id())
                .maxHealth(guild.getMaxLife())
                .currentHealth(guild.currentLife())
                .x(guild.coordinate().x())
                .y(guild.coordinate().y())
                .createdTime(LocalDateTime.now())
                .icon(guild.getIcon())
                .build();
        stonePo.members = new ArrayList<>();
        guild.guildKungFu().ifPresent(kf -> stonePo.guildKungFuPo = GuildKungFuPo.convert(stonePo, kf));
        guild.getMembers().forEach(membership -> stonePo.members.add(GuildMembershipPo.of(stonePo, membership)));
        return stonePo;
    }
}
