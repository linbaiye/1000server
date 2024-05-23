package org.y1000.entities.item;

import lombok.Builder;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.kungfu.attack.AttackKungFuType;

public final class Weapon extends AbstractItem {

    private final AttackKungFuType attackKungFuType;

    @Builder
    public Weapon(long id, String name, ItemType type, AttackKungFuType attackKungFuType) {
        super(id, name, type);
        this.attackKungFuType = attackKungFuType;
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
