package org.y1000.entities.creatures.npc;

import org.y1000.entities.AttackableEntity;

public class HurtAbility {

    private final Npc npc;

    public HurtAbility(Npc npc) {
        this.npc = npc;
    }

    public void attackedBy(AttackableEntity attacker) {
        npc.changeState(new NpcHurtState());
        npc.findAbility(AttackAbility.class).ifPresent(attackAbility -> attackAbility.setEnemy(attacker));
    }
}
