package org.y1000.entities.creatures.npc.AI;

import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.EntitySoundEvent;
import org.y1000.entities.creatures.monster.Monster;

import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractMonsterWanderingAI extends AbstractWanderingAI<Monster> {
    private int soundCounter;

    public AbstractMonsterWanderingAI() {
        setSoundCounter();
    }

    private void setSoundCounter() {
        soundCounter = ThreadLocalRandom.current().nextInt(10, 50);
    }

    protected abstract void onMonsterActionDone(Monster monster);

    @Override
    protected Class<Monster> npcType() {
        return Monster.class;
    }

    @Override
    protected void onActionDoneNotDead(Monster monster) {
        if (soundCounter-- <= 0 &&
                (monster.stateEnum() == State.IDLE || monster.stateEnum() == State.WALK)) {
            monster.normalSound().ifPresent(s -> monster.emitEvent(new EntitySoundEvent(monster, s)));
            setSoundCounter();
        }
        onMonsterActionDone(monster);
    }
}
