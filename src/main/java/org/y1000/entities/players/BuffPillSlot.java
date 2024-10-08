package org.y1000.entities.players;

import org.y1000.item.BuffPill;

final class BuffPillSlot {

    private int last;

    private BuffPill buffPill;

    public boolean canTake() {
        return this.buffPill == null;
    }

    public void take(BuffPill buffPill) {
        if (!canTake() || buffPill == null)
            return;
        this.buffPill = buffPill;
        last = buffPill.getDuration();
    }

    public void update(int delta) {
        if (last > 0)
            last -= delta;
    }

    public void cancel() {
        buffPill = null;
        last = 0;
    }

    public Damage apply(Damage damage) {
        if (damage == null)
            return null;
        return isEffective() ? damage.addNoNegative(buffPill.getDamage()) : damage;
    }

    public boolean isEffective() {
        return buffPill != null && last > 0;
    }

}
