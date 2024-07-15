package org.y1000.kungfu;

import lombok.Getter;
import org.y1000.entities.players.Player;

public abstract class AbstractPeriodicalConsumingKungFu extends AbstractKungFu implements PeriodicalKungFu {
    @Getter
    private final KeepParameters keepParameters;

    @Getter
    private final FiveSecondsParameters consumingParameters;

    private int consumingTimer;

    protected AbstractPeriodicalConsumingKungFu(String name, int exp,
                                                KeepParameters keepParameters,
                                                FiveSecondsParameters consumingParameters) {
        super(name, exp);
        this.keepParameters = keepParameters;
        this.consumingParameters = consumingParameters;
        resetTimer();
    }


    public void resetTimer() {
        consumingTimer = 5000;
    }

    public boolean updateResources(Player player, int delta) {
        consumingTimer -= delta;
        if (consumingTimer > 0) {
            return false;
        }
        player.consumePower(consumingParameters.powerPer5Seconds());
        player.consumeOuterPower(consumingParameters.outerPowerPer5Seconds());
        player.consumeInnerPower(consumingParameters.innerPowerPer5Seconds());
        var consumingLife = player.currentLife() > consumingParameters.lifePer5Seconds() ? consumingParameters.lifePer5Seconds()
                : player.currentLife() - 1;
        player.consumeLife(consumingLife);
        resetTimer();
        return true;
    }

    public boolean canKeep(Player player) {
        return keepParameters.lifeToKeep() <= player.currentLife() &&
                keepParameters.innerPowerToKeep() <= player.innerPower() &&
                keepParameters.outerPowerToKeep() <= player.outerPower() &&
                keepParameters.powerToKeep() <= player.power();
    }
}
