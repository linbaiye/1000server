package org.y1000.kungfu;

import lombok.Builder;
import lombok.experimental.SuperBuilder;
import org.y1000.exp.Experience;

@SuperBuilder
public abstract class AbstractKungFu implements KungFu {

    private int level;

    private final String name;

    @Builder.Default
    private int exp = 0;

    public int level() {
        if (level == 0) {
            level = Experience.computeLevel(exp);
        }
        return level;
    }

    @Override
    public boolean gainExp(int value) {
        if (value <= 0) {
            return false;
        }
        int previous = level();
        exp += Math.min(value, Experience.GetLevelMaxExp(level()) * 3);
        level = 0;
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

    public String name() {
        return name;
    }
}
