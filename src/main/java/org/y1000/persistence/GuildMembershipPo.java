package org.y1000.persistence;

import jakarta.persistence.Id;
import org.y1000.guild.Guild;

public class GuildMembershipPo {

    private String duty;

    @Id
    private long playerId;


}
