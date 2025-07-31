package org.y1000.entities.teleport;

import org.y1000.entities.players.Player;
import org.y1000.util.Coordinate;

import java.util.List;
import java.util.Set;


public interface Teleport {
    Coordinate coordinate();

    Set<Coordinate> coordinates();

    void onPlayerEntered(Player player);

    int toRealmId();

    Coordinate toCoordinate();

    Coordinate rejectCoordinate();

    List<TeleportCost> costs();

    int rejectRealmId();

}
