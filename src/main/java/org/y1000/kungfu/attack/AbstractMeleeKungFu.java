package org.y1000.kungfu.attack;

import org.slf4j.Logger;
import org.y1000.entities.AttackableActiveEntity;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.clientevent.ClientAttackEvent;

public abstract class AbstractMeleeKungFu extends AbstractAttackKungFu {

    public AbstractMeleeKungFu(String name, int exp, AttackKungFuParameters parameters) {
        super(name, exp, parameters);
    }

    protected abstract Logger logger();

    @Override
    protected boolean useResources(PlayerImpl player) {
        return useAttributeResources(player);
    }

    protected PlayerTextEvent checkResources(PlayerImpl player) {
        return checkAttributeResources(player);
    }

    @Override
    public void startAttack(PlayerImpl player, ClientAttackEvent event, AttackableActiveEntity target) {
        doStartAttack(player, event, target);
    }

    @Override
    public boolean isRanged() {
        return false;
    }
}
