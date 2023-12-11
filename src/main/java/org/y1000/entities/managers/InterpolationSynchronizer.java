package org.y1000.entities.managers;

import org.y1000.entities.players.Player;
import org.y1000.entities.players.Interpolation;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

public class InterpolationSynchronizer {

    private final Player source;

    private final Deque<Interpolation> interpolations;


    public InterpolationSynchronizer(Player source) {
        this.source = source;
        this.interpolations = new ArrayDeque<>();
    }

    public void update(Collection<Player> others, long timeMillis) {
        Interpolation interpolation = source.snapshot();
        for (Player other : others) {
            if (other.joinedAtMilli() )
        }
    }
}
