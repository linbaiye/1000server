package org.y1000.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.y1000.guild.GuildMembership;
import org.y1000.guild.GuildStone;
import org.y1000.kungfu.attack.AttackKungFu;
import org.y1000.realm.Realm;
import org.y1000.util.Coordinate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Entity
@Builder
@Table(name = "guild_stone")
@NoArgsConstructor
@AllArgsConstructor
public class GuildStonePo {
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

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "guildStone", fetch = FetchType.EAGER)
    private GuildKungFuPo guildKungFuPo;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "guildStone", fetch = FetchType.EAGER)
    private List<GuildMembershipPo> members;

    public Coordinate coordinate() {
        return Coordinate.xy(x, y);
    }

    public void merge(GuildStone guildStone) {
        currentHealth = guildStone.currentLife();
        guildStone.guildKungFu().ifPresent(k -> {
            if (guildKungFuPo == null)
                guildKungFuPo = GuildKungFuPo.convert(this, k);
        });
        if (members == null) {
            members = guildStone.getMembers().stream().map(m -> GuildMembershipPo.of(this, m)).toList();
            return;
        }
        var current = guildStone.getMembers().stream().map(GuildMembership::playerId).collect(Collectors.toSet());
        members.removeIf(m -> !current.contains(m.getPlayerId()));
        guildStone.getMembers().forEach(m -> {
            if (members.stream().anyMatch(mo -> mo.getPlayerId() == m.playerId()))
                return;
            members.add(GuildMembershipPo.of(this, m));
        });
    }


    public GuildStone restore(Realm realm, long id, AttackKungFu attackKungFu) {
        Set<GuildMembership> m = members.stream().map(GuildMembershipPo::restore).collect(Collectors.toSet());
        var st = new GuildStone(id, coordinate(), currentHealth, maxHealth, name, this.id, icon, realm, m);
        st.registerGuildKungFu(attackKungFu);
        return st;
    }

    public static GuildStonePo convert(GuildStone guildStone) {
        GuildStonePo stonePo = GuildStonePo.builder()
                .name(guildStone.guildName())
                .realmId(guildStone.getRealm().id())
                .maxHealth(guildStone.getMaxLife())
                .currentHealth(guildStone.currentLife())
                .x(guildStone.coordinate().x())
                .y(guildStone.coordinate().y())
                .createdTime(LocalDateTime.now())
                .icon(guildStone.getIcon())
                .build();
        stonePo.members = new ArrayList<>();
        guildStone.getMembers().forEach(membership -> stonePo.members.add(GuildMembershipPo.of(stonePo, membership)));
        return stonePo;
    }
}
