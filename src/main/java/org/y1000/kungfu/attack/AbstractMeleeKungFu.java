package org.y1000.kungfu.attack;

import org.slf4j.Logger;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.objects.DynamicObject;
import org.y1000.entities.players.Player;
import org.y1000.util.Coordinate;

public abstract class AbstractMeleeKungFu extends AbstractAttackKungFu {

    public AbstractMeleeKungFu(String name, int exp, AttackKungFuParameters parameters) {
        super(name, exp, parameters);
    }

    protected abstract Logger logger();


    @Override
    public String checkResourceToAttack(Player player) {
        return checkHasEnoughAttributes(player);
    }

    @Override
    protected int computeAbove5000SoundOffset(int level) {
        return level > 8999 ? 4 : 2;
    }

    @Override
    public boolean isRanged() {
        return false;
    }


    @Override
    public boolean isWithinAttackRange(Coordinate playerCoordinate, ActiveEntity entity) {
        if (entity instanceof DynamicObject dynamicObject) {
            return dynamicObject.occupiedCoordinates().stream().anyMatch(c -> c.directDistance(playerCoordinate) <= 1);
        } else {
            return entity.coordinate().directDistance(playerCoordinate) <= 1;
        }
    }
}
