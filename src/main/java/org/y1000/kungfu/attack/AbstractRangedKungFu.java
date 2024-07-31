package org.y1000.kungfu.attack;


import org.y1000.entities.AttackableActiveEntity;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.fight.PlayerAttackState;
import org.y1000.item.Ammo;
import org.y1000.item.ItemType;
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

    protected abstract ItemType getAmmoType();

    protected boolean checkResourcesAndSendError(Player player) {
        var ret = checkAttributeResources(player);
        if (ret != null) {
            player.emitEvent(ret);
            return false;
        }
        if (!player.inventory().contains(getAmmoType())) {
            player.emitEvent(PlayerTextEvent.outOfAmmo(player));
            return false;
        }
        return count > 0;
    }

    protected PlayerAttackState useResourcesAndCreateState(PlayerImpl player) {
        useAttributeResources(player);
        count --;
        var ammo = player.inventory()
                .consumeStackItem(player, getAmmoType(), player::emitEvent);
        return PlayerAttackState.ranged(player, ((Ammo)ammo).spriteId());
    }

    @Override
    protected int computeAbove5000SoundOffset(int level) {
        return level > 8999 ? 2 : 1;
    }

    @Override
    public void startAttack(PlayerImpl player, ClientAttackEvent event, AttackableActiveEntity target) {
        count = level() / 2000 + 2;
        doStartAttack(player, event, target);
    }
}
