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

    private boolean tryCombat(NpcHurtAbility ability) {
        HurtAbility hurtAbility = attacker.findAbility(HurtAbility.class).orElse(null);
        if (hurtAbility == null || !hurtAbility.canBeAttacked())
            return false;
        if (npc().findAbility(AbstractNpcAttackAbility.class).isEmpty())
            return false;
        npc().startAI(CombatAI.hurtAbilityTriggered(npc(), attacker, ability));
        return true;
    }

    private void afterHurtDone(NpcHurtAbility hurtAbility) {
        if (tryEscape(hurtAbility))
            return;
        if (tryCombat(hurtAbility))
            return;
        continueWander(hurtAbility.getInterruptedAbility());
    }

    private void onAttacked(ActiveEntity attacker, NpcHurtAbility ability) {
        applyHurtAbility(ability);
        this.attacker = attacker;
    }

    void onNonDieAbilityDone(NpcAbility doneAbility) {
        if (doneAbility instanceof NpcHurtAbility hurtAbility) {
            afterHurtDone(hurtAbility);
        } else {
            continueWander(doneAbility);
        }
    }

    @Override
    public void update(int delta) {
        updateAbility(delta);
    }

}
