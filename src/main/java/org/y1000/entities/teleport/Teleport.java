package org.y1000.entities.teleport;

import org.y1000.entities.players.Player;
import org.y1000.util.Coordinate;

import java.util.Set;


public interface Teleport {
    Coordinate coordinate();

    Set<Coordinate> teleportCoordinates();

    void teleport(Player player);

}
