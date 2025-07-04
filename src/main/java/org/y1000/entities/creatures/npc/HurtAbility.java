package org.y1000.entities.creatures.npc;

import lombok.Getter;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.creatures.monster.NpcActionEnum;
import org.y1000.entities.creatures.npc.event.NpcStartActionEvent;
import org.y1000.entities.players.Damage;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class HurtAbility extends AbstractNpcAction {

    private final int armor;

    private final int avoidance;

    private final int hurtSound;

    private final int animationMillis;

    @Getter
    private int currentLife;

    @Getter
    private final int maxLife;

    private Consumer<? super HurtAbility> onTriggered;


    @Getter
    private ActiveEntity trigger;

    private int elapsed;

    public HurtAbility(int armor,
                       int avoidance,
                       int hurtSound,
                       int life,
                       int millis) {
        this.armor = armor;
        this.avoidance = avoidance;
        this.hurtSound = hurtSound;
        this.animationMillis = millis;
        this.maxLife = life;
        this.currentLife = life;
    }

    protected boolean isDodged(int attackerHit) {
        var rand = ThreadLocalRandom.current().nextInt(0, attackerHit + 75 + avoidance);
        return rand < avoidance;
    }

    public void setTrigger(Consumer<? super HurtAbility> t) {
        this.onTriggered = t;
    }

    @Override
    public NpcActionEnum actionEnum() {
        return NpcActionEnum.Hurt;
    }

    public boolean canBeAttackedNow() {
        return true;
    }


    public void hurt(Npc npc) {
        setTimer(animationMillis);
        npc.sendEvent(NpcStartActionEvent.of(npc, actionEnum()));
    }

    public int attackedBy(ActiveEntity attacker, Damage damage, int hit) {
//        if (currentLife <= 0) {
//            return -1;
//        }
//        elapsed = 0;
//        int damageTaken = -1;
//        if (!isDodged(hit)) {
//            var before = currentLife;
//            currentLife = damage.bodyDamage() - armor;
//            damageTaken = before - currentLife;
//        }
//        trigger = attacker;
        onTriggered.accept(this);
//        return damageTaken;
        return 0;
    }
}
