package org.y1000.entities.creatures.monster;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.creatures.AbstractCreatureState;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.EntitySoundEvent;
import org.y1000.entities.creatures.event.MonsterShootEvent;
import org.y1000.entities.projectile.MonsterProjectile;

@Slf4j
public final class MonsterRangedAttackState extends AbstractCreatureState<AbstractMonster> implements MonsterState<AbstractMonster> {

    private final String sound;

    private final int spriteId;

    private boolean shot;

    public MonsterRangedAttackState(int totalMillis,
                                    String sound,
                                    int spriteId) {
        super(totalMillis);
        this.sound = sound;
        this.spriteId = spriteId;
        shot = false;
    }

    @Override
    public State stateEnum() {
        return State.ATTACK;
    }

    @Override
    public void update(AbstractMonster monster, int delta) {
//        if (elapse(delta)) {
//            monster.AI().onAttackDone(monster);
//            return;
//        }
//        if (elapsedMillis() >= (totalMillis() / 2) && monster.getFightingEntity() != null && !shot) {
//            monster.emitEvent(new MonsterShootEvent(new MonsterProjectile(monster, monster.getFightingEntity(), spriteId)));
//            if (sound != null) {
//                monster.emitEvent(new EntitySoundEvent(monster, sound));
//            }
//            shot = true;
//        }
    }

    @Override
    public void afterHurt(AbstractMonster creature) {
//        creature.AI().onAttackDone(creature);
    }

    public static MonsterRangedAttackState attack(AbstractMonster monster, String sound, int projectId) {
        return new MonsterRangedAttackState(monster.getStateMillis(State.ATTACK), sound, projectId);
    }
}
