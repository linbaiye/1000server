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

    private void afterHurtDone(NpcHurtAbility hurtAbility) {
        if (npc().needToEscape()) {
            EscapeAI newAi = new EscapeAI(npc(), attacker, hurtAbility);
            npc().startAI(newAi);
        } else if (attacker.findAbility(HurtAbility.class).map(HurtAbility::canBeAttacked).orElse(false) &&
                npc().findAbility(NpcMeleeAbility.class).isPresent()) {
                npc().startAI(CombatAI.hurtAbilityTriggered(npc(), attacker, hurtAbility));
        } else {
            continueWander(hurtAbility.getInterruptedAbility());
        }
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
