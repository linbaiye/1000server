package org.y1000.entities.creatures.npc;

import lombok.Getter;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.creatures.monster.NpcActionEnum;
import org.y1000.entities.creatures.npc.event.NpcStartActionEvent;
import org.y1000.entities.players.Damage;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class HurtAction extends AbstractNpcAction {

    private final int armor;

    private final int avoidance;

    private final int hurtSound;

    private final int animationMillis;

    @Getter
    private int currentLife;

    @Getter
    private final int maxLife;

    private Consumer<? super HurtAction> onTriggered;


    @Getter
    private ActiveEntity trigger;

    @Getter
    private NpcActionEnum previousAction;

    public HurtAction(int armor,
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

    public void setTrigger(Consumer<? super HurtAction> t) {
        this.onTriggered = t;
    }

    @Override
    public NpcActionEnum actionEnum() {
        return NpcActionEnum.Hurt;
    }

    public boolean canBeAttackedNow() {
        return true;
    }

    public void hurt(Npc npc, NpcActionEnum previousAction) {
        setTimer(animationMillis);
        this.previousAction = previousAction;
        npc.sendEvent(NpcStartActionEvent.of(npc, actionEnum()));
    }

    public void hurt(Npc npc, NpcAction actionWhenHurt) {
        setTimer(animationMillis);
        if (actionWhenHurt instanceof HurtAction hurtAbility) {
            this.previousAction = hurtAbility.getPreviousAction();
        } else {
            this.previousAction = actionWhenHurt.actionEnum();
        }
        npc.sendEvent(NpcStartActionEvent.of(npc, actionEnum()));
    }

    public int attackedBy(ActiveEntity attacker, Damage damage, int hit) {
        if (isDodged(hit)) {
            return -1;
        }
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
