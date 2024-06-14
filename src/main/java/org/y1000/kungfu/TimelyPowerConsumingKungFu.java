package org.y1000.kungfu;


import org.y1000.entities.players.Player;

public interface TimelyPowerConsumingKungFu extends KungFu {

    void setConsumingPowerTimer();

    void consumePowerIfTimeUp(Player player, int delta);

}
