package org.y1000.entities.creatures.npc;

import org.y1000.entities.AttackableActiveEntity;
import org.y1000.entities.creatures.IAbstractCreatureState;
import org.y1000.entities.creatures.PlayerStateEnum;
import org.y1000.entities.creatures.event.EntitySoundEvent;
import org.y1000.entities.creatures.event.MonsterShootEvent;
import org.y1000.entities.creatures.monster.NpcStateEnum;
import org.y1000.entities.projectile.NpcProjectile;

public final class NpcRangedAttackState extends IAbstractCreatureState<Npc> implements NpcState {

    private final String sound;

    private final int spriteId;

    private boolean shot;

    private final AttackableActiveEntity target;

    private final ViolentNpc shooter;

    public NpcRangedAttackState(int totalMillis,
                                String sound,
                                int spriteId,
                                AttackableActiveEntity target, ViolentNpc shooter) {
        super(totalMillis);
        this.sound = sound;
        this.spriteId = spriteId;
        this.target = target;
        this.shooter = shooter;
        shot = false;
    }

    @Override
    public void update(Npc npc, int delta) {
        if (elapse(delta)) {
            npc.onActionDone();
            return;
        }
        if (elapsedMillis() >= (totalMillis() / 2) &&  !shot) {
            npc.emitEvent(new MonsterShootEvent(new NpcProjectile(shooter, target, spriteId)));
            if (sound != null) {
                npc.emitEvent(new EntitySoundEvent(npc, sound));
            }
            shot = true;
        }
    }

    @Override
    public NpcStateEnum state() {
        return NpcStateEnum.Attack;
    }

    @Override
    public PlayerStateEnum stateEnum() {
        return PlayerStateEnum.ATTACK;
    }
}
