package org.y1000.entities.creatures.npc;

public class FightAI implements NpcAI {
    private final Npc npc;

    private NpcAction currentAction;

    private int attackCooldown;

    private int recoveryCooldown;


    public FightAI(Npc npc, NpcAction actionWhenGetHurt, HurtAction hurtAbility) {
        this.npc = npc;
        this.currentAction = hurtAbility;
        hurtAbility.hurt(npc, actionWhenGetHurt);
        npc.findAction()
    }



    @Override
    public void update(int delta) {
        if (!currentAction().update(delta))
            return;
        switch (currentAction.actionEnum()) {
            case Hurt ->
        }

    }

    @Override
    public NpcAction currentAction() {
        return currentAction;
    }

    @Override
    public void onAttacked(AttackAction action) {
    }
}
