package org.y1000.entities.managers;

import org.y1000.entities.players.Player;
import org.y1000.entities.players.Interpolation;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

public class InterpolationManager {

    private final Player player;

    private final Deque<Interpolation> interpolations;


    public InterpolationManager(Player player) {
        this.player = player;
        this.interpolations = new ArrayDeque<>();
    }

    public void update(Collection<Player> others, long timeMillis) {
        Interpolation interpolation = player.snapshot();
        for (Player other : others) {
            if (other.joinedAtMilli() )
        }
    }
}
