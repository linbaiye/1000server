package org.y1000.entities.creatures.monster;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.creatures.event.EntitySoundEvent;
import org.y1000.entities.creatures.event.SeekPlayerEvent;
import org.y1000.entities.creatures.npc.AggressiveNpc;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.creatures.npc.NpcAI;
import org.y1000.entities.creatures.npc.ViolentNpcWanderingAI;

import java.util.concurrent.ThreadLocalRandom;


public final class MonsterWanderingAI implements NpcAI {

    private final ViolentNpcWanderingAI wrappedAi;

    private int counter;

    private void resetCounter() {
        counter = ThreadLocalRandom.current().nextInt(50, 100);
    }

    public MonsterWanderingAI(ViolentNpcWanderingAI wrappedAi) {
        this.wrappedAi = wrappedAi;
        resetCounter();
    }

    public MonsterWanderingAI() {
        this(new ViolentNpcWanderingAI());
    }


    @Override
    public void onActionDone(Npc npc) {
        Validate.notNull(npc);
        if (npc instanceof AggressiveNpc aggressiveNpc) {
            aggressiveNpc.emitEvent(new SeekPlayerEvent(aggressiveNpc));
        }
        wrappedAi.onActionDone(npc);
        if (--counter <= 0 && npc instanceof Monster monster) {
            resetCounter();
            monster.normalSound().ifPresent(s -> npc.emitEvent(new EntitySoundEvent(npc, s)));
        }
    }

    @Override
    public void onMoveFailed(Npc npc) {
        wrappedAi.onMoveFailed(npc);
    }

    @Override
    public void start(Npc npc) {
        wrappedAi.start(npc);
    }

}
