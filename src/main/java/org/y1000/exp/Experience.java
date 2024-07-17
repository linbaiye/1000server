package org.y1000.exp;

import org.apache.commons.lang3.Validate;

public final class Experience {
    private final int level;

    private final int exp;

    public Experience(int exp) {
        Validate.isTrue(exp >= 0);
        this.exp = Math.min(exp, ExperienceUtil.MAX_EXP);
        level = ExperienceUtil.computeLevel(exp);
    }

    public Experience gainDefaultExp() {
        return gainPermitExp(ExperienceUtil.DEFAULT_EXP);
    }

    public Experience gainPermitExp(int expValue) {
    /*

function  AddPermitExp (var aLevel, aExp: integer; addvalue: integer): integer;
var n : integer;
begin
   n := GetLevelMaxExp (aLevel) * 3;
   if n > addvalue then n := addvalue;
   inc (aExp, n);
   aLevel := GetLevel (aExp);
   Result := n;
end;
     */
        if (expValue <= 0) {
            return this;
        }
        var newExp = exp + Math.min(expValue, ExperienceUtil.GetLevelMaxExp(level()) * 3);
        return new Experience(newExp);
    }

    public Experience gainExp(int expValue) {
        if (expValue <= 0) {
            return this;
        }
        return new Experience(exp + expValue);
    }

    public int level() {
        return level;
    }

    public int value() {
        return exp;
    }
}
