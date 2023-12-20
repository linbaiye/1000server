package org.y1000.entities.repository;

import org.y1000.entities.players.Player;
import org.y1000.util.Coordinate;

import java.util.Map;

public final class PlayerRepositoryImpl implements PlayerRepository {

    private final Map<Long,Coordinate> usableCoordinates = Map.of(0L, new Coordinate(39, 26), 1L, new Coordinate(35, 29));

    @Override
    public Player load(long id) {
        return Player.create(id, usableCoordinates.getOrDefault(id, new Coordinate(39,27)));
    }
}
