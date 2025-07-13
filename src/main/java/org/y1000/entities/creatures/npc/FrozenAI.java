package org.y1000.entities.creatures.npc;

import org.y1000.entities.ActiveEntity;

public class FrozenAI extends AbstractNpcAI {

    public FrozenAI(Npc npc) {
        super(npc);
        npc.findAbility(NpcHurtAbility.class)
                .ifPresent(e -> e.setHurtTrigger(this::onAttacked));
    }

    @Override
    void onNonDieAbilityDone(NpcAbility ability) {
        changeAbilityOrThrow(NpcIdleAbility.class).apply(npc());
    }

    private void onAttacked(ActiveEntity attacker, NpcHurtAbility hurtAbility) {
        applyHurtAbility(hurtAbility);
    }

    @Override
    public void update(int delta) {
        updateAbility(delta);
    }

    @Override
    public void start() {
        changeAbilityOrThrow(NpcIdleAbility.class).apply(npc());
    }
}
