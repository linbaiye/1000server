package org.y1000.input;

import org.y1000.network.Connection;

public record Login(Connection connection, long playerId) {
}
