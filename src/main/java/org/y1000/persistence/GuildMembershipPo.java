package org.y1000.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "guild_membership")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuildMembershipPo {
    @Id
    private long playerId;

    private Integer guildId;

    private String role;

    @Column(updatable = false, name = "created_time")
    private LocalDateTime createdTime;


}
