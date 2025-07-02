package org.y1000.kungfu.attack;


import org.y1000.entities.AttackableEntity;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.item.ItemType;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.input.ClientAttackEvent;
import org.y1000.util.Coordinate;

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

    @Override
    public String checkResourceToAttack(Player player) {
        var ret = checkHasEnoughAttributes(player);
        if (ret != null) {
            return ret;
        }
        if (!player.inventory().contains(getAmmoType())) {
            return "弹药不足。";
        }
        return null;
    }

    //
//    protected PlayerAttackState useResourcesAndCreateState(PlayerImpl player) {
//        useAttributeResources(player);
//        count --;
//        var ammo = player.inventory()
//                .consumeStackItem(player, getAmmoType(), player::emitEvent);
//        return PlayerAttackState.ranged(player, ((Ammo)ammo).spriteId());
//    }

    @Override
    protected int computeAbove5000SoundOffset(int level) {
        return level > 8999 ? 2 : 1;
    }

    @Override
    public void startAttack(PlayerImpl player, ClientAttackEvent event, AttackableEntity target) {
        count = level() / 2000 + 2;
        doStartAttack(player, event, target);
    }

    @Override
    public boolean isWithinAttackRange(Coordinate coordinate1, Coordinate coordinate2) {
        return coordinate1 != null && coordinate1.isWithinVisibleRange(coordinate2);
    }
}
