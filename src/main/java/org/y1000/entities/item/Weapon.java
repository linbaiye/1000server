package org.y1000.entities.item;

import lombok.Builder;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.kungfu.attack.AttackKungFuType;

@Builder
public final class Weapon implements Item {

    private final long id;

    private final String name;

    private final AttackKungFuType attackKungFuType;

    @Override
    public long id() {
        return id;
    }

    @Override
    public String name() {
        return name;
    }

    public AttackKungFuType kungFuType() {
        return attackKungFuType;
    }

    @Override
    public ItemType type() {
        return ItemType.WEAPON;
    }

    @Override
    public void doubleClicked(Player player) {
        if (player.stateEnum() == State.DIE) {
            return;
        }
        player.equipWeapon(this);
    }

    public boolean isRanged () {
        return attackKungFuType == AttackKungFuType.BOW ||
               attackKungFuType == AttackKungFuType.THROW;
    }
}
