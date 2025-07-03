package org.y1000.entities.creatures.npc;

import org.y1000.entities.AttackableEntity;
import org.y1000.entities.players.Damage;

import java.util.concurrent.ThreadLocalRandom;

public class HurtAbility {

    private final Npc npc;

    private final int armor;

    private final int avoidance;

    private final int hurtSound;

    private final int millis;

    private int life;


    public HurtAbility(Npc npc,
                       int armor,
                       int avoidance,
                       int hurtSound,
                       int life,
                       int millis) {
        this.npc = npc;
        this.armor = armor;
        this.avoidance = avoidance;
        this.hurtSound = hurtSound;
        this.millis = millis;
        this.life = life;
    }
    protected boolean isDodged(int attackerHit) {
        var rand = ThreadLocalRandom.current().nextInt(0, attackerHit + 75 + avoidance);
        return rand < avoidance;
    }

    public void attackedBy(AttackableEntity attacker, Damage damage, int hit) {
        if (isDodged(hit))
            return;
        var before = life;
        damageAction.accept(damage);
        var damagedLife = before - currentLife();
        if (damagedLife > 0) {
            var exp = damagedLifeToExp(damagedLife);
            gainExp.accept(exp);
        }
        npc.changeState();
        npc.findAbility(AttackAbility.class).ifPresent(attackAbility -> attackAbility.setEnemy(attacker));
    }
}
