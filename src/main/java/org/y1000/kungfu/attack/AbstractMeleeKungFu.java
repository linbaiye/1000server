package org.y1000.kungfu.attack;

import org.slf4j.Logger;
import org.y1000.entities.AttackableActiveEntity;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.fight.PlayerAttackState;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.clientevent.ClientAttackEvent;

public abstract class AbstractMeleeKungFu extends AbstractAttackKungFu {

    public AbstractMeleeKungFu(String name, int exp, AttackKungFuParameters parameters) {
        super(name, exp, parameters);
    }

    protected abstract Logger logger();

    protected PlayerAttackState useResourcesAndCreateState(PlayerImpl player) {
        useAttributeResources(player);
        return PlayerAttackState.melee(player);
    }

    protected boolean checkResourcesAndSendError(Player player) {
        var text = checkAttributeResources(player);
        if (text != null) {
            player.emitEvent(text);
        }
        return text == null;
    }

    @Override
    public void startAttack(PlayerImpl player, ClientAttackEvent event, AttackableActiveEntity target) {
        doStartAttack(player, event, target);
    }

    @Override
    protected int computeAbove5000SoundOffset(int level) {
        return level > 8999 ? 4 : 2;
    }

    @Override
    public boolean isRanged() {
        return false;
    }
}
