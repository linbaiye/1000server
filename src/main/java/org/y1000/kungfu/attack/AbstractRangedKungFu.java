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

    /*public Direction computeDirection() {
        public static int Wrap(int value, int min, int max)
    {
      int num = max - min;
      return num == 0 ? min : min + ((value - min) % num + num) % num;
    }

        public static float Snapped(float s, float step)
    {
      return (double) step != 0.0 ? MathF.Floor((float) ((double) s / (double) step + 0.5)) * step : s;
    }



        Math.atan2(y, x)
        var angle = Mathf.Snapped(vector.Angle(), Math.PI / 4) / (Math.PI / 4);
        int dir = Mathf.Wrap((int)angle, 0, 8);
        return dir switch
        {
            0 => CreatureDirection.Right,
                1 => CreatureDirection.DownRight,
                2 => CreatureDirection.Down,
                3 => CreatureDirection.DownLeft,
                4 => CreatureDirection.Left,
                5 => CreatureDirection.UpLeft,
                6 => CreatureDirection.Up,
                7 => CreatureDirection.UpRight,
                _ => CreatureDirection.Right,
        };
    }*/

//    @Override
//    public void startAttack(PlayerImpl player, ClientAttackEvent event, AttackableEntity target) {
//        count = level() / 2000 + 2;
//        doStartAttack(player, event, target);
//    }

    @Override
    public boolean isWithinAttackRange(Coordinate coordinate1, Coordinate coordinate2) {
        return coordinate1 != null && coordinate1.isWithinVisibleRange(coordinate2);
    }
}
