package org.y1000.entities.repository;

import org.y1000.entities.players.Player;

public final class PlayerRepositoryImpl implements PlayerRepository {


    @Override
    public Player load(long id) {
        return Player.ofRealm();
    }
}
