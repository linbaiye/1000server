package org.y1000.entities.creatures.npc.AI;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.AttackableActiveEntity;
import org.y1000.entities.creatures.event.SeekAggressiveMonsterEvent;
import org.y1000.entities.creatures.monster.AggressiveMonster;
import org.y1000.entities.creatures.npc.Guardian;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.creatures.npc.NpcHurtState;
import org.y1000.util.Coordinate;


@Slf4j
public final class GuardWanderingAI extends AbstractWanderingNpcAI {

    public GuardWanderingAI(Coordinate dest) {
        this(dest, null);
    }

    private final Chatter chatter;

    public GuardWanderingAI(Coordinate dest, Chatter chatter) {
        super(dest, Coordinate.Empty);
        this.chatter = chatter;
    }

    private void changeToFight(Guardian guardian, AttackableActiveEntity enemy) {
        guardian.changeAndStartAI(new ViolentNpcMeleeFightAI(enemy, guardian, 1, chatter));
    }

    @Override
    protected void onHurtDone(Npc npc) {
        if (!(npc instanceof Guardian guardian)) {
            return;
        }
        if (npc.state() instanceof NpcHurtState hurtState) {
            changeToFight(guardian, hurtState.attacker());
        }
    }

    @Override
    public void onActionDone(Npc npc) {
        if ((npc instanceof Guardian guardian)) {
            SeekAggressiveMonsterEvent event = new SeekAggressiveMonsterEvent(guardian, guardian.getWidth());
            guardian.emitEvent(event);
            AggressiveMonster aggressiveMonster = event.getMonsters().stream().findFirst().orElse(null);
            if (aggressiveMonster != null) {
                changeToFight(guardian, aggressiveMonster);
                return;
            }
        }
        if (chatter != null)
            chatter.onActionDone(npc);
        defaultActionDone(npc);
    }
}
