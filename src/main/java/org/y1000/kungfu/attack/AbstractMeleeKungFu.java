package org.y1000.kungfu.attack;

import org.slf4j.Logger;
import org.y1000.entities.AttackableEntity;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.message.input.ClientAttackEvent;
import org.y1000.util.Coordinate;

public abstract class AbstractMeleeKungFu extends AbstractAttackKungFu {

    public AbstractMeleeKungFu(String name, int exp, AttackKungFuParameters parameters) {
        super(name, exp, parameters);
    }

    protected abstract Logger logger();


    @Override
    public void startAttack(PlayerImpl player, ClientAttackEvent event, AttackableEntity target) {
        doStartAttack(player, event, target);
    }

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
    public boolean isWithinAttackRange(Coordinate coordinate1, Coordinate coordinate2) {
        return coordinate1 != null && coordinate2 != null &&
                coordinate1.directDistance(coordinate2) < 2;
    }
}
