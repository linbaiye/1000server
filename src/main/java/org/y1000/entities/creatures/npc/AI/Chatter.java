package org.y1000.entities.creatures.npc.AI;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.npc.HumanNpc;
import org.y1000.entities.creatures.npc.Npc;

import java.util.List;

public final class Chatter {

    private int counter;

    private final List<String> dialogs;

    public Chatter(List<String> dialogs) {
        Validate.isTrue(dialogs != null && !dialogs.isEmpty());
        this.dialogs = dialogs;
        counter = 0;
    }

    public void onActionDone(Npc npc) {
        if (!(npc instanceof HumanNpc humanNpc) || npc.stateEnum() == State.DIE)
            return;
        ++counter;
        if (counter % 10 != 0)
            return;
        int index = counter / 10 - 1;
        humanNpc.say(dialogs.get(index));
        if (index == dialogs.size() - 1)
            counter = 0;
    }
}
