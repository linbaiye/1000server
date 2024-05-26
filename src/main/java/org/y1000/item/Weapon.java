package org.y1000.item;

import lombok.Builder;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.kungfu.attack.AttackKungFuType;

public final class Weapon extends AbstractEquipment {

    private final AttackKungFuType attackKungFuType;

    @Builder
    public Weapon(String name, AttackKungFuType attackKungFuType) {
        super(name);
        this.attackKungFuType = attackKungFuType;
    }

    public AttackKungFuType kungFuType() {
        return attackKungFuType;
    }

    @Override
    public void doubleClicked(Player player) {
        if (player.stateEnum() == State.DIE) {
            return;
        }
        player.equipWeapon(this);
    }
}
