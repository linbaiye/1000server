package org.y1000.entities.players;

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


    public int computePermitExp(int exp) {
        if (exp <= 0) {
            return this.exp;
        }
        return exp + Math.min(exp, ExperienceUtil.GetLevelMaxExp(level()) * 3);
    }


    public Experience gainPermitExp(int expValue) {
    /*

function  AddPermitExp (var aLevel, aExp: integer; addvalue: integer): integer;
var number : integer;
begin
   number := GetLevelMaxExp (aLevel) * 3;
   if number > addvalue then number := addvalue;
   inc (aExp, number);
   aLevel := GetLevel (aExp);
   Result := number;
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
