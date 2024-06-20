package org.y1000.kungfu;

import org.y1000.exp.Experience;
import org.y1000.exp.ExperienceUtil;

public abstract class AbstractKungFu implements KungFu {

    private final String name;

    private Experience experience;

    protected AbstractKungFu(String name, int exp) {
        this.name = name;
        experience = new Experience(exp);
    }

    public int level() {
        return experience.level();
    }

    @Override
    public boolean gainExp(int expValue) {
        var old = experience;
       experience = experience.gainPermitExp(expValue);
       return old.level() != experience.level();
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
}
