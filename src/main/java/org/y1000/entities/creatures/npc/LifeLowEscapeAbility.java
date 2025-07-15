package org.y1000.entities.creatures.npc;

import org.y1000.entities.ActiveEntity;
import org.y1000.entities.HurtAbility;
import org.y1000.util.Coordinate;

import java.util.Optional;

public class LifeLowEscapeAbility implements EscapeAbility {

    private final int lifeToEscape;

    public LifeLowEscapeAbility(int lifeToEscape) {
        this.lifeToEscape = lifeToEscape;
    }

    @Override
    public Optional<Coordinate> computeSafeSpot(Npc npc, ActiveEntity enemy) {
        return Optional.ofNullable(EscapeAbility.doCompute(npc, enemy, npc.viewRange()));
    }

    public boolean shouldEscape(ActiveEntity entity) {
        return entity.findAbility(HurtAbility.class)
                .map(h -> h.currentLife() > 0 && h.currentLife() <= lifeToEscape)
                .orElse(false);
    }
}
