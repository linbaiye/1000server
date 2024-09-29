package org.y1000.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "guild_kungfu")
@AllArgsConstructor
@NoArgsConstructor
public class GuildKungFuPo {
    @Id
    private Integer guildId;

    private int attackKungfuId;
}
