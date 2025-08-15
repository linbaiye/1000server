package org.y1000.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.y1000.entities.objects.DynamicObjectType;
import org.y1000.guild.GuildMembership;
import org.y1000.guild.GuildStone;
import org.y1000.realm.EntityIdGenerator;
import org.y1000.realm.Realm;
import org.y1000.sdb.DynamicObjectSdb;
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

    }

    public GuildStone restore(Realm realm, long id) {
        Set<GuildMembership> m = members.stream().map(GuildMembershipPo::restore).collect(Collectors.toSet());
        return new GuildStone(id, coordinate(), currentHealth, maxHealth, name, this.id, icon, realm, m);
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
