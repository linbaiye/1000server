package org.y1000.kungfu;

import org.y1000.entities.players.Player;

public interface PeriodicalKungFu {

    boolean consumeResources(Player player, int delta);

    boolean canKeep(Player player);

}
