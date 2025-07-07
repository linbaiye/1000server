package org.y1000.entities.creatures.npc;

import org.y1000.entities.ActiveEntity;
import org.y1000.entities.players.Player;

public class CombatAI implements NpcAI {
    private final Npc npc;

    private NpcAction currentAction;

    private ActiveEntity enemy;

    private final NpcAttackAbility attackAbility;


    public CombatAI(Npc npc, NpcAction actionWhenGetHurt, ActiveEntity enemy) {
        this.npc = npc;
        this.enemy = enemy;
        var action = npc.findAction(HurtAction.class).orElseThrow();
        currentAction = action;
        action.hurt(npc, actionWhenGetHurt);
        NpcHurtAbility hurtAbility = npc.findAbility(NpcHurtAbility.class).orElseThrow();
        hurtAbility.setHurtTrigger(this::onAttacked);
        attackAbility = npc.findAbility(NpcAttackAbility.class).orElseThrow();
    }


    private void doAttack() {
        if (enemy instanceof Player player) {
            player.attacked(attackAbility.damage(), attackAbility.accuracy());
            attackAbility.cooldownAttack();
        }
    }

    @Override
    public void update(int delta) {
        attackAbility.cooldown(delta);
        if (attackAbility.ableToAttack() && enemy.coordinate().directDistance(npc.coordinate()) <= 2) {
            doAttack();
        }
        if (!currentAction().update(delta))
            return;
        switch (currentAction.actionEnum()) {

        }
    }

    @Override
    public NpcAction currentAction() {
        return currentAction;
    }

    public void onAttacked(ActiveEntity attacker, NpcHurtAbility ability) {

    }
}
