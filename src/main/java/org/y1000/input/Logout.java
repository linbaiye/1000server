package org.y1000.input;

import org.apache.commons.lang3.Validate;
import org.y1000.network.Connection;

public record Logout(Connection connection, Long playerId) {

    public Logout{
        if (playerId == null)
            Validate.isTrue(connection != null);
    }

    public static Logout byConnection(Connection connection) {
        return new Logout(connection, null);
    }

    public static Logout byPlayerId(long playerId) {
        return new Logout(null, playerId);
    }
}
