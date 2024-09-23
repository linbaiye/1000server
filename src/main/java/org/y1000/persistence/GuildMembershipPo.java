package org.y1000.persistence;

import jakarta.persistence.Id;

public class GuildMembershipPo {

    private String role;

    @Id
    private long playerId;

}
