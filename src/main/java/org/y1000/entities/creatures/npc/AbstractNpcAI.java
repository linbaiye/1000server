package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.npc.event.NpcRemoveEvent;
import org.y1000.entities.creatures.npc.event.NpcSnapshot;

public abstract class AbstractNpcAI implements NpcAI {

    private final Npc npc;

    private NpcAnimatedAbility currentAbility;

    protected AbstractNpcAI(Npc npc) {
        this.npc = npc;
    }

    <T extends NpcAnimatedAbility> T changeAbilityOrThrow(Class<T> type) {
        var a = npc.findAbility(type).orElseThrow();
        return changeAbility(a);
    }

    <A extends NpcAnimatedAbility> A changeAbility(A ability) {
        currentAbility = ability;
        npc.findAbilities(NpcAnimatedAwareAbility.class)
                .forEach(npcStateAwareAbility -> npcStateAwareAbility.onStateChanged(npc));
        return ability;
    }


    abstract void onNonDieAbilityDone(NpcAnimatedAbility ability);

    void updateAbility(int delta) {
        if (!currentAbility().update(delta)) {
            return;
        }
        if (currentAbility() instanceof NpcDieAbility) {
            npc.sendEvent(NpcRemoveEvent.of(npc));
        } else {
            onNonDieAbilityDone(currentAbility());
        }
    }

    Npc npc() {
        return npc;
    }

    NpcAnimatedAbility currentAbility() {
        return currentAbility;
    }

    public void instantKill() {
        npc.findAbility(NpcHurtAbility.class).ifPresent(e -> {
            if (!e.isDead()) {
                e.instantKill();
                changeAbilityOrThrow(NpcDieAbility.class).apply(npc());
            }
        });
    }

    void applyHurtAbility(NpcHurtAbility ability) {
        if (currentAbility() instanceof NpcMoveAbility moveAbility) {
            moveAbility.interrupt(npc());
        }
        if (ability.currentLife() <= 0) {
            changeAbilityOrThrow(NpcDieAbility.class)
                    .apply(npc());
        } else {
            ability.apply(npc(), currentAbility());
            changeAbility(ability);
        }
    }

    @Override
    public NpcSnapshot captureSnapshot() {
        return currentAbility.captureSnapshot(npc);
    }
}
