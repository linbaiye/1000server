package org.y1000.kungfu;

import org.y1000.exp.Experience;

public abstract class AbstractKungFu implements KungFu {

    private int level;

    private final String name;

    private int exp;

    protected AbstractKungFu(String name, int exp) {
        this.name = name;
        this.exp = exp;
    }

    public int level() {
        if (level == 0) {
            level = Experience.computeLevel(exp);
        }
        return level;
    }

    @Override
    public boolean gainExp(int expValue) {
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
            return false;
        }
        int previous = level();
        exp += Math.min(expValue, Experience.GetLevelMaxExp(level()) * 3);
        level = Experience.computeLevel(exp);
        return level() != previous;
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
        int n = Experience.GetLevelMaxExp(level());
        return Math.min(n, value);
    }

    public String name() {
        return name;
    }

    public int exp() {
        return exp;
    }
}
