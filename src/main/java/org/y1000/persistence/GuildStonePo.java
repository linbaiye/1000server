package org.y1000.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.y1000.entities.objects.DynamicObjectType;
import org.y1000.guild.GuildStone;
import org.y1000.sdb.DynamicObjectSdb;
import org.y1000.util.Coordinate;

import java.time.LocalDateTime;
import java.util.Optional;

@Data
@Entity
@Builder
@Table(name = "guild_stone")
@NoArgsConstructor
@AllArgsConstructor
public class GuildStonePo implements DynamicObjectSdb  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private int realmId;

    private int x;

    private int y;

    private int maxHealth;

    private int currentHealth;

    @Column(updatable = false)
    private LocalDateTime createdTime;

    public Coordinate coordinate() {
        return Coordinate.xy(x, y);
    }

    @Override
    public String getShape(String name) {
        return "67";
    }

    @Override
    public boolean isRemove(String name) {
        return true;
    }

    @Override
    public Optional<String> getViewName(String name) {
        return Optional.of(name);
    }

    @Override
    public int getRegenInterval(String name) {
        return 0;
    }

    @Override
    public int getOpenedInterval(String name) {
        return 0;
    }

    @Override
    public int getArmor(String name) {
        return 0;
    }

    @Override
    public DynamicObjectType getKind(String name) {
        return DynamicObjectType.KILLABLE;
    }

    @Override
    public String getSStep0(String name) {
        return "0";
    }

    @Override
    public String getEStep0(String name) {
        return "0";
    }

    @Override
    public String getSStep1(String name) {
        return "0";
    }

    @Override
    public String getEStep1(String name) {
        return "0";
    }

    @Override
    public String getSStep2(String name) {
        return null;
    }

    @Override
    public String getEStep2(String name) {
        return null;
    }

    @Override
    public String getEventItem(String name) {
        return null;
    }

    @Override
    public String getGuardPos(String name) {
        return null;
    }

    @Override
    public Optional<String> getSoundEvent(String name) {
        return Optional.empty();
    }

    @Override
    public Optional<String> getSoundDie(String name) {
        return Optional.empty();
    }

    @Override
    public Optional<String> getSoundSpecial(String name) {
        return Optional.empty();
    }

    @Override
    public int getLife(String name) {
        return maxHealth;
    }

    public static GuildStonePo convert(GuildStone guildStone, int realmId) {
        return GuildStonePo.builder()
                .name(guildStone.idName())
                .realmId(realmId)
                .maxHealth(guildStone.getMaxLife())
                .currentHealth(guildStone.currentLife())
                .x(guildStone.coordinate().x())
                .y(guildStone.coordinate().y())
                .createdTime(LocalDateTime.now())
                .build();

    }
}
