package org.y1000.kungfu.attack;


import org.apache.commons.lang3.Validate;
import org.y1000.entities.Direction;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.item.Ammo;
import org.y1000.item.ItemType;
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

    public abstract ItemType getAmmoType();

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

    public Ammo consumeResources(PlayerImpl player) {
        Validate.isTrue(checkResourceToAttack(player) == null);
        consumeAttributes(player);
        count --;
        return (Ammo) player.inventory()
                .consumeStackItem(player, getAmmoType());
    }


    @Override
    protected int computeAbove5000SoundOffset(int level) {
        return level > 8999 ? 2 : 1;
    }

    @Override
    public boolean isWithinAttackRange(Coordinate coordinate1, Coordinate coordinate2) {
        return coordinate1 != null && coordinate1.isWithinVisibleRange(coordinate2);
    }
}
