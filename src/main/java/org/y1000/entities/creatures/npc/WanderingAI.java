package org.y1000.entities.creatures.npc;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.HurtAbility;
import org.y1000.util.Coordinate;


@Slf4j
public final class WanderingAI extends AbstractWanderingAI {

    private ActiveEntity attacker;

    public WanderingAI(Npc npc) {
        super(npc);
        npc.findAbility(NpcHurtAbility.class).ifPresent(h -> h.setHurtTrigger(this::onAttacked));
    }

    @Override
    Coordinate getWanderOrigin() {
        return npc().getSpawnCoordinate();
    }

    private boolean tryEscape(NpcHurtAbility hurtAbility) {
        LifeLowEscapeAbility lifeLowEscapeAbility = npc().findAbility(LifeLowEscapeAbility.class).orElse(null);
        if (lifeLowEscapeAbility == null || !lifeLowEscapeAbility.shouldEscape(npc()))
            return false;
        EscapeAI newAi = new EscapeAI(npc(), attacker, hurtAbility, lifeLowEscapeAbility);
        npc().startAI(newAi);
        return true;
    }

    private boolean tryCombat(ActiveEntity enemy) {
        HurtAbility hurtAbility = enemy.findAbility(HurtAbility.class).orElse(null);
        if (hurtAbility == null || !hurtAbility.canBeAttacked())
            return false;
        if (npc().findAbility(AbstractNpcAttackAbility.class).isEmpty())
            return false;
        npc().startAI(new CombatAI(npc(), enemy, currentAbility()));
        return true;
    }


    private void afterHurtDone(NpcHurtAbility hurtAbility) {
        if (tryEscape(hurtAbility))
            return;
        if (tryCombat(attacker))
            return;
        continueWander(hurtAbility.getInterruptedAbility());
    }

    private void onAttacked(ActiveEntity attacker, NpcHurtAbility ability) {
        applyHurtAbility(ability);
        this.attacker = attacker;
    }

    private boolean tryGuard() {
        NpcProtectAbility guardAbility = npc().findAbility(NpcProtectAbility.class).orElse(null);
        if (guardAbility == null)
            return false;
        return guardAbility.findEnemy(npc()).map(this::tryCombat).orElse(false);
    }

    private boolean tryFindPlayerToAttack() {
        EngageAlivePlayerAbility ability = npc().findAbility(EngageAlivePlayerAbility.class).orElse(null);
        if (ability == null)
            return false;
        return ability.find(npc()).map(this::tryCombat).orElse(false);
    }

    void onNonDieAbilityDone(NpcUpdatableAbility doneAbility) {
        if (doneAbility instanceof NpcHurtAbility npcHurtAbility) {
            afterHurtDone(npcHurtAbility);
            return;
        }
        if (tryFindPlayerToAttack()) {
            return;
        }
        if (!tryGuard()) {
            continueWander(doneAbility);
        }
    }

    @Override
    public void update(int delta) {
        updateAbility(delta);
    }

}
