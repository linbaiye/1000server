package org.y1000.kungfu.attack;

import static org.y1000.kungfu.ParameterConstants.INI_MAGIC_DIV_VALUE;

public abstract class AbstractAttackKungFuParameters implements AttackKungFuParameters {

    protected static final int INI_ADD_DAMAGE       = 40;
    protected static final int INI_MUL_ATTACKSPEED  = 10;
    protected static final int INI_MUL_AVOID          = 6;
    protected static final int INI_MUL_RECOVERY        = 10;
    protected static final int INI_MUL_DAMAGEBODY      = 23;
    protected static final int INI_MUL_DAMAGEHEAD      = 17;
    protected static final int INI_MUL_DAMAGEARM       = 17;
    protected static final int INI_MUL_DAMAGELEG       = 17;

    protected int valueOrZero(int v, int mul) {
        return v == 0 ? 0 :
                (v + INI_ADD_DAMAGE ) * mul/ INI_MAGIC_DIV_VALUE;
    }
}
