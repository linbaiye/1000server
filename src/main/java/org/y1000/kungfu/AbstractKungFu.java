package org.y1000.kungfu;

import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.PlayerGainExpEvent;
import org.y1000.exp.Experience;
import org.y1000.exp.ExperienceUtil;

public abstract class AbstractKungFu implements KungFu {

    private final String name;

    private Experience experience;
    private final int icon;

    protected AbstractKungFu(String name, int exp) {
        this.name = name;
        experience = new Experience(exp);
        icon = 0;
    }

    protected AbstractKungFu(String name, int exp, int icon) {
        this.name = name;
        experience = new Experience(exp);
        this.icon = icon;
    }

    public int level() {
        return experience.level();
    }

    @Override
    public int exp() {
        return experience.value();
    }

    @Override
    public boolean gainPermittedExp(int expValue) {
        var old = experience.level();
        var exp = experience.computePermitExp(expValue);
        experience = experience.gainExp(exp * 20000);
        return old != experience.level();
    }

    @Override
    public boolean isLevelFull() {
        return experience.level() == 9999;
    }

    private int getPermitExp(int value) {
        /*
        function  GetPermitExp (aLevel, addvalue: integer): integer;
var n : integer;
begin
   n := GetLevelMaxExp (aLevel);
   if n > addvalue then n := addvalue;
   Result := n;
end;
         */
        int n = ExperienceUtil.GetLevelMaxExp(level());
        return Math.min(n, value);
    }

    public String name() {
        return name;
    }

    @Override
    public int icon() {
        return icon;
    }

    protected StringBuilder getDescriptionBuilder() {
        var str = String.format("修炼等级: %d.%02d", level() / 100, level() % 100);
        return new StringBuilder(str).append("\n");
    }

    @Override
    public void gainExp(Player player, int exp) {
        if (exp <= 0 || isLevelFull())
            return;
        if (!gainPermittedExp(exp)) {
            return;
        }
        player.sendEvent(PlayerGainExpEvent.of(player, this));
//        if (isLevelFull())
//            player.sendEvent();
    }
}
