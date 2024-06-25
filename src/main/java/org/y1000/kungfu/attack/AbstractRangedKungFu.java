package org.y1000.kungfu.attack;


import org.y1000.entities.AttackableEntity;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.clientevent.ClientAttackEvent;

public abstract class AbstractRangedKungFu extends AbstractAttackKungFu {
    private int count;

    public AbstractRangedKungFu(String name, int exp, AttackKungFuParameters parameters) {
        super(name, exp, parameters);
    }

    @Override
    public boolean isRanged() {
        return true;
    }

    protected PlayerTextEvent checkResources(PlayerImpl player) {
        var ret = checkAttributeResources(player);
        if (ret != null) {
            return ret;
        }
        if (!player.inventory().contains("箭")) {
            return PlayerTextEvent.outOfAmmo(player);
        }
        return null;
    }

    @Override
    protected boolean useResources(PlayerImpl player) {
        if (count <= 0) {
            return false;
        }
        count--;
        if (!useAttributeResources(player)) {
            return false;
        }
        boolean ret = player.inventory()
                .consumeStackItem(player, "箭", player::emitEvent);
        if (!ret) {
            player.emitEvent(PlayerTextEvent.outOfAmmo(player));
        }
        return ret;
    }

    @Override
    public void startAttack(PlayerImpl player, ClientAttackEvent event, AttackableEntity target) {
        count = 2;
        doStartAttack(player, event, target);
    }
}
