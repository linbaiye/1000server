package org.y1000.entities.creatures.npc;


import org.y1000.entities.creatures.monster.NpcActionEnum;

public class DieAbility implements NpcAction {

    @Override
    public boolean update(int delta) {
        return false;
    }

    @Override
    public int elapsedMillis() {
        return 0;
    }

    @Override
    public NpcActionEnum actionEnum() {
        return null;
    }

    public void die(Npc npc) {

    }

}
